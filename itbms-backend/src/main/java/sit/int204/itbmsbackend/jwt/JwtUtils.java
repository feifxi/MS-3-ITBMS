package sit.int204.itbmsbackend.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import sit.int204.itbmsbackend.entities.User;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    @Autowired
    private Environment environment;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${team.code:ms3}")
    private String teamCode;

    @Value("${jwt.access-token-expiration_ms}")
    private int jwtExpirationMs;

    @Value("${app.frontend.url.dev:http://localhost:5173}")
    private String devFrontendUrl;

    @Value("${app.frontend.url.prod:http://intproj24.sit.kmutt.ac.th}")
    private String prodFrontendUrl;

    private String getHostPath() {
        String[] activeProfiles = environment.getActiveProfiles();
        boolean isDevMode = activeProfiles.length > 0 && activeProfiles[0].equals("dev");
        return isDevMode ? devFrontendUrl : prodFrontendUrl;
    }

    public String generateJwtToken(User user) {
        String hostPath = getHostPath() + "/" + teamCode + "/";
        return Jwts.builder()
                .setIssuer(hostPath)
                .setSubject(user.getEmail())    // generate with user email
                .claim("nickname", user.getNickname())  // custom claim
                .claim("id", user.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getEmailFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}