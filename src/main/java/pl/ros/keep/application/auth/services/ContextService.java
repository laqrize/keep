package pl.ros.keep.application.auth.services;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.ros.keep.application.users.services.UserService;
import pl.ros.keep.core.users.AppUser;

@Component
public class ContextService {

    @Value("${system.user.email}")
    private String systemUserEmail;

    @Lazy
    @Autowired
    private UserService appUserService;

    public AppUser getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof AppUser appUser) {
            return appUser;
        }
        throw new IllegalStateException("No user in context");
    }

    public boolean isPrincipalPresent() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof AppUser;
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public void setSystemUserInContext() {
        setUserInContext(systemUserEmail);
    }
    public void setUserInContext(@NonNull String email) {
        AppUser user = appUserService.findEntityByEmail(email);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
