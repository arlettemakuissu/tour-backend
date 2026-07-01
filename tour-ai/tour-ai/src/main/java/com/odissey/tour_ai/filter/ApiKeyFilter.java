package com.odissey.tour_ai.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odissey.tour_ai.dto.CustomErrorResponse;
import com.odissey.tour_ai.ripository.ClientRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApiKeyFilter extends OncePerRequestFilter {

private final ClientRepository clientRepository;
private final ObjectMapper objectMapper;



    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String apiKey = request.getHeader("X-api-key");

        if (apiKey == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            CustomErrorResponse customErrorResponse = CustomErrorResponse.getCustomErrorResponse(
                    "https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#403",
                    HttpStatus.FORBIDDEN,
                    request.getRequestURI(),
                    "ACCESSO NEGATO"
            );
            String jsonError = objectMapper.writeValueAsString(customErrorResponse);
            response.getWriter().write(jsonError);
            return; // NON chiamare filterChain.doFilter()
        }

        if(clientRepository.existsByApiKeyAndExpirationDateAfter(apiKey, LocalDate.now())){

            filterChain.doFilter(request,response);
        }else{

            // Gestisci api non valido/scaduto
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            CustomErrorResponse customErrorResponse = CustomErrorResponse.getCustomErrorResponse(
                    "https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#403",
                    HttpStatus.FORBIDDEN,
                    request.getRequestURI(),
                    "ACESSO NEGATO -  API KEY MANCANTE."
            );
            String jsonError = objectMapper.writeValueAsString(customErrorResponse);
            response.getWriter().write(jsonError);
        }


    }

    }
