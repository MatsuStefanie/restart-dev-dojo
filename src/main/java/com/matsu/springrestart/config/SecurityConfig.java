package com.matsu.springrestart.config;

import com.matsu.springrestart.service.CustomerDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("*/auth/**").hasRole("ADMIN")
                        .requestMatchers("animes/**").hasRole("USER")
                        .anyRequest()
                        .authenticated()
                )
                .formLogin(withDefaults())
                .httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    public AuthenticationManager userManager(CustomerDetailsService customerDetailsService,
                                             HttpSecurity httpSecurity) throws Exception {

        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        AuthenticationManagerBuilder builderAuthentication = httpSecurity
                .getSharedObject(AuthenticationManagerBuilder.class);

        builderAuthentication
                .userDetailsService(customerDetailsService)
                .passwordEncoder(passwordEncoder);

        return builderAuthentication.build();
    }
}
