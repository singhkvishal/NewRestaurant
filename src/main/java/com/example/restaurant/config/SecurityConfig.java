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
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> {
                // Define public paths
                String[] publicPaths = {
                    "/", "/index", "/menu", "/menu/**", "/about", "/contact",
                    "/login", "/login/**", "/register", "/register/**", "/error",
                    "/reviews", "/reviews/**",
                    "/order/menu", "/order/menu/**", "/reservations/**",
                    "/cart/**", "/cart/add/**", "/cart/remove/**", "/cart/update/**",
                    "/css/**", "/js/**", "/images/**", "/webjars/**",
                    "/h2-console/**"
                };
                
                // Define API endpoints
                String[] apiEndpoints = {
                    "/api/check-auth"
                };
                
                // Configure security rules
                auth.requestMatchers(publicPaths).permitAll()
                    .requestMatchers(apiEndpoints).permitAll()
                    .requestMatchers("/order/checkout").authenticated()
                    .requestMatchers("/cart/checkout").authenticated()
                    .requestMatchers("/order/place").authenticated()
                    .requestMatchers("/order/confirmation/**").permitAll()
                    .anyRequest().authenticated();
            })
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/menu", true)
                .loginProcessingUrl("/login")
                .successHandler((request, response, authentication) -> {
                    // Handle successful authentication
                    String redirectUrl = request.getParameter("redirect");
                    if (redirectUrl != null && !redirectUrl.isEmpty()) {
                        response.sendRedirect(redirectUrl);
                    } else {
                        response.sendRedirect("/menu");
                    }
                })
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    // Redirect to login page with redirect URL for unauthenticated access to protected resources
                    String redirectUrl = request.getRequestURI();
                    if (request.getQueryString() != null) {
                        redirectUrl += "?" + request.getQueryString();
                    }
                    response.sendRedirect("/login?redirect=" + java.net.URLEncoder.encode(redirectUrl, "UTF-8"));
                })
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
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(
                    "/h2-console/**"
                )
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
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
