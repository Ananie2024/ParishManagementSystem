package org.ananie.parishManagementSystem.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // .loginPage("/login") // This is the default, but you can specify a custom path
        http
                .authorizeHttpRequests(authorize -> authorize
                        // 1. Allow public access to all /hello paths
                        .requestMatchers( "/hello/**").permitAll()
                        .requestMatchers("/api/**").permitAll()

                        // 2. Allow access to the default Spring Security login page
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/actuator/**").permitAll()

                        // 3. Require authentication for everything else
                        .anyRequest().authenticated()
                )
                // 4. Configure Form-based Login
                .formLogin(AbstractAuthenticationFilterConfigurer::permitAll // Ensure the login form itself is publicly accessible
                );
        // We remove the .httpBasic() call here

        return http.build();
    }
}
