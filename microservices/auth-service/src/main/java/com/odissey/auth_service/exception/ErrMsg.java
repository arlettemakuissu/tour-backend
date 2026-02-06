package com.odissey.auth_service.exception;

// Error Messages
public class ErrMsg {

    // USER
    public final static String EMAIL_TAKEN = "Email già in uso";
    public final static String USERNAME_TAKEN = "Username già in uso";
    public final static String USER_NOT_FOUND = "Utente non trovato";
    public final static String BAD_CREDENTIALS = "Credenziali errate";
    public final static String INVALID_ROLE = "Ruolo inesistente";
    public final static String USER_DISABLED = "Utente disabilitato";
    public final static String FORBIDDEN_CHANGE_ENABLE_FLAG = "Non puoi disabilitare te stesso";
    public final static String TERMS_NOT_ACCEPTED = "Per potersi registrare è obbligatorio accettare i termini di servizio";
    public final static String EMAIL_NOT_SENT = "Si è verificato un errore nell'invio dell'email di conferma";
    public final static String ALREADY_CONFIRMED = "Hai già provveduto a confermare la tua registrazione";
    public static final String INVALID_OTP = "Codice otp errato";
    public static final String OTP_EXPIRED = "Codice otp scaduto";
    // JWT
    public final static String UNSIGNABLE_JWT = "Non è possibile firmare il token";
    public final static String INVALID_REFRESH_TOKEN = "Refresh token non valido";
    public final static String REFRESH_TOKEN_REVOKED = "Refresh token revocato";
    public final static String REFRESH_TOKEN_EXPIRED = "Refresh token scaduto";


}
