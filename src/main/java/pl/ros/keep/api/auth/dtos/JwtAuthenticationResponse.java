package pl.ros.keep.api.auth.dtos;

import lombok.Builder;

@Builder
public record JwtAuthenticationResponse(String token, String refreshToken) {
}
