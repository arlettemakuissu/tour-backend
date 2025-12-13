package com.odissay.tour.controller;

import com.odissay.tour.model.dto.request.CustomerRequest;
import com.odissay.tour.model.dto.request.LoginRequest;
import com.odissay.tour.model.dto.request.ResetPasswordRequest;
import com.odissay.tour.model.dto.reponse.CustomErrorResponse;
import com.odissay.tour.model.dto.reponse.LoginResponse;
import com.odissay.tour.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {



    private final AuthService authService;


    @Operation(
            summary = "OTP VERIFICATION",
            description = "Questo metodo serve a verificare l'email dell'utente registrato e ad abilitarlo.",
            tags = {"Auth"},
            responses = {
                    @ApiResponse(responseCode="200", description="Abilitazione avvenuta con successo.", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode="400", description="Uno dei seguenti valori di otp o email non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="404", description="Utente non trovato per coppia email/otp", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PatchMapping // http://localhost:8081/api/auth?otp=xxxxxx&email=xxx@yyy.zzz
    public ResponseEntity<String> otpVerification(
            @RequestParam @Size(min = 6, max = 6) @NotBlank String otp,
            @RequestParam @Email @NotBlank String email
    ){
        return new ResponseEntity<>(authService.otpVerification(otp, email), HttpStatus.OK);
    }


    @Operation(
            summary = "USER LOGIN",
            description = "Questo metodo serve a loggarsi nel sistema.",
            tags = {"Auth"},
            responses = {
                    @ApiResponse(responseCode="200", description="Autenticazione avvenuta con successo.", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
                    @ApiResponse(responseCode="400", description="Uno dei seguenti valori di username o password non è quello atteso.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="401", description="Utente disabilitato e/o non verificato.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="403", description="Credenziali errate.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PostMapping
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest req){
        System.out.println(req.getUsername());

        return new ResponseEntity<>(authService.login(req), HttpStatus.OK);
    }


    @Operation(
            summary = "REQUEST RESET PASSWORD",
            description = "Questo metodo serve a richiedere il reset della password. Si usa se la password è scaduta oppure se l'utente non se la ricorda.",
            tags = {"Auth"},
            responses = {
                    @ApiResponse(responseCode="200", description="Richiesta inviata con successo.", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
                    @ApiResponse(responseCode="400", description="Formato email non valido.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="404", description="Utente non trovato o disabilitato e/o non verificato.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="500", description="Errore nell'invio della richiesta.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PostMapping("/request_reset_password")
    public ResponseEntity<String> requestResetPassword(
            @RequestParam @NotBlank(message = "Email obbligatoria") @Email(message = "Formato email non valido") String email
    ){
        return new ResponseEntity<>(authService.requestResetPassword(email), HttpStatus.OK);
    }

    @Operation(
            summary = "RESET PASSWORD",
            description = "Questo metodo serve a resettare la propria password. Si usa se la password è scaduta oppure se l'utente non se la ricorda.",
            tags = {"Auth"},
            responses = {
                    @ApiResponse(responseCode="200", description="Reset password avvenuto con successo.", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
                    @ApiResponse(responseCode="400", description="I valori della vecchia e/o nuova password non sono quelli attesi oppure il codice otp non è valido.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="409", description="La nuova password scelta è già stata utilizzata in uno dei tre precedenti cambi password.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )
    @PostMapping("/reset_password")
    public ResponseEntity<String> resetPassword(
            @RequestBody @Valid ResetPasswordRequest req
            ){
        return new ResponseEntity<>(authService.resetPassword(req), HttpStatus.OK);
    }

    @Operation(
            summary = "CUSTOMER SIGNUP",
            description = "Questo metodo serve a ad un utente a registrasi sul portale come customer.",
            tags = {"Auth"},
            responses = {
                    @ApiResponse(responseCode="201", description="Utente di tipo customer creato con successo", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode="400", description="I valori di username, email, country, address, city, firstname o lastname non sono quelli attesi oppure il codice otp non è valido.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="404", description="Nazione non trovata o disattivata", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="409", description="Username o email sono già in uso da altro utente.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))),
                    @ApiResponse(responseCode="500", description="Errore nell'invio dell'email di conferma o errore generico di registrazione.", content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
            }
    )

    @PostMapping("/signup")
    public ResponseEntity<String> CostumerSignup(@RequestBody @Valid CustomerRequest req){

        return new ResponseEntity<>(authService.customersignup(req), HttpStatus.CREATED);


    }
}
