package com.odissey.auth_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
public class GenericMail {

    private String subject;//soggetto della mail
    private String body; // contenuto della mail
    private String to;   // destinatario della maoil
}
