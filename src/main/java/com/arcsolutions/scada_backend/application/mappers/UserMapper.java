package com.arcsolutions.scada_backend.application.mappers;

import com.arcsolutions.scada_backend.domain.Role;
import com.arcsolutions.scada_backend.domain.User;
import com.arcsolutions.scada_backend.infrastructure.dtos.CreateUserDto;
import com.arcsolutions.scada_backend.infrastructure.dtos.LoginRequestDto;
import com.arcsolutions.scada_backend.infrastructure.dtos.UpdateUserDto;
import com.arcsolutions.scada_backend.infrastructure.dtos.UserResponseDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;


public class UserMapper {
    private UserMapper() {
        throw new UnsupportedOperationException("This class should never be instantiated");
    }

    public static User fromDto(CreateUserDto dto, String encodedPassword) {
        return User.builder()
                .email(dto.email())
                .fullName(dto.fullName())
                .password(encodedPassword)
                .phone(dto.phone())
                .description(dto.description())
                .role(Role.USER)
                .build();

    }


    public static Authentication fromDto(final LoginRequestDto loginRequestDto) {
        return new UsernamePasswordAuthenticationToken(loginRequestDto.email(), loginRequestDto.password());
    }

    public static UserResponseDto toDto(final User user) {
        return new UserResponseDto(user.getId(), user.getFullName(), user.getEmail(), user.getRole(), user.getPhone(), user.getDescription(), user.getAvatarUrl());
    }

    public static void updateFromDto(final UpdateUserDto updateUserDto, final User user) {

        if (updateUserDto.description() != null) {
            user.setDescription(updateUserDto.description());
        }
        if (updateUserDto.phone() != null) {
            user.setPhone(updateUserDto.phone());
        }
        if (updateUserDto.fullName() != null) {
            user.setFullName(updateUserDto.fullName());
        }
    }

}
