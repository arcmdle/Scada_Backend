package com.arcsolutions.scada_backend.application.services;

import com.arcsolutions.scada_backend.application.mappers.UserMapper;
import com.arcsolutions.scada_backend.domain.User;
import com.arcsolutions.scada_backend.domain.UserRepository;
import com.arcsolutions.scada_backend.domain.services.UserService;
import com.arcsolutions.scada_backend.infrastructure.dtos.UpdateUserDto;
import com.arcsolutions.scada_backend.infrastructure.dtos.UserResponseDto;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponseDto findById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User not found")
        );
        return UserMapper.toDto(user);
    }

    @Override
    public UserResponseDto findByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("Email does not registered")
        );
        return UserMapper.toDto(user);
    }

    @Override
    public UserResponseDto update(UpdateUserDto dto, UUID id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("Id don't belong to any user")
        );
        UserMapper.updateFromDto(dto, user);
        User savedUser = userRepository.save(user);
        return UserMapper.toDto(savedUser);

    }

    @Override
    public void changeAvatar(String avatarUrl, UUID id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User Not Found")
        );
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);

    }
}
