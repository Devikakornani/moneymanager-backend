package com.devika.moneymanager.config;

import com.devika.moneymanager.security.JwtRequestFilter;
import com.devika.moneymanager.service.AppUserDetailsService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

//bean - springboot regsiters in application context during setup
// then spring security automatically discovers these methods

@Configuration //marking as config bean to set up spring security
@RequiredArgsConstructor //generate const for any final fields
public class SecurityConfig {
    private final AppUserDetailsService appUserDetailsService;
    private final JwtRequestFilter jwtRequestFilter;

    // spring security now uses this filter instead of websecurityconfigureradapter
    // configures cors,csrf,session policy, which endpoints need auth, jwt filter position
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        //enables cross origin resource sharing , useful if fe hosted on another domain
        httpSecurity.cors(Customizer.withDefaults())
                // used for stateful web apps using sessions+cookies(our app is stateless-jwt)
                .csrf(AbstractHttpConfigurer::disable)
                //doesnt require authentication
                .authorizeHttpRequests(auth -> auth.requestMatchers("/status", "/health", "/activate", "/register", "/login").permitAll()
                       //everything else needs login login token(protected)
                        .anyRequest().authenticated())
                // no session is stored on server, every request must include jwt authentication
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();

    }

    @Bean
    //uses this bean when comparing raw pwd with stored hashed pwd during login
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //defining cors rules for the backend apis
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // allows reqs from any domain, recommended to specify fe domain(https://fe-domain.com")
        corsConfiguration.setAllowedOrigins(List.of("*"));
        // these are the methods allowed from fe
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        // browser blocks the req if fe sends any other header not in the list
        // Authorization required for JWT
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        // allows sending cookies, tokens,
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // /** - apply to all the endpoints
        source.registerCorsConfiguration("/**",corsConfiguration);
        return source;
    }

    @Bean
    // ss uses to authenticate user
    // invokes AppUserDetailsService, validates pwd using encoder
    // jwt token will be generated if authentication is successful
    public AuthenticationManager authenticationManager(){
        DaoAuthenticationProvider authenticationProvider= new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(appUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authenticationProvider);
    }
}
