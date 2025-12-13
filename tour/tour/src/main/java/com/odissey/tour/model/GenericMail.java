package com.odissey.tour.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class GenericMail {

    private String subject; // soggetto dell'email
    private String body;    // contenuto dell'email
    private String to;      // destinatario dell'email
}
