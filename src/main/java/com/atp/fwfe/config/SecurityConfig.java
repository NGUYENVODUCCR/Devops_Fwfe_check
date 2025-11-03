package com.atp.fwfe.config;

import com.atp.fwfe.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@EnableMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/", "/index.html", "/favicon.ico", "/static/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/api/auth/**", "/api/chat/**", "/api/companies").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/admin/*").authenticated()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        .requestMatchers("/api/account/**").hasAnyRole("ADMIN", "MANAGER", "USER")

                        .requestMatchers(HttpMethod.POST, "/api/companies").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/companies/my").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/companies/search").hasAnyRole("ADMIN", "MANAGER", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/companies/{id}/public").hasAnyRole("ADMIN", "MANAGER", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/companies/**").hasAnyRole("ADMIN", "MANAGER", "USER")
                        .requestMatchers(HttpMethod.PUT, "/api/companies/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/companies/**").hasAnyRole("ADMIN", "MANAGER")

                        .requestMatchers(HttpMethod.POST, "/api/works/*/acceptances").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/api/works/*/acceptances/{acceptanceId}/status").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/works/*/acceptances/account/*/status/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/works/*/acceptances").hasAnyRole("ADMIN", "MANAGER", "USER")

                        .requestMatchers(HttpMethod.POST, "/api/reports").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/reports/unresolved").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/works-posted").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/works-posted").hasAnyRole("ADMIN", "MANAGER", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/works-posted/getAll").hasAnyRole("ADMIN", "MANAGER", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/works-posted/**").hasAnyRole("ADMIN", "MANAGER", "USER")
                        .requestMatchers(HttpMethod.PUT, "/api/works-posted/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/works-posted/**").hasAnyRole("ADMIN", "MANAGER")

                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(List.of(
                "https://fwfe.duckdns.org",
                "http://152.42.196.211",
                "http://152.42.196.211:*",
                "http://localhost:*",
                "http://127.0.0.1:*",
                "http://10.0.2.2:*"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "X-Username",
                "X-Role"
        ));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
