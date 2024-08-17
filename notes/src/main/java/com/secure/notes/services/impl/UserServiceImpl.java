package com.secure.notes.services.impl;

import com.secure.notes.dtos.UserDTO;
import com.secure.notes.models.AppRole;
import com.secure.notes.models.PasswordResetToken;
import com.secure.notes.models.Role;
import com.secure.notes.models.User;
import com.secure.notes.repositories.PasswordResetTokenRepository;
import com.secure.notes.repositories.RoleRepository;
import com.secure.notes.repositories.UserRepository;
import com.secure.notes.services.TotpService;
import com.secure.notes.services.UserService;
import com.secure.notes.util.EmailService;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Value("${frontend.url}")
    String frontendUrl;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder ;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository ;

    @Autowired
    EmailService emailService;

    @Autowired
    TotpService totpService ;

    @Override
    public void updateUserRole(Long userId, String roleName) {
        Optional<User> optionalUser=userRepository.findById(userId) ;
        if(optionalUser.isEmpty())return ;
        AppRole appRole=AppRole.valueOf(roleName) ;
        Optional<Role> optionalRole=roleRepository.findByRoleName(appRole) ;
        if(optionalRole.isEmpty())return ;
        User user=optionalUser.get() ;
        Role role=optionalRole.get() ;
        user.setRole(role);
        userRepository.save(user) ;
        return ;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll() ;
    }

    @Override
    public UserDTO getUserById(Long userId) {
        Optional<User> optionalUser=userRepository.findById(userId) ;
        if(optionalUser.isEmpty())return null ;
        User user=optionalUser.get() ;
        return convertToDto(user) ;
    }

    private UserDTO convertToDto(User user) {
        return new UserDTO(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.isAccountNonLocked(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired(),
                user.isEnabled(),
                user.getCredentialsExpiryDate(),
                user.getAccountExpiryDate(),
                user.getTwoFactorSecret(),
                user.isTwoFactorEnabled(),
                user.getSignUpMethod(),
                user.getRole(),
                user.getCreatedDate(),
                user.getUpdatedDate()
        );
    }
    @Override
    public User findByUsername(String username) {
        Optional<User> user = userRepository.findByUserName(username);
        return user.orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    @Override
    public void updatePassword(Long userId, String password) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update password");
        }
    }

    @Override
    public void generatePasswordResetToken(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plus(24, ChronoUnit.HOURS);
        PasswordResetToken resetToken = new PasswordResetToken(token, expiryDate, user);
        passwordResetTokenRepository.save(resetToken);

        String resetUrl = frontendUrl + "/reset-password?token=" + token;
        // Send email to user
        emailService.sendPasswordResetEmail(user.getEmail(), resetUrl);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken passwordResetToken=passwordResetTokenRepository.findByToken(token)
                .orElseThrow(()->new RuntimeException("Invalid password reset token")) ;

        if(passwordResetToken.isUsed()){
            throw new RuntimeException("password reset token already used") ;
        }

        if(passwordResetToken.getExpiryDate().isBefore(Instant.now())){
            throw new RuntimeException("password reset token expired") ;
        }

        User user=passwordResetToken.getUser() ;
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user) ;

        passwordResetToken.setUsed(true);
        passwordResetTokenRepository.save(passwordResetToken);

    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email) ;
    }

    @Override
    public User registerUser(User newUser) {
        if(newUser.getPassword()!=null){
            newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        }
        return userRepository.save(newUser) ;
    }


    @Override
    public void updateAccountLockStatus(Long userId, boolean lock) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("User not found"));
        user.setAccountNonLocked(!lock);
        userRepository.save(user);
    }

    @Override
    public void updateAccountExpiryStatus(Long userId, boolean expire) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("User not found"));
        user.setAccountNonExpired(!expire);
        userRepository.save(user);
    }

    @Override
    public void updateAccountEnabledStatus(Long userId, boolean enabled) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("User not found"));
        user.setEnabled(enabled);
        userRepository.save(user);
    }

    @Override
    public void updateCredentialsExpiryStatus(Long userId, boolean expire) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("User not found"));
        user.setCredentialsNonExpired(!expire);
        userRepository.save(user);
    }

//    @Override
//    public GoogleAuthenticatorKey generate2FASecret(Long userId){
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        GoogleAuthenticatorKey key = totpService.generateSecret();
//        user.setTwoFactorSecret(key.getKey());
//        userRepository.save(user);
//        return key;
//    }
//
//    @Override
//    public boolean validate2FACode(Long userId, int code){
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        return totpService.verifyCode(user.getTwoFactorSecret(), code);
//    }
//
//    @Override
//    public void enable2FA(Long userId){
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        user.setTwoFactorEnabled(true);
//        userRepository.save(user);
//    }
//
//    @Override
//    public void disable2FA(Long userId){
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        user.setTwoFactorEnabled(false);
//        userRepository.save(user);
//    }
}