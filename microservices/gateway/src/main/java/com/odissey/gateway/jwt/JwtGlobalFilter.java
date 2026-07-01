package com.odissey.gateway.jwt;

import com.odissey.gateway.config.GatewaySecurityProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtGlobalFilter extends OncePerRequestFilter implements Ordered {

    private final GatewaySecurityProperties props;
    private final JwtVerifier jwtVerifier;
    private final AntPathMatcher matcher = new AntPathMatcher();

    public JwtGlobalFilter(GatewaySecurityProperties props) {
        this.props = props;
        this.jwtVerifier = new JwtVerifier(
                props.getJwt().getHmacSecret(),
                props.getJwt().getIssuer(),
                props.getJwt().getAudience(),
                props.getJwt().getRolesClaim(),
                props.getJwt().getSubjectClaim()
        );
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        HttpMethod method = parseMethod(request.getMethod());

        if (HttpMethod.OPTIONS.equals(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (isPublic(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = extractBearer(authHeader);
        if (token == null) {
            deny(response, HttpStatus.UNAUTHORIZED, "missing_token", "Missing Bearer token", path);
            return;
        }

        JwtVerifier.VerifiedUser user;
        try {
            user = jwtVerifier.verifyAndExtract(token);
        } catch (JwtVerifier.JwtValidationException e) {
            deny(response, HttpStatus.UNAUTHORIZED, "invalid_token", e.getMessage(), path);
            return;
        }

        Set<String> requiredRoles = requiredRolesFor(path, method);
        if (!requiredRoles.isEmpty()) {
            boolean allowed = user.getRoles().stream().anyMatch(requiredRoles::contains);
            if (!allowed) {
                deny(response, HttpStatus.FORBIDDEN, "forbidden", "Insufficient role", path);
                return;
            }
        } else {
            deny(response, HttpStatus.FORBIDDEN, "forbidden", "No rule configured for endpoint", path);
        }

        Map<String, List<String>> extraHeaders = new LinkedHashMap<>();
        extraHeaders.put("X-User-Id", List.of(user.getUserId()));
        //extraHeaders.put("X-User-Roles", List.of(String.join(",", user.getRoles())));

        HttpServletRequest wrapped = new AdditionalHeadersRequestWrapper(request, extraHeaders);
        filterChain.doFilter(wrapped, response);
    }

    private boolean isPublic(String path) {
        for (String p : props.getPublicPaths()) {
            if (matcher.match(p, path)) {
                return true;
            }
        }
        return false;
    }

    private String extractBearer(String authHeader) {
        if (authHeader == null) {
            return null;
        }
        if (!authHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return null;
        }
        String token = authHeader.substring(7).trim();
        return token.isEmpty() ? null : token;
    }

    private Set<String> requiredRolesFor(String path, HttpMethod method) {
        if (method == null) {
            return Set.of();
        }
        String m = method.name();

        List<GatewaySecurityProperties.Rule> matches = props.getRules().stream()
                .filter(r -> matcher.match(r.getPath(), path))
                .filter(r -> r.getMethods().stream().anyMatch(mm -> mm.equalsIgnoreCase(m)))
                .toList();

        if (matches.isEmpty()) {
            return Set.of();
        }

        return matches.stream()
                .flatMap(r -> r.getRoles().stream())
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toUnmodifiableSet());
    }

    private void deny(HttpServletResponse response, HttpStatus status, String code, String message, String path)
            throws IOException {
        byte[] body = ("""
      {"error":"%s","message":"%s","path":"%s"}
      """.formatted(code, escapeJson(message), escapeJson(path)))
                .getBytes(StandardCharsets.UTF_8);

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        response.setContentLength(body.length);
        response.getOutputStream().write(body);
    }

    private String escapeJson(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private HttpMethod parseMethod(String method) {
        if (method == null) {
            return null;
        }
        try {
            return HttpMethod.valueOf(method);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    @Override
    public int getOrder() {
        // run early, before other filters
        return -100;
    }

    private static class AdditionalHeadersRequestWrapper extends HttpServletRequestWrapper {
        private final Map<String, List<String>> additionalHeaders;

        AdditionalHeadersRequestWrapper(HttpServletRequest request, Map<String, List<String>> additionalHeaders) {
            super(request);
            this.additionalHeaders = new LinkedHashMap<>(additionalHeaders);
        }

        @Override
        public String getHeader(String name) {
            List<String> values = additionalHeaders.get(name);
            // Controllo che la lista esista e non sia vuota per evitare IndexOutOfBoundsException
            if (values != null && !values.isEmpty()) {
                return values.get(0);
            }
            return super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            List<String> values = additionalHeaders.get(name);
            if (values != null) {
                return Collections.enumeration(values);
            }
            return super.getHeaders(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            Collection<String> parent = Collections.list(super.getHeaderNames());
            List<String> names = new ArrayList<>(parent);
            for (String key : additionalHeaders.keySet()) {
                if (!names.contains(key)) {
                    names.add(key);
                }
            }
            return Collections.enumeration(names);
        }
    }
}
