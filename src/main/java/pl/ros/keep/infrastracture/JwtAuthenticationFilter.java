package pl.ros.keep.infrastracture;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.ros.keep.application.users.services.IJwtService;
import pl.ros.keep.application.users.services.UserService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final IJwtService jwtService;
    private final UserService userService;

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);

        if (StringUtils.isEmpty(authorizationHeader) || !authorizationHeader.toUpperCase().startsWith(BEARER.toUpperCase())) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authorizationHeader.substring(BEARER.length());
        String username = jwtService.getUsernameFromToken(jwt);

        if (StringUtils.isEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
            filterChain.doFilter(request, response);
            return;
        }
        UserDetails userDetails = userService.userDetailsService().loadUserByUsername(username);

        if (jwtService.isTokenValid(jwt, userDetails)) {
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

            UsernamePasswordAuthenticationToken authenticationToken = new
                    UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            securityContext.setAuthentication(authenticationToken);
            SecurityContextHolder.setContext(securityContext);
        }
        filterChain.doFilter(request, response);
    }

}
