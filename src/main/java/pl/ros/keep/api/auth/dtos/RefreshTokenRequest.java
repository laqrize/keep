package pl.ros.keep.api.auth.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RefreshTokenRequest (@NotBlank String token){

}
