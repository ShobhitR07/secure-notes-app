package com.secure.notes.controllers;


import com.secure.notes.dtos.UserDTO;
import com.secure.notes.models.Role;
import com.secure.notes.models.User;
import com.secure.notes.repositories.RoleRepository;
import com.secure.notes.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
//@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    @Autowired
    UserService userService ;

    @Autowired
    RoleRepository roleRepository ;

//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long userId){
        UserDTO userDTO= userService.getUserById(userId) ;
        if(userDTO==null){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND) ;
        }
        return new ResponseEntity<>(userDTO,HttpStatus.OK) ;
    }

//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/getusers")
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> list=userService.getAllUsers() ;
        return new ResponseEntity<>(list,HttpStatus.OK) ;

    }

    @PutMapping("/update-role")
    public ResponseEntity<String> updateUser(@RequestParam Long userId,@RequestParam String roleName){
        userService.updateUserRole(userId,roleName);
        return new ResponseEntity<>("update successful",HttpStatus.OK) ;
    }

    @PutMapping("/update-lock-status")
    public ResponseEntity<String> updateAccountLockStatus(@RequestParam Long userId, @RequestParam boolean lock) {
        userService.updateAccountLockStatus(userId, lock);
        return ResponseEntity.ok("Account lock status updated");
    }

    @GetMapping("/roles")
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @PutMapping("/update-expiry-status")
    public ResponseEntity<String> updateAccountExpiryStatus(@RequestParam Long userId, @RequestParam boolean expire) {
        userService.updateAccountExpiryStatus(userId, expire);
        return ResponseEntity.ok("Account expiry status updated");
    }

    @PutMapping("/update-enabled-status")
    public ResponseEntity<String> updateAccountEnabledStatus(@RequestParam Long userId, @RequestParam boolean enabled) {
        userService.updateAccountEnabledStatus(userId, enabled);
        return ResponseEntity.ok("Account enabled status updated");
    }

    @PutMapping("/update-credentials-expiry-status")
    public ResponseEntity<String> updateCredentialsExpiryStatus(@RequestParam Long userId, @RequestParam boolean expire) {
        userService.updateCredentialsExpiryStatus(userId, expire);
        return ResponseEntity.ok("Credentials expiry status updated");
    }

    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestParam Long userId, @RequestParam String password) {
        try {
            userService.updatePassword(userId, password);
            return ResponseEntity.ok("Password updated");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
