package com.example.myexpensetracker.services;

import com.example.myexpensetracker.persistence.entities.ProfileEntity;
import com.example.myexpensetracker.persistence.entities.UserEntity;
import com.example.myexpensetracker.persistence.repositories.ProfileRepository;
import com.example.myexpensetracker.persistence.repositories.UserRepository;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Log4j2
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    public boolean usernameExists(@NonNull String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional
    public UserEntity create(@NonNull String username, @NonNull String password) {
        log.debug("Creating user: {}", username);
        UUID id = UUID.randomUUID();
        UserEntity userEntity = new UserEntity(id, username, password, new ProfileEntity(id));
        profileRepository.save(userEntity.getProfile());
        userRepository.save(userEntity);
        return userEntity;
    }

}
