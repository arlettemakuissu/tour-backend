package com.odissey.gateway.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Validated
@ConfigurationProperties(prefix = "gateway-security")
//@EnableConfigurationProperties(GatewaySecurityProperties.class)
public class GatewaySecurityProperties {

    private final Jwt jwt = new Jwt();

    private List<String> publicPaths = new ArrayList<>();

    @NotEmpty
    private List<Rule> rules = new ArrayList<>();

    public Jwt getJwt() {
        return jwt;
    }
    public List<String> getPublicPaths() {
        return publicPaths;
    }
    public void setPublicPaths(List<String> publicPaths) {
        this.publicPaths = publicPaths;
    }

    public List<Rule> getRules() {
        return rules;
    }
    public void setRules(List<Rule> rules) { this.rules = rules; }


    public static class Jwt {
        @NotBlank private String issuer;
        @NotBlank private String audience;
        @NotBlank private String hmacSecret;
        @NotBlank private String rolesClaim = "roles";
        @NotBlank private String subjectClaim = "sub";

        public String getIssuer() { return issuer; }
        public void setIssuer(String issuer) { this.issuer = issuer; }

        public String getAudience() { return audience; }
        public void setAudience(String audience) { this.audience = audience; }

        public String getHmacSecret() { return hmacSecret; }
        public void setHmacSecret(String hmacSecret) { this.hmacSecret = hmacSecret; }

        public String getRolesClaim() { return rolesClaim; }
        public void setRolesClaim(String rolesClaim) { this.rolesClaim = rolesClaim; }

        public String getSubjectClaim() { return subjectClaim; }
        public void setSubjectClaim(String subjectClaim) { this.subjectClaim = subjectClaim; }
    }

    public static class Rule {
        @NotBlank
        private String path;

        @NotEmpty
        private List<String> methods = new ArrayList<>();

        @NotEmpty
        private List<String> roles = new ArrayList<>();

        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }

        public List<String> getMethods() { return methods; }
        public void setMethods(List<String> methods) { this.methods = methods; }

        public List<String> getRoles() { return roles; }
        public void setRoles(List<String> roles) { this.roles = roles; }
    }
}

