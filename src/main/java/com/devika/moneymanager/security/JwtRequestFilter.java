package com.devika.moneymanager.security;

import com.devika.moneymanager.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
// intercepts every req before it reaches the controller
// checks for jwt token in the header, and authenticated the user if token is  valid
public class JwtRequestFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JWTUtil jwtUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // extracts token from header and email from it
        final String authHeader=request.getHeader("Authorization");
        String email =null;
        String jwt = null;
        if(authHeader !=null && authHeader.startsWith("Bearer")){
            jwt= authHeader.substring(7);
            email = jwtUtil.extractUsername(jwt);
        }
        //if that email is not null and not authenticated, loads user from db using AppUserDetailsService
        // and checking token is valid(token signature,email matches,token not expired) or not using fetched email from db
        if(email != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
            if(jwtUtil.isTokenValid(jwt,userDetails.getUsername())){
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,null,userDetails.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        // continues processing requests
        filterChain.doFilter(request,response);
    }
}
