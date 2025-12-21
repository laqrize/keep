package pl.ros.keep.application.users.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.ros.keep.api.auth.dtos.*;
import pl.ros.keep.api.enums.UserType;
import pl.ros.keep.api.user.UserDto;
import pl.ros.keep.application.auth.converters.UserConverter;
import pl.ros.keep.application.auth.services.ContextService;
import pl.ros.keep.core.users.AppUser;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final IJwtService jwtService;
    private final ContextService contextService;
    private final UserConverter userConverter; //nw czy to nie antypattern

    public AuthResponse signup(SignupRequest request) {
        UserDto userDto = addUser(request);
        return signin(SigninRequest.builder()
                .username(request.username())
                .password(request.password())
                .build());
    }

    private UserDto addUser(SignupRequest request) {
        UserDto userDto = UserDto.builder()
                .email(request.username())
                .username(request.username())
                .passwordHash(passwordEncoder.encode(request.password()))
                .type(UserType.USER)
                .enabled(true)
                .locked(false)
                .build();
        if (!contextService.isPrincipalPresent()) {
            contextService.setSystemUserInContext();
        }

        return userService.create(userDto);
    }

    public AuthResponse signin(SigninRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        AppUser userDetails = userService.findEntityByEmail(request.username());
        String token = jwtService.generateToken(userDetails);
        return new AuthResponse(userConverter.toDto(userDetails), token);

    }

    public JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String userEmail = jwtService.getUsernameFromToken(refreshTokenRequest.token());
        UserDetails userDetails = userService.findEntityByEmail(userEmail);
        if (!jwtService.isTokenValid(refreshTokenRequest.token(), userDetails)) {
            throw new IllegalArgumentException("Invalid token");
        }
        String token = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateToken(userDetails);
        return JwtAuthenticationResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .build();
    }

}
