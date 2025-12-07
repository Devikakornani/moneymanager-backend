package com.devika.moneymanager.service;

import com.devika.moneymanager.entity.ProfileEntity;
import com.devika.moneymanager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
//Spring security UserDetailsService loads user information from db when user logs in
public class AppUserDetailsService implements UserDetailsService {
    private final ProfileRepository profileRepository;

    // loading user details from spring security
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        ProfileEntity existingProfile = profileRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("Profile si not found with the email: "+ email));
        // returning user object
        return User.builder()
                .username(existingProfile.getEmail())
                .password(existingProfile.getPassword())
                .authorities(Collections.emptyList()) // not maintaining any roles here
                .build();
    }
}
