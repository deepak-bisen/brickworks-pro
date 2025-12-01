document.addEventListener('DOMContentLoaded', () => {

    // --- Configuration ---
    const API_URL = "http://localhost:9191";

    // --- Admin Contact Messages: functions & modal handlers ---
    // Safe element references (may be null if admin panel not present)
    const adminMessagesTbody = document.getElementById('admin-messages-table-body');
    const viewMessageModal = document.getElementById('view-message-modal');
    const viewMessageClose = document.getElementById('view-message-close');
    const viewMessageDelete = document.getElementById('view-message-delete');
    const viewMessageMarkRead = document.getElementById('view-message-markread');
    const viewMessageFrom = document.getElementById('view-message-from');
    const viewMessageReceived = document.getElementById('view-message-received');
    const viewMessageBody = document.getElementById('view-message-body');
    let _currentMessageId = null;

    // Mark-read always uses backend; local simulation removed
    // Keep last fetched messages in memory for re-rendering and updates
    let lastFetchedMessages = [];
    

    // Fetch and render admin messages
    window.fetchAdminMessages = async function () {
        if (!adminMessagesTbody) return;
        adminMessagesTbody.innerHTML = '<tr><td colspan="5" class="p-4 text-center">Loading...</td></tr>';
        try {
            const res = await fetch(`${API_URL}/api/v1/contact`, { headers: getAuthHeader() });
            if (handleAuthError(res)) return;
            if (!res.ok) throw new Error('Failed to fetch messages');

            const msgs = await res.json();
            if (!Array.isArray(msgs) || msgs.length === 0) {
                adminMessagesTbody.innerHTML = '<tr><td colspan="5" class="p-4 text-center text-gray-500">No messages found.</td></tr>';
                lastFetchedMessages = [];
                return;
            }

            // Store a copy for re-rendering and local UI updates
            lastFetchedMessages = msgs.map(m => ({ ...m }));
            // No client-side flags applied — message status comes from the backend

            renderAdminMessages(lastFetchedMessages);

        } catch (err) {
            console.error('Fetch messages error:', err);
            adminMessagesTbody.innerHTML = '<tr><td colspan="5" class="p-4 text-center text-red-500">Failed to load messages.</td></tr>';
        }
    };

    // Render messages array into the table body
    function renderAdminMessages(msgArray) {
        if (!adminMessagesTbody) return;
        adminMessagesTbody.innerHTML = '';
        msgArray.forEach(m => {
            const tr = document.createElement('tr');
            tr.className = 'hover:bg-gray-50';

            const receivedTd = document.createElement('td');
            receivedTd.className = 'px-4 py-2 text-sm text-gray-500 whitespace-nowrap';
            const receivedDate = m.receivedAt ? new Date(m.receivedAt) : new Date();
            receivedTd.innerHTML = `${receivedDate.toLocaleDateString()}<br><span class="text-xs">${receivedDate.toLocaleTimeString()}</span>`;

            const fromTd = document.createElement('td');
            fromTd.className = 'px-4 py-2';
            const nameDiv = document.createElement('div'); nameDiv.className = 'text-sm font-medium text-gray-900'; nameDiv.textContent = m.name || '';
            const emailDiv = document.createElement('div'); emailDiv.className = 'text-sm text-gray-500'; emailDiv.textContent = m.email || '';
            fromTd.appendChild(nameDiv); fromTd.appendChild(emailDiv);

            const msgTd = document.createElement('td');
            msgTd.className = 'px-4 py-2 text-sm text-gray-700 max-w-xs break-words';
            msgTd.textContent = m.message || '';

            const statusTd = document.createElement('td');
            statusTd.className = 'px-4 py-2';
            const statusSpan = document.createElement('span');
            const statusText = (m.status || 'NEW').toUpperCase();
            statusSpan.className = statusText === 'READ' ? 'px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-gray-100 text-gray-800' : 'px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800';
            statusSpan.textContent = statusText;
            statusTd.appendChild(statusSpan);

            const actionsTd = document.createElement('td');
            actionsTd.className = 'px-4 py-2';

            if (statusText !== 'READ') {
                const markBtn = document.createElement('button');
                markBtn.className = 'bg-indigo-600 text-white px-3 py-1 rounded-md mr-2 text-sm';
                markBtn.textContent = 'Mark Read';
                markBtn.addEventListener('click', async () => {
                    if (!confirm('Mark this message as read?')) return;
                    await markMessageRead(m.id || m.messageId || m.contactId);
                });
                actionsTd.appendChild(markBtn);
            }

            const viewBtn = document.createElement('button');
            viewBtn.className = 'bg-blue-600 text-white px-3 py-1 rounded-md mr-2 text-sm';
            viewBtn.textContent = 'View';
            viewBtn.addEventListener('click', () => openMessageModal(m));
            actionsTd.appendChild(viewBtn);

            const delBtn = document.createElement('button');
            delBtn.className = 'bg-red-600 text-white px-3 py-1 rounded-md text-sm';
            delBtn.textContent = 'Delete';
            delBtn.addEventListener('click', async () => {
                if (!confirm('Delete this message? This action cannot be undone.')) return;
                await deleteMessage(m.id || m.messageId || m.contactId);
            });
            actionsTd.appendChild(delBtn);

            tr.appendChild(receivedTd);
            tr.appendChild(fromTd);
            tr.appendChild(msgTd);
            tr.appendChild(statusTd);
            tr.appendChild(actionsTd);

            adminMessagesTbody.appendChild(tr);
        });
    }

    function openMessageModal(msg) {
        _currentMessageId = msg.id || msg.messageId || msg.contactId;
        if (viewMessageFrom) viewMessageFrom.textContent = `${msg.name || ''} <${msg.email || ''}>`;
        if (viewMessageReceived) viewMessageReceived.textContent = msg.receivedAt ? new Date(msg.receivedAt).toLocaleString() : '';
        if (viewMessageBody) viewMessageBody.textContent = msg.message || '';
        if (viewMessageModal) {
            viewMessageModal.classList.remove('opacity-0', 'pointer-events-none');
            const mc = viewMessageModal.querySelector('.modal-content'); if (mc) mc.classList.remove('scale-95');
        }
    }

    function closeMessageModal() {
        _currentMessageId = null;
        if (viewMessageModal) {
            viewMessageModal.classList.add('opacity-0', 'pointer-events-none');
            const mc = viewMessageModal.querySelector('.modal-content'); if (mc) mc.classList.add('scale-95');
        }
    }

    viewMessageClose && viewMessageClose.addEventListener('click', closeMessageModal);
    viewMessageDelete && viewMessageDelete.addEventListener('click', async () => {
        if (!_currentMessageId) return; if (!confirm('Delete this message?')) return;
        await deleteMessage(_currentMessageId); closeMessageModal();
    });
    viewMessageMarkRead && viewMessageMarkRead.addEventListener('click', async () => {
        if (!_currentMessageId) return; if (!confirm('Mark this message as read?')) return;
        await markMessageRead(_currentMessageId); closeMessageModal();
    });

    
    async function deleteMessage(id) {
        // Normalize id
        const sid = String(id);

        // Ensure admin token is present: require login to perform backend deletion
        if (!getAuthToken()) {
            showStatus('Please log in as admin to delete messages.', true);
            return;
        }

        // Always call the backend
        try {
            const res = await fetch(`${API_URL}/api/v1/contact/${id}`, 
                { method: 'DELETE', headers: getAuthHeader() });
            if (handleAuthError(res)) return; if (!res.ok) throw new Error('Delete failed');
            showStatus('Message deleted.');
            // No persisted flags to clear
            if (typeof window.fetchAdminMessages === 'function') window.fetchAdminMessages();
        } catch (err) { console.error(err); showStatus(err.message || 'Delete failed', true); }
    }

    async function markMessageRead(id) {
        // Always call the backend to mark message as read

        try {
            const res = await fetch(`${API_URL}/api/v1/contact/${id}`, { method: 'PATCH', headers: getAuthHeader(), body: JSON.stringify({ status: 'READ' }) });
            if (handleAuthError(res)) return; if (!res.ok) {
                let msg = 'Failed to mark read'; try { const d = await res.json(); msg = d.message || msg; } catch (e) { }
                throw new Error(msg);
            }
            showStatus('Message marked as read.');
            if (typeof window.fetchAdminMessages === 'function') window.fetchAdminMessages();
        } catch (err) { console.error(err); showStatus(err.message || 'Mark read failed', true); }
    }

    // --- Page Elements ---
    const pageSections = document.querySelectorAll('.page-section');
    const navLinks = document.querySelectorAll('.nav-link');
    const mobileMenuButton = document.getElementById('mobile-menu-button');
    const mobileMenu = document.getElementById('mobile-menu');
    const statusMessage = document.getElementById('status-message');

    // --- Public Page Elements ---
    const productGrid = document.getElementById('product-grid');
    const quoteProductSelect = document.getElementById('quote-product');

    // --- NEW: Auth State Elements ---
    const adminLoginLink = document.getElementById('admin-login-link');
    const adminLoginLinkMobile = document.getElementById('admin-login-link-mobile');
    const adminDashboardLink = document.getElementById('admin-dashboard-link');
    const adminDashboardLinkMobile = document.getElementById('admin-dashboard-link-mobile');
    const logoutLink = document.getElementById('logout-link');
    const logoutLinkMobile = document.getElementById('logout-link-mobile');

    // --- NEW: Admin Dashboard Elements ---
    const adminTabs = document.querySelectorAll('.admin-tab-link');
    const adminPanels = document.querySelectorAll('.admin-panel');
    const adminProductTableBody = document.getElementById('admin-product-table-body');
    const adminAddForm = document.getElementById('admin-add-form');
    const adminEditModal = document.getElementById('edit-modal');
    const adminEditForm = document.getElementById('admin-edit-form');
    const editModalClose = document.getElementById('edit-modal-close');
    const editModalCancel = document.getElementById('edit-modal-cancel');

    // --- Global State ---
    let allProducts = [];
    let currentEditProductId = null;

    // ==================================================================
    // UTILITY FUNCTIONS
    // ==================================================================

    // --- Show/Hide Page Sections ---
    function showPage(pageId) {
        pageSections.forEach(section => { section.hidden = true; });
        const activePage = document.getElementById(pageId + '-page');
        if (activePage) { activePage.hidden = false; }
        mobileMenu.classList.add('hidden');
        // If admin dashboard is shown and we're logged in, load admin products
        if (pageId === 'admin-dashboard' && getAuthToken && getAuthToken()) {
            if (typeof fetchAdminProducts === 'function') fetchAdminProducts();
        }
    }


    /// --- Show Status Message ---
    function showStatus(message, isError = false) {
        statusMessage.textContent = message;
        statusMessage.classList.remove(isError ? 'bg-green-500' : 'bg-red-500');
        statusMessage.classList.add(isError ? 'bg-red-500' : 'bg-green-500');
        statusMessage.classList.remove('hidden');
        statusMessage.classList.add('opacity-100');

        setTimeout(() => {
            statusMessage.classList.remove('opacity-100');
            statusMessage.classList.add('hidden');
        }, 3000);
    }

    // --- Get Auth Token ---
    // --- FIX: ADDED getAuthToken FUNCTION ---
    function getAuthToken() {
        return localStorage.getItem('adminToken');
    }

    // --- Get Auth Header ---
    function getAuthHeader() {
        const token = localStorage.getItem('adminToken');
        return token ? { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' } : { 'Content-Type': 'application/json' };
    }

    // --- Handle Auth Error (e.g., 401/403) ---
    function handleAuthError(response) {
        if (response.status === 401 || response.status === 403) {
            showStatus("Session expired or invalid. Please log in again.", true);
            logout();
            return true;
        }
        return false;
    }


    // ==================================================================
    // NAVIGATION & AUTHENTICATION
    // ==================================================================

    // --- Page Navigation Link Listeners ---
    navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const pageId = link.getAttribute('data-page');
            showPage(pageId);
        });
    });

    mobileMenuButton.addEventListener('click', () => {
        mobileMenu.classList.toggle('hidden');
    });

    // --- Show UI for Logged-In State ---
    function showLoggedInUI() {
        adminLoginLink.hidden = true;
        adminLoginLinkMobile.hidden = true;
        adminDashboardLink.hidden = false;
        adminDashboardLinkMobile.hidden = false;
        logoutLink.hidden = false;
        logoutLinkMobile.hidden = false;
    }

    // --- Show UI for Logged-Out State ---
    function showLoggedOutUI() {
        adminLoginLink.hidden = false;
        adminLoginLinkMobile.hidden = false;
        adminDashboardLink.hidden = true;
        adminDashboardLinkMobile.hidden = true;
        logoutLink.hidden = true;
        logoutLinkMobile.hidden = true;
    }

    // --- Check Login State on Page Load ---
    function checkLoginState() {
        const token = localStorage.getItem('adminToken');
        if (token) {
            showLoggedInUI();
        } else {
            showLoggedOutUI();
        }
    }

    // --- Logout Function ---
    function logout() {
        localStorage.removeItem('adminToken');
        showLoggedOutUI();
        showPage('home');
        showStatus("You have been logged out.");
    }
    logoutLink.addEventListener('click', (e) => { e.preventDefault(); logout(); });
    logoutLinkMobile.addEventListener('click', (e) => { e.preventDefault(); logout(); });

    // --- Admin Login Form ---
    document.getElementById('login-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const loginButton = document.getElementById('login-submit-button');
        loginButton.disabled = true;
        loginButton.textContent = "Logging in...";

        const username = document.getElementById('login-username').value;
        const password = document.getElementById('login-password').value;

        try {
            const response = await fetch(`${API_URL}/api/auth/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });
            if (!response.ok) { throw new Error('Incorrect username or password'); }
            const data = await response.json();

            localStorage.setItem('adminToken', data.jwt); // Store the token
            showStatus("Admin login successful!");
            document.getElementById('login-form').reset();

            showLoggedInUI(); // Update nav
            showPage('admin-dashboard'); // Show dashboard
            fetchAdminProducts(); // Load product data for the dashboard

        } catch (error) {
            console.error('Login failed:', error);
            showStatus(error.message, true);
        } finally {
            loginButton.disabled = false;
            loginButton.textContent = "Login";
        }
    });


    // ==================================================================
    // PUBLIC-FACING PAGES
    // ==================================================================
    // --- Product Image Helper ---
    function getProductImage(product) {
        // 1. Check for Binary Image Data (New System)
        if (product.imageData) {
            // Ensure product.imageType exists, default to png if missing
            const mimeType = product.imageType || 'image/jpeg';
            return `data:${mimeType};base64,${product.imageData}`;
        }

        // 2. Check for Legacy Image URL (Old System / Database URL)
        // Ensure we don't double-prefix if the URL is already a data URI
        if (product.imageUrl && !product.imageUrl.startsWith('data:')) {
            return product.imageUrl;
        }

        // 3. Fallback: Match by Name (For existing products without data or URL)
        const name = product.name ? product.name.toLowerCase() : "";
        if (name.includes("red brick")) return "./images/red-brick.jpg";
        if (name.includes("cement brick")) return "./images/cement-brick.jpg";
        if (name.includes("gattu") || name.includes("paver")) return "./images/gattu.jpg";
        if (name.includes("multicolor")) return "./images/multicolor.jpeg";

        // 4. Ultimate Fallback: Placeholder
        return `https://placehold.co/600x400/993333/FFFFFF?text=${encodeURIComponent(product.name || "Product")}`;
    }

    // --- Public Product Loading ---
    async function fetchProducts() {
        try {
            const response = await fetch(`${API_URL}/api/products`);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            allProducts = await response.json();
            renderProducts(allProducts);
            populateQuoteSelect(allProducts);
        } catch (error) {
            console.error("Failed to fetch products:", error);
            productGrid.innerHTML = `<p class="col-span-full text-center text-red-500">Error loading products. Please make sure the backend services are running.</p>`;
        }
    }

    function renderProducts(products) {
        productGrid.innerHTML = '';
        if (products.length === 0) {
            productGrid.innerHTML = `<p class="col-span-full text-center">No products available at this time.</p>`;
            return;
        }

        products.forEach(product => {
            const productCard = document.createElement('div');
            productCard.className = 'bg-white shadow-lg rounded-lg overflow-hidden transition-transform duration-300 hover:scale-105';

            // Use new helper function for image (pass the full product object)
            const imageUrl = getProductImage(product);

            const title = document.createElement('h3');
            title.className = 'text-2xl font-bold mb-2';
            title.textContent = product.name || 'Unnamed product';
            const price = document.createElement('p');
            price.className = 'text-gray-700 mb-4';
            price.innerHTML = `<span class="font-semibold">Price:</span> ₹${(product.unitPrice ?? 0).toFixed(2)}</small> (per unit)`;

            productCard.innerHTML = `
                        <div class="bg-gray-200">
                            <img src="${imageUrl}" alt="${product.name}" class="w-full h-64 object-cover" onerror="this.src='https://placehold.co/600x400/cccccc/FFFFFF?text=Image+Not+Found'">
                        </div>
                        <div class="p-6">
                            ${title.outerHTML}
                            <p class="text-gray-700 mb-1"><span class="font-semibold">Type:</span> ${product.brickType}</p>
                            <p class="text-gray-700 mb-1"><span class="font-semibold">Color:</span> ${product.color}</p>
                            ${price.outerHTML}
                            <a href="#" class="nav-link-card bg-red-600 text-white px-4 py-2 rounded-lg font-medium hover:bg-red-700 transition duration-300" data-page="quote" data-product-id="${product.productId}">Get a Quote</a>
                        </div>
                    `;
            productCard.querySelector('.nav-link-card').addEventListener('click', (e) => {
                e.preventDefault();
                showPage('quote');
                // Use the element's dataset (safer than e.target which can be a child element)
                const pid = e.currentTarget && e.currentTarget.dataset ? e.currentTarget.dataset.productId : null;
                if (pid) quoteProductSelect.value = pid;
            });
            productGrid.appendChild(productCard);
        });
    }

    // --- Populate Quote Form Product Select ---
    function populateQuoteSelect(products) {
        quoteProductSelect.innerHTML = '<option value="">-- Select a Brick --</option>';
        products.forEach(product => {
            const option = document.createElement('option');
            // Use product.productId
            option.value = product.productId;
            option.textContent = `${product.name} (₹${product.unitPrice.toFixed(2)})`;
            quoteProductSelect.appendChild(option);
        });
    }

    // --- Form Submissions ---


    // --- "Get a Quote" Form (UPDATED with validation) ---
    document.getElementById('quote-form').addEventListener('submit', async (e) => {
        e.preventDefault();

        const selectedProductId = document.getElementById('quote-product').value;
        const quantity = document.getElementById('quote-quantity').value;
        const deliveryLocation = document.getElementById('quote-location').value;

        // We add this check to prevent submitting an empty/invalid product ID.
        if (!selectedProductId) {
            showStatus("Please select a brick type.", true);
            return; // Stop the function here
        }

        const quoteRequest = {
            customerId: 0, // Placeholder 0 for a public "guest" quote
            deliveryLocation: deliveryLocation,
            items: [
                {
                    productId: parseInt(selectedProductId),
                    quantity: parseInt(quantity)
                }
            ]
        };

        try {
            // Call the NEW public endpoint
            const response = await fetch(`${API_URL}/api/orders/public-quote`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(quoteRequest)
            });

            if (!response.ok) {
                // --- THIS IS THE NEW ERROR HANDLING ---
                let errorMsg = 'Quote submission failed. Please try again.';
                try {
                    // Try to get a specific message from Spring Boot
                    const errorData = await response.json();
                    // Check for Spring's specific "error" field
                    errorMsg = errorData.error || errorData.message || `Error ${response.status}`;
                } catch (jsonError) {
                    // If the response isn't JSON, just use the status
                    errorMsg = `Error: ${response.status} ${response.statusText}`;
                }
                throw new Error(errorMsg); // Throw the detailed error
            }

            showStatus("Quote request sent successfully! We will contact you soon.");
            document.getElementById('quote-form').reset();

            // Go back home after 5 seconds, as requested
            setTimeout(() => {
                showPage('home'); // Go back home
            }, 5000);

        } catch (error) {
            console.error('Quote submission failed:', error);
            // This will now show the *detailed* error message (e.g., "Forbidden")
            showStatus(error.message, true);
        }
    });

    // --- "Contact Us" Form ---
    const contactForm = document.getElementById('contact-form');
    // const contactSubmitButton = document.getElementById('contact-submit-button');
    contactForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const contactSubmitButton = contactForm.querySelector('button[type="submit"]');

        contactSubmitButton.disabled = true;
        contactSubmitButton.textContent = "Sending...";

        const contactData = {
            name: document.getElementById('contact-name').value,
            email: document.getElementById('contact-email').value,
            message: document.getElementById('contact-message').value
        };

        try {
            // Call our NEW endpoint via the API Gateway
            const response = await fetch(`${API_URL}/api/v1/contact`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(contactData)
            });

            if (!response.ok) {
                let errorMsg = 'Message failed to send.';
                try {
                    // Try to get validation errors from Spring Boot
                    const errorData = await response.json();
                    if (errorData.errors) {
                        // Join multiple validation errors
                        errorMsg = Object.values(errorData.errors).join(', ');
                    } else {
                        errorMsg = errorData.message || `Error: ${response.status}`;
                    }
                } catch (jsonError) {
                    errorMsg = `Error: ${response.status} ${response.statusText}`;
                }
                throw new Error(errorMsg);
            }

            // Success!
            await response.json();
            showStatus("Message sent successfully! We will get back to you soon.");
            contactForm.reset();

        } catch (error) {
            console.error('Contact form submission failed:', error);
            showStatus(error.message, true);
        } finally {
            // Re-enable the button whether it succeeded or failed
            contactSubmitButton.disabled = false;
            contactSubmitButton.textContent = "Send Message";
        }
    });

    // ==================================================================
    // ADMIN DASHBOARD
    // ==================================================================
    // --- Admin Tab Navigation ---
    adminTabs.forEach(tab => {
        tab.addEventListener('click', (e) => {
            e.preventDefault();

            // Style tabs
            adminTabs.forEach(t => {
                t.classList.remove('bg-white', 'text-blue-600', 'border-b-2', 'border-blue-600');
                t.classList.add('text-gray-500', 'hover:text-gray-700');
            });
            tab.classList.add('bg-white', 'text-blue-600', 'border-b-2', 'border-blue-600');
            tab.classList.remove('text-gray-500', 'hover:text-gray-700');

            // Show panel
            const panelId = tab.id.replace('admin-tab-', 'admin-panel-');
            adminPanels.forEach(panel => {
                panel.hidden = (panel.id !== panelId);
            });
            // If switching to messages panel, refresh messages
            if (panelId === 'admin-panel-messages') {
                if (getAuthToken && getAuthToken()) {
                    if (typeof window.fetchAdminMessages === 'function') window.fetchAdminMessages();
                }
            }
            // If switching to products panel, refresh admin products (only if logged in)
            if (panelId === 'admin-panel-products') {
                if (getAuthToken && getAuthToken()) {
                    if (typeof fetchAdminProducts === 'function') fetchAdminProducts();
                }
            }
        });
    });

    // --- Fetch and Render Products for Admin Table ---
    async function fetchAdminProducts() {
        adminProductTableBody.innerHTML = `<tr><td colspan="5" class="p-4 text-center">Loading products...</td></tr>`;
        try {
            const response = await fetch(`${API_URL}/api/products`, {
                method: 'GET',
                headers: getAuthHeader()
            });

            if (handleAuthError(response)) return; // Stop if auth failed
            if (!response.ok) throw new Error("Failed to fetch products");

            const products = await response.json();
            renderAdminProductTable(products);

        } catch (error) {
            console.error("Error fetching admin products:", error);
            adminProductTableBody.innerHTML = `<tr><td colspan="5" class="p-4 text-center text-red-500">${error.message}</td></tr>`;
        }
    }

    // --- Render Admin Product Table ---
    function renderAdminProductTable(products) {
        adminProductTableBody.innerHTML = '';
        if (products.length === 0) {
            adminProductTableBody.innerHTML = `<tr><td colspan="5" class="p-4 text-center">No products found.</td></tr>`;
            return;
        }

        products.forEach(product => {
            const row = document.createElement('tr');
            row.innerHTML = `
                        <td class="px-4 py-3 whitespace-nowrap text-sm font-medium text-gray-900">${product.name}</td>
                        <td class="px-4 py-3 whitespace-nowrap text-sm text-gray-500">${product.brickType}</td>
                        <td class="px-4 py-3 whitespace-nowrap text-sm text-gray-500">${product.color}</td>
                        <td class="px-4 py-3 whitespace-nowrap text-sm text-gray-500">₹${product.unitPrice.toFixed(2)}</td>
                        <td class="px-4 py-3 whitespace-nowrap text-sm text-gray-500">${product.stockQuantity}</td>
                        <td class="px-4 py-3 whitespace-nowrap text-sm font-medium space-x-2">
                            <button class="admin-edit-btn text-blue-600 hover:text-blue-900" data-id="${product.productId}">Edit</button>
                            <button class="admin-delete-btn text-red-600 hover:text-red-900" data-id="${product.productId}">Delete</button>
                        </td>
                    `;
            // Add listeners for the new buttons
            // We ensure the ID is parsed correctly
            const pId = product.productId;
            row.querySelector('.admin-edit-btn').addEventListener('click', () => openEditModal(pId));
            row.querySelector('.admin-delete-btn').addEventListener('click', () => deleteProduct(pId));

            adminProductTableBody.appendChild(row);
        });
    }

    // --- Admin "Add Product" Form ---
    adminAddForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const button = document.getElementById('add-product-button');
        button.disabled = true;
        button.textContent = "Adding...";

        const product = {
            name: document.getElementById('add-name').value,
            brickType: document.getElementById('add-type').value,
            color: document.getElementById('add-color').value,
            unitPrice: parseFloat(document.getElementById('add-price').value),
            stockQuantity: parseInt(document.getElementById('add-stock').value)
        };

        // Create FormData for Multipart Upload
        const formData = new FormData();

        // 1. Add Product JSON as a specific Blob with content-type application/json
        const productBlob = new Blob([JSON.stringify(product)], { type: 'application/json' });
        formData.append('product', productBlob);

        // 2. Add Image File (if selected)
        const fileInput = document.getElementById('add-image-file');
        if (fileInput.files.length > 0) {
            formData.append('imageFile', fileInput.files[0]);
        }
        console.log("Submitting Add Product FormData:", formData);
        try {
            const token = getAuthToken();
            const response = await fetch(`${API_URL}/api/products`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`
                    // Note: Content-Type is NOT set manually. fetch sets it to multipart/form-data with boundary automatically.
                },
                body: formData
            });
            console.log("Add Product Response Status:", response.status);
            if (handleAuthError(response)) return;
            console.log("Add Product Response OK:", response.ok);
            if (!response.ok) throw new Error("Failed to create product");
            console.log("Add Product Response Received");
            showStatus("Product Added Successfully!");
            adminAddForm.reset();
            console.log("Add Product Form Reset");

            // Reset image preview
            const addImagePreview = document.getElementById('add-image-preview');
            addImagePreview.classList.add('hidden');
            addImagePreview.src = "";

            fetchAdminProducts(); // Refresh the table
            fetchProducts(); // Refresh the public products page

        } catch (error) {
            console.error("Add product failed:", error);
            showStatus(error.message, true);
        } finally {
            button.disabled = false;
            button.textContent = "Add Product";
        }
    });

    // Image Previews
    const addImageFile = document.getElementById('add-image-file');
    const addImagePreview = document.getElementById('add-image-preview');
    const editImageFile = document.getElementById('edit-image-file');
    const editImagePreview = document.getElementById('edit-image-preview');

    addImageFile.addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (!file) return;
        const reader = new FileReader();
        reader.onload = (ev) => {
            addImagePreview.src = ev.target.result;
            addImagePreview.classList.remove('hidden');
        };
        reader.readAsDataURL(file);
    });

    editImageFile.addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (!file) return;
        const reader = new FileReader();
        reader.onload = (ev) => {
            editImagePreview.src = ev.target.result;
        };
        reader.readAsDataURL(file);
    });

    // --- Admin "Delete Product" Function ---
    async function deleteProduct(id) {
        if (!confirm("Are you sure you want to delete this product? This cannot be undone.")) {
            return;
        }

        try {
            const response = await fetch(`${API_URL}/api/products/${id}`, {
                method: 'DELETE',
                headers: getAuthHeader()
            });

            if (handleAuthError(response)) return;
            if (!response.ok) throw new Error("Failed to delete product");

            showStatus("Product deleted successfully.");
            fetchAdminProducts(); // Refresh the table
            fetchProducts(); // Refresh the public products page

        } catch (error) {
            console.error("Delete product failed:", error);
            showStatus(error.message, true);
        }
    }

    // --- Admin "Edit Product" Modal ---
    // Edit Modal Logic
    function openEditModal(id) {
        currentEditProductId = id;
        const product = allProducts.find(p => p.productId === id);
        if (!product) return;

        document.getElementById('edit-id').value = product.productId;
        document.getElementById('edit-name').value = product.name;
        document.getElementById('edit-type').value = product.brickType;
        document.getElementById('edit-color').value = product.color;
        document.getElementById('edit-price').value = product.unitPrice;
        document.getElementById('edit-stock').value = product.stockQuantity;

        document.getElementById('edit-image-file').value = "";
        document.getElementById('edit-image-preview').src = getProductImage(product);

        adminEditModal.classList.remove('opacity-0', 'pointer-events-none');
        adminEditModal.querySelector('.modal-content').classList.remove('scale-95');
    }

    function closeEditModal() {
        adminEditModal.classList.add('opacity-0', 'pointer-events-none');
        adminEditModal.querySelector('.modal-content').classList.add('scale-95');
        currentEditProductId = null;
        adminEditForm.reset();
    }


    editModalClose.addEventListener('click', closeEditModal);
    editModalCancel.addEventListener('click', closeEditModal);

    // Edit Form Submit
    adminEditForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const btn = document.getElementById('edit-product-button');
        btn.disabled = true;
        btn.textContent = "Saving...";

        const product = {
            productId: currentEditProductId,
            name: document.getElementById('edit-name').value,
            brickType: document.getElementById('edit-type').value,
            color: document.getElementById('edit-color').value,
            unitPrice: parseFloat(document.getElementById('edit-price').value),
            stockQuantity: parseInt(document.getElementById('edit-stock').value)
        };

        const formData = new FormData();
        formData.append('product', new Blob([JSON.stringify(product)], { type: 'application/json' }));
        const fileInput = document.getElementById('edit-image-file');
        if (fileInput.files.length > 0) formData.append('imageFile', fileInput.files[0]);

        try {
            const response = await fetch(`${API_URL}/api/products/${currentEditProductId}`, {

                method: 'PUT',
                headers: { 'Authorization': `Bearer ${getAuthToken()}` },
                body: formData
            });
            if (handleAuthError(response)) return;
            if (!response.ok) throw new Error("Update failed");

            showStatus("Product updated!");
            closeEditModal();
            fetchAdminProducts();
            fetchProducts();
        } catch (error) {
            console.error(error);
            showStatus("Update failed.", true);
        } finally {
            btn.disabled = false;
            btn.textContent = "Save Changes";
        }
    });

    // --- Function to Load Gallery Images from DB ---
    async function loadGallery() {
        const galleryGrid = document.getElementById('gallery-product-grid');

        // Safety check: ensure the element exists before trying to modify it
        if (!galleryGrid) return;

        try {
            // 1. Fetch data from your backend (use configured API_URL)
            const response = await fetch(`${API_URL}/api/products`);

            if (!response.ok) throw new Error("Failed to fetch products");

            const products = await response.json();

            // 2. Handle empty database
            if (products.length === 0) {
                galleryGrid.innerHTML = '<p class="col-span-full text-center text-gray-500">No products found in database.</p>';
                return;
            }

            // 3. Clear the "Loading..." text
            galleryGrid.innerHTML = '';

            // 4. Loop through products and create HTML
            products.forEach(product => {
                // Logic to determine image source: DB URL -> Local Helper -> Placeholder
                let imgSrc = product.imageUrl;

                // If DB url is empty, try to match local images based on name (Optional helper)
                if (!imgSrc) {
                    const lowerName = product.name.toLowerCase();
                    if (lowerName.includes("red")) imgSrc = "./images/red-brick.jpg";
                    else if (lowerName.includes("cement")) imgSrc = "./images/cement-brick.jpg";
                    else if (lowerName.includes("gattu") || lowerName.includes("paver")) imgSrc = "./images/gattu.jpg";
                    else imgSrc = `https://placehold.co/600x400/cccccc/FFFFFF?text=${encodeURIComponent(product.name)}`;
                }

                // Create safe DOM nodes for the gallery card (prevents accidental HTML injection)
                const card = document.createElement('div');
                card.className = 'rounded-lg shadow-md overflow-hidden shrink-0 group relative';

                const img = document.createElement('img');
                // Prefer getProductImage helper (handles data URI, imageUrl, and fallback)
                img.src = getProductImage(product) || imgSrc;
                img.alt = product.name || 'Product image';
                img.className = 'w-full h-64 object-contain transition-transform duration-300 group-hover:scale-105';
                img.onerror = function () { this.src = 'https://placehold.co/600x400/cccccc/FFFFFF?text=Image+Not+Found'; };

                const overlay = document.createElement('div');
                overlay.className = 'absolute bottom-0 left-0 right-0 bg-black bg-opacity-50 text-white text-center py-2 opacity-0 group-hover:opacity-100 transition-opacity';
                overlay.textContent = product.name || '';

                card.appendChild(img);
                card.appendChild(overlay);

                // Append to grid
                galleryGrid.appendChild(card);
            });

        } catch (error) {
            console.error("Gallery Load Error:", error);
            galleryGrid.innerHTML = '<p class="col-span-full text-center text-red-500">Error loading gallery images.</p>';
        }
    }

    // --- Initial Load ---
    fetchProducts();
    checkLoginState();
    // Load the gallery now that init is running
    loadGallery();

});
