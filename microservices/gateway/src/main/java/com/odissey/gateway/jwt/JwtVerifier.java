package com.odissey.gateway.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.util.*;

public class JwtVerifier {

    private final MACVerifier verifier;
    private final String issuer;
    private final String audience;
    private final String rolesClaim;
    private final String subjectClaim;

    public JwtVerifier(String hmacSecret, String issuer, String audience, String rolesClaim, String subjectClaim) {
        // HS256 requires >= 256-bit key -> 32+ chars
        try {
            this.verifier = new MACVerifier(hmacSecret.getBytes(StandardCharsets.UTF_8));
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        this.issuer = issuer;
        this.audience = audience;
        this.rolesClaim = rolesClaim;
        this.subjectClaim = subjectClaim;
    }

    public static class VerifiedUser {
        private final String userId;
        private final Set<String> roles;

        public VerifiedUser(String userId, Set<String> roles) {
            this.userId = userId;
            this.roles = roles;
        }

        public String getUserId() { return userId; }
        public Set<String> getRoles() { return roles; }
    }

    public VerifiedUser verifyAndExtract(String rawToken) throws JwtValidationException {
        try {
            SignedJWT jwt = SignedJWT.parse(rawToken);

            if (!jwt.verify(verifier)) {
                throw new JwtValidationException("Invalid token signature");
            }

            var claims = jwt.getJWTClaimsSet();

            // exp
            Date exp = claims.getExpirationTime();
            if (exp == null || exp.toInstant().isBefore(Instant.now())) {
                throw new JwtValidationException("Token expired");
            }

            // issuer
            String iss = claims.getIssuer();
            if (iss == null || !iss.equals(issuer)) {
                throw new JwtValidationException("Invalid issuer");
            }

            // audience
            List<String> aud = claims.getAudience();
            if (aud == null || aud.stream().noneMatch(audience::equals)) {
                throw new JwtValidationException("Invalid audience");
            }

            // subject
            String sub = Optional.ofNullable(claims.getStringClaim(subjectClaim)).orElse(claims.getSubject());
            if (sub == null || sub.isBlank()) {
                throw new JwtValidationException("Missing subject");
            }

            // roles
            Set<String> roles = extractRoles(claims.getClaim(rolesClaim));

            return new VerifiedUser(sub, roles);

        } catch (ParseException e) {
            throw new JwtValidationException("Malformed token");
        } catch (JOSEException e) {
            throw new JwtValidationException("Token verification error");
        }
    }

    private Set<String> extractRoles(Object claimValue) throws JwtValidationException {
        if (claimValue == null) return Set.of();

        if (claimValue instanceof String s) {
            // support "USER,ADMIN" or "USER"
            return Arrays.stream(s.split(","))
                    .map(String::trim)
                    .filter(x -> !x.isBlank())
                    .collect(java.util.stream.Collectors.toUnmodifiableSet());
        }

        if (claimValue instanceof Collection<?> col) {
            Set<String> out = new HashSet<>();
            for (Object o : col) {
                if (o != null) out.add(o.toString().trim());
            }
            out.removeIf(String::isBlank);
            return Collections.unmodifiableSet(out);
        }

        throw new JwtValidationException("Unsupported roles claim format");
    }



    public static class JwtValidationException extends Exception {
        public JwtValidationException(String message) { super(message); }
    }
}

