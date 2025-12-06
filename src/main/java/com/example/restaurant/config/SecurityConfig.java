package com.example.restaurant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers("/", "/menu/**", "/about", "/contact", 
                                  "/login", "/register", "/error",
                                  "/reviews", "/reviews/**",
                                  "/css/**", "/js/**", "/images/**", "/webjars/**",
                                  "/h2-console/**",
                                  "/*.css", "/*.js", "/*.png", "/*.jpg", "/*.jpeg", "/*.gif", "/*.svg", "/*.ico")
                    .permitAll()
                    .anyRequest().authenticated();
            })
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/menu", true)
                .permitAll()
            )
            .logout(logout -> 
                logout.logoutSuccessUrl("/")
                      .permitAll()
            )
            .anonymous(anonymous -> anonymous
                .principal("anonymousUser")
                .authorities("ROLE_ANONYMOUS")
                .key("anonymousKey")
            )
            .csrf(csrf -> csrf.disable()) // For H2 console and development only
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin()) // For H2 console
                .httpStrictTransportSecurity(hsts -> hsts.disable()))
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder.encode("admin123"))
            .roles("ADMIN")
            .build();
            
        UserDetails user = User.builder()
            .username("user")
            .password(passwordEncoder.encode("user123"))
            .roles("USER")
            .build();
            
        return new InMemoryUserDetailsManager(admin, user);
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
