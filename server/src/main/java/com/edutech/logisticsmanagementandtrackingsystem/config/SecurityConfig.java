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

@Configuration
@EnableWebSecurity
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

        // /register and /login should be permitted to all
        // /business/cargo should be permitted to users with BUSINESS role
        // /business/assign-cargo should be permitted to users with BUSINESS role
        // /driver/cargo should be permitted to users with DRIVER role
        // /driver/update-cargo-status should be permitted to users with DRIVER role
        // /customer/cargo-status should be permitted to users with CUSTOMER role
        // all other requests should be authenticated

        // configure jwtRequestFilter to be executed before UsernamePasswordAuthenticationFilter

        
http.csrf().disable()
    .sessionManagement()
    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    .and()
    .authorizeRequests()

    .antMatchers("/register/**", "/login/**").permitAll()

    .antMatchers("/business/**").hasRole("BUSINESS")
    .antMatchers("/driver/**").hasRole("DRIVER")
    .antMatchers("/customer/**").hasRole("CUSTOMER")

    .anyRequest().authenticated()

    .and()
    .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
        .userDetailsService(userDetailsService)
        .passwordEncoder(passwordEncoder);
    }



}