package pl.ros.keep.api.auth.dtos;

import pl.ros.keep.api.user.UserDto;

public record AuthResponse(UserDto user, String token) {
}
