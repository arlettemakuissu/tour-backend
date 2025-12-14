\# Tour Backend



Backend Spring Boot per la gestione dei tour per Odissey Agency, dockerizzato per facilitare il deployment.



\## Tecnologie utilizzate



\- Java 17

\- Spring Boot 3.5

\- Spring Data JPA

\- MySQL

\- Docker \& Docker Compose

\- Maven

\- Lombok

\- Spring Security

\- JWT per l'autenticazione



\## Struttura del progetto



\- `src/main/java/com/odissay/tour` → codice sorgente

\- `Dockerfile` → creazione dell’immagine Docker

\- `docker-compose.yml` → configurazione di Docker Compose

\- `.gitignore` → file ignorati da Git

\- `pom.xml` → dipendenze Maven



\## Installazione e avvio



1\. Clonare il repository:



git clone https://github.com/arlettemakuissu/tour-backend.git

cd tour-backend/tour

Costruire e avviare con Docker Compose:






docker-compose up --build

API

Tutte le rotte sono documentate con Swagger:



http://localhost:8081/api/swagger-ui/swagger-ui/index.html#/



