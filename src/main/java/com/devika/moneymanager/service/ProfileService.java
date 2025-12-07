package com.devika.moneymanager.service;

import com.devika.moneymanager.dto.AuthDTO;
import com.devika.moneymanager.dto.ProfileDTO;
import com.devika.moneymanager.entity.ProfileEntity;
import com.devika.moneymanager.repository.ProfileRepository;
import com.devika.moneymanager.util.JWTUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
//@AllArgsConstructor
public class ProfileService {
    // should be final in order to inject the profileRepository
    // constructor injection(@RequiredArgsConstructor)
    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    @Value("${app.activation.url}")
    private String activationURL;

    public ProfileDTO registerProfile(ProfileDTO profileDTO){
        ProfileEntity newProfile = toEntity(profileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile= profileRepository.save(newProfile);
        // send activation mail
        String activationLink= activationURL+"/api/v1.0/activate?token="+newProfile.getActivationToken();
        String to= newProfile.getEmail();
        String subject="Activate your MoneyManager Account";
        String body="Click on the following link to activate your account: " + activationLink;
        emailService.sendEmail(to,subject,body);
       return toDTO(newProfile);
    }

    public ProfileEntity toEntity(ProfileDTO profileDTO){
        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .profileImage(profileDTO.getProfileImage())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();
    }

    public ProfileDTO toDTO(ProfileEntity profileEntity){
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .profileImage(profileEntity.getProfileImage())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }

    public Boolean activateProfile(String activationToken){
        return profileRepository.findByActivationToken(activationToken)
                .map(profile -> {
                    profile.setIsActive(true);
                    profileRepository.save(profile);
                    return true;
                }).orElse(false);
    }

    public boolean isAccountActive(String email){
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }

    public ProfileEntity getCurrentProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return profileRepository.findByEmail(authentication.getName())
                .orElseThrow(()-> new UsernameNotFoundException("Profile is not found with email: "+ authentication.getName() ));
    }

    public ProfileDTO getPublicProfile(String email){
        ProfileEntity currenrUser = null;
        if(email == null){
            currenrUser=getCurrentProfile();
        }else{
            currenrUser=profileRepository.findByEmail(email)
                    .orElseThrow(()-> new UsernameNotFoundException("Profile is not found with email: "+ email));
        }
        return ProfileDTO.builder()
                .id(currenrUser.getId())
                .fullName(currenrUser.getFullName())
                .email(currenrUser.getEmail())
                .profileImage(currenrUser.getProfileImage())
                .createdAt(currenrUser.getCreatedAt())
                .updatedAt(currenrUser.getUpdatedAt())
                .build();

    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
      try{
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(),authDTO.getPassword()));
        String token = jwtUtil.generateToken(authDTO.getEmail());
        return Map.of(
                "token", token,
                "user", getPublicProfile(authDTO.getEmail())
        );
      }catch (Exception e){
          throw new RuntimeException("Invalid email or password");
      }
    }
}
