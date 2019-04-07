package com.github.rudylucky.auth.common.util;

import com.github.rudylucky.auth.common.AuthConstants;
import com.github.rudylucky.auth.common.user.LoginUser;
import com.github.rudylucky.auth.dao.entity.UserDbo;
import com.github.rudylucky.auth.dto.UserDTO;
import com.github.rudylucky.auth.enums.UserTypeEnum;
import com.github.rudylucky.auth.common.exception.AuthBlankParamException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.github.rudylucky.auth.common.AuthConstants.EXPIRATION_DAYS;


public class CommonUtils {

    public static Boolean isAdminUser(String username) {
        return StringUtils.equalsIgnoreCase(username, AuthConstants.ADMIN);
    }

    public static Boolean isAdminUser(UserDbo user) {
        return isAdminUser(user.getUsername());
    }

    public static Boolean isAdminUser(LoginUser user) {
        return isAdminUser(user.getUsername());
    }

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(5));
    }

    public static String getDefaultRoleName(String username) {
        return String.format("_%s", username);
    }

    public static Timestamp getPasswordExpirationTimestamp() {
        LocalDate now = LocalDate.now();
        Period p = Period.ofDays((Integer) SystemConfig.get(EXPIRATION_DAYS));
        LocalDate expiration = now.plus(p);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = expiration.atStartOfDay(zoneId);
        return Timestamp.from(zdt.toInstant());
    }

    public static Timestamp getScriptUserPasswordExpirationTimestamp() {
        LocalDate now = LocalDate.now();
        Period p = Period.ofYears(10);
        LocalDate expiration = now.plus(p);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = expiration.atStartOfDay(zoneId);
        return Timestamp.from(zdt.toInstant());
    }

    public static Timestamp getPasswordExpirationTimestamp(UserTypeEnum userType) {
        switch (userType) {
            case SCRIPT:
                return getScriptUserPasswordExpirationTimestamp();
            default:
                return getPasswordExpirationTimestamp();
        }
    }

    public static Boolean checkPassword(String password, String hashPassword) {
        return BCrypt.checkpw(password, hashPassword);
    }

    public static Optional<String> generateToken(UserDTO user, String secret, String issuer) {
        if (user.getLocked() || user.getExpired())
            return Optional.empty();
        String[] roles = user.getRoleName().toArray(new String[user.getRoleName().size()]);
        return Optional.of(generateToken(user.getUsername(), roles, secret, issuer));
    }

    public static String generateToken(String username, String[] roles, String secret, String issuer) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("roles", roles);
        long expMillis = System.currentTimeMillis() + 90 * 24 * 60 * 60 * 1000L;
        claims.put("sub", username);
        claims.put("exp", new Date(expMillis));
        claims.put("iss", issuer);
        //claims.put("nbf", new Date());
        claims.put("iat", new Date());
        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();
    }

    public static void checkBlankParam(Map<String, String> params) {
        String[] paramNames = params.entrySet().stream()
                .filter(p -> StringUtils.isBlank(p.getValue())).map(Map.Entry::getKey).distinct().toArray(String[]::new);

        if (paramNames.length > 0)
            throw new AuthBlankParamException(paramNames);
    }
}
