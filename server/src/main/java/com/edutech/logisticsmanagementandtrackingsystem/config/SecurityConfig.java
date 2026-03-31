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

    public SecurityConfig(UserDetailsService userDetailsService, JwtRequestFilter jwtRequestFilter,
            PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()

                // Public endpoints
                .antMatchers(HttpMethod.POST, "/register", "/login").permitAll()

                // BUSINESS role endpoints
                .antMatchers(HttpMethod.POST, "/business/cargo", "/business/assign-cargo").hasRole("BUSINESS")
                .antMatchers(HttpMethod.GET, "/business/drivers", "/business/cargo", "/business/cargo-id").hasRole("BUSINESS")

                // DRIVER role endpoints
                .antMatchers(HttpMethod.GET, "/driver/cargo").hasRole("DRIVER")
                .antMatchers(HttpMethod.PUT, "/driver/update-cargo-status").hasRole("DRIVER")

                // CUSTOMER role endpoints
                .antMatchers(HttpMethod.GET, "/customer/cargo-status").hasRole("CUSTOMER")

                // All other requests must be authenticated
                .anyRequest().authenticated()

                .and()
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
