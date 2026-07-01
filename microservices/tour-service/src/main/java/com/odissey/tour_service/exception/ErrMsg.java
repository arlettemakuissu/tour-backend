package com.odissey.tour_service.exception;

// Error Messages
public class ErrMsg {

    // TOUR
    public final static String TOUR_NOT_FOUND = "Tour non trovato";
    public final static String MINPAX_ERROR = "Il numero minimo di partecipanti è maggiore del numero massimo";
    public final static String STARTDATE_ERROR = "La data di partenza del tour non può essere successiva alla data di fine";
    public final static String TOUR_STATUS_NOT_UPDATABLE = "Il tour si trova in uno stato per il quale non è permesso nessun tipo di modifica se non la cancellazione.";
    public final static String NO_PARAMETER_TO_FOUND = "Nessun parametro di ricerca impostato";
    public final static String ONLY_END_DATE_NOT_ALLOWED = "La data di fine tour senza una data di partenza non è consentita";

    // AGENCY
    public final static String AGENCY_NOT_FOUND = "Agenzia non trovata";
    // COUNTRY
    public final static String COUNTRY_NOT_FOUND = "Nazione non trovata";

    // HERO IMAGES
    public final static String EMPTY_IMAGE = "L'immagine è vuota";
    public final static String FILE_TOO_LARGE = "La dimesione del file supera il limite consentito";
    public final static String WRONG_DIMENSIONS_IN_PIXELS = "La larghezze e/o l'altezza dell'immagine non sono corrette";
    public final static String EXTENSION_NOT_ALLOWED = "Estensione file non consentita";
    public final static String TOUR_IMAGE_NOT_UPDATABLE = "Lo status del tour non consente più il caricamento di immagini";
    public final static String CHECKSUM_ERROR = "Impossibile generare la firma del file";
    public final static String IMAGE_ALREADY_PRESENT = "L'immagine è già stata caricata";
}
