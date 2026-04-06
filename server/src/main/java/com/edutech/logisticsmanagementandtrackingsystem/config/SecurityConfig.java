package com.edutech.logisticsmanagementandtrackingsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.edutech.logisticsmanagementandtrackingsystem.jwt.JwtRequestFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

private final UserDetailsService userDetailsService;
private final JwtRequestFilter jwtRequestFilter;
private final PasswordEncoder passwordEncoder;

public SecurityConfig(UserDetailsService userDetailsService,
JwtRequestFilter jwtRequestFilter,
PasswordEncoder passwordEncoder) {
this.userDetailsService = userDetailsService;
this.jwtRequestFilter = jwtRequestFilter;
this.passwordEncoder = passwordEncoder;
}

@Override
protected void configure(HttpSecurity http) throws Exception {

http
.cors().and() // :white_check_mark: enable CORS
.csrf().disable() // :white_check_mark: disable CSRF for REST APIs

.sessionManagement()
.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
.and()

.authorizeRequests()

// :unlock: PUBLIC ENDPOINTS
.antMatchers(HttpMethod.POST,
"/api/register",
"/api/login",
"/api/send-otp"
).permitAll()

// :fire: GPS TRACKING APIs (VERY IMPORTANT)
.antMatchers("/api/location/**").permitAll()

// :office: BUSINESS ROLE
.antMatchers(HttpMethod.POST,
"/api/business/cargo",
"/api/business/assign-cargo",
"/api/business/cargo-with-documents"
).hasAuthority("BUSINESS")

.antMatchers(HttpMethod.GET,
"/api/business/drivers",
"/api/business/cargo",
"/api/business/cargo-id"
).hasAuthority("BUSINESS")

// :truck: DRIVER ROLE
.antMatchers(HttpMethod.GET, "/api/driver/cargo")
.hasAuthority("DRIVER")

.antMatchers(HttpMethod.PUT, "/api/driver/update-cargo-status")
.hasAuthority("DRIVER")

                
                // CUSTOMER role endpoints
                .antMatchers(HttpMethod.GET, "/api/customer/cargo-status")
                .hasAuthority("CUSTOMER")

                // CHATBOT endpoint
                // ✅ Public Chatbot (works before login)
                .antMatchers(HttpMethod.POST, "/api/chat/message").permitAll()


// :page_facing_up: DOCUMENT ACCESS
.antMatchers(HttpMethod.GET, "/api/documents/**")
.hasAnyAuthority("BUSINESS", "DRIVER")

// :closed_lock_with_key: ALL OTHER REQUESTS REQUIRE AUTH
.anyRequest().authenticated()

.and()

// :closed_lock_with_key: JWT FILTER
.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
}

@Override
@Bean
public AuthenticationManager authenticationManagerBean() throws Exception {
return super.authenticationManagerBean();
}

@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
auth
.userDetailsService(userDetailsService)
.passwordEncoder(passwordEncoder);
}
}