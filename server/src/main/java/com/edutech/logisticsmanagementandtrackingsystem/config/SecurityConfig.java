package com.edutech.logisticsmanagementandtrackingsystem.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.edutech.logisticsmanagementandtrackingsystem.jwt.JwtRequestFilter;


public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final JwtRequestFilter jwtRequestFilter;
    private final PasswordEncoder passwordEncoder;

    
   

    public SecurityConfig(UserDetailsService userDetailsService, JwtRequestFilter jwtRequestFilter,
            PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
        this.passwordEncoder = passwordEncoder;
    }




    // public SecurityConfig(boolean disableDefaults, UserDetailsService userDetailsService,
    //         JwtRequestFilter jwtRequestFilter, PasswordEncoder passwordEncoder) {
    //     super(disableDefaults);
    //     this.userDetailsService = userDetailsService;
    //     this.jwtRequestFilter = jwtRequestFilter;
    //     this.passwordEncoder = passwordEncoder;
    // }




    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // complete these method to configure the security of the application

        // /api/register and /api/login should be permitted to all
        // /api/business/cargo should be permitted to users with BUSINESS role
        // /api/business/assign-cargo should be permitted to users with BUSINESS role
        // /api/driver/cargo should be permitted to users with DRIVER role
        // /api/driver/update-cargo-status should be permitted to users with DRIVER role
        // /api/customer/cargo-status should be permitted to users with CUSTOMER role
        // all other requests should be authenticated

        // configure jwtRequestFilter to be executed before UsernamePasswordAuthenticationFilter

        
http.csrf().disable()
        .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // because using JWT
        .and()
        .authorizeRequests()

        // ✅ POST: permit all
        .antMatchers(HttpMethod.POST, "/api/register", "/api/login")
            .permitAll()

        // ✅ POST: BUSINESS authority
        .antMatchers(HttpMethod.POST, "/api/business/cargo", "/api/business/assign-cargo")
            .hasAuthority("BUSINESS")

        // ✅ GET: BUSINESS authority
        .antMatchers(HttpMethod.GET, "/api/business/drivers", "/api/business/cargo")
            .hasAuthority("BUSINESS")

        // ✅ GET: DRIVER authority
        .antMatchers(HttpMethod.GET, "/api/driver/cargo")
            .hasAuthority("DRIVER")

        // ✅ GET: CUSTOMER authority
        .antMatchers(HttpMethod.GET, "/api/customer/cargo-status")
            .hasAuthority("CUSTOMER")

        // ✅ PUT: CUSTOMER authority
        .antMatchers(HttpMethod.PUT, "/api/customer/cargo-status")
            .hasAuthority("CUSTOMER")

        // ✅ All other endpoints require authentication
        .anyRequest().authenticated()
        .and().addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return authenticationManagerBean();
    }

}