package com.brick.app.employeeservice.controller;

import com.brick.app.employeeservice.dto.AuthRequest;
import com.brick.app.employeeservice.dto.AuthResponse;
import com.brick.app.employeeservice.entity.Employee;
import com.brick.app.employeeservice.repository.EmployeeRepository;
import com.brick.app.employeeservice.service.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager  authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

   // Endpoint for registering a new employee
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Employee registerUser(@RequestBody Employee employee){
        // IMPORTANT: We must hash the password before saving it to the database.
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        return employeeRepository.save(employee);
    }

    //Endpoint for logging and receiving a JWT
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception{
        try{
            // Step 1: Authenticate the user. Spring Security will use our EmployeeUserDetailsService
            // and PasswordEncoder to verify the credentials.
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(),authRequest.getPassword())
            );
        } catch (Exception e) {
            // If authentication fails, throw an exception
            throw new Exception("Incorrect username and password",e);
        }

        // Step 2: If authentication is successful, load the UserDetails.
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

        // Step 3: Generate the JWT.
        final String jwt = jwtUtil.generateToken(userDetails);

        // Step 4: Return the jwt in response
        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}
