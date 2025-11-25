package com.brick.app.productservice.config;

import com.brick.app.productservice.service.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;
// 1. Check for the Authorization header and the "Bearer " prefix
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // Extract the token
            username = jwtUtil.extractUsername(jwt);
        }

        // 2. If we have a username and there is no user currently authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 3. Load the user's details from the database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 4. Validate the token (does the username match? is it expired?)
            if (jwtUtil.validateToken(jwt, userDetails)) {

                // 5. If valid, create an authentication token
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 6. Set this token in the SecurityContext, effectively "logging in" the user for this one request
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        // 7. Pass the request on to the next filter in the chain
        chain.doFilter(request, response);
    }
}