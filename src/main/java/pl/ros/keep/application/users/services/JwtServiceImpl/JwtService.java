package pl.ros.keep.application.users.services.JwtServiceImpl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pl.ros.keep.application.users.services.IJwtService;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService implements IJwtService {

    @Value("${jwt.expiration.minutes:15}")
    private int expirationMinutes;

    @Value("${jwt.refresh.token.expiration.multiplier:7}")
    private int jwtRefreshTokenExpirationMultiplier;

    @Value("${jwt.signing.key:}")
    private String jwtSignKey;

    private static final String AUTHORITIES = "authorities";


    public String generateToken(UserDetails userDetails) {
        return Jwts.builder().setSubject(userDetails.getUsername())
                .claim(AUTHORITIES, userDetails.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(LocalDateTime.now()
                        .plusMinutes(expirationMinutes).toInstant(ZoneOffset.UTC)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

    }

    public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .claim(AUTHORITIES, userDetails.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(LocalDateTime.now().plusMinutes(expirationMinutes * jwtRefreshTokenExpirationMultiplier)
                        .toInstant(ZoneOffset.UTC)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = getClaimFromToken(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    public  <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token).getBody();
    }

    private SecretKey getSigningKey() {
        byte[] key = Decoders.BASE64.decode(jwtSignKey);
        return Keys.hmacShaKeyFor(key);
    }

}
