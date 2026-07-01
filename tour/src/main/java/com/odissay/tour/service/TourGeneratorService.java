package com.odissay.tour.service;

import com.odissay.tour.exception.Exception401;
import com.odissay.tour.exception.Exception404;
import com.odissay.tour.exception.Exception500;
import com.odissay.tour.model.dto.reponse.TourDetailResponse;
import com.odissay.tour.model.dto.reponse.TourGeneratorResponse;
import com.odissay.tour.model.dto.request.TourGeneratorRequest;
import com.odissay.tour.model.entity.Branch;
import com.odissay.tour.model.entity.Country;
import com.odissay.tour.model.entity.Tour;
import com.odissay.tour.repository.BranchRepository;
import com.odissay.tour.repository.CountryRepository;
import com.odissay.tour.repository.TourRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TourGeneratorService {

private final BranchRepository branchRepository;
private final CountryRepository countryRepository;
private final TourRepository tourRepository;
private final RestTemplate restTemplate;
private final static String URI = "http://Localhost:8090/api/generate";


    public TourDetailResponse generateTour(int branchId ,TourGeneratorRequest req) {
        System.out.println("ooooooooooooooooooooooo");

        Tour tour = null;
        try{
       Branch branch = branchRepository.findByIdAndActiveTrue(branchId)
               .orElseThrow(() ->new Exception404("branch non trovato o non piu attivo"));

       String apiKey = branch.getApiKey();
       if(branch.getApiKey()==null)
           throw new Exception401("la filliala non ha disposizione un apiKey");

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Api-KEY",apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            System.out.println("2222222222222");
       try{
        HttpEntity<TourGeneratorRequest> entity = new HttpEntity<>(req,headers);
            System.out.println("4444444444");
        ResponseEntity<TourGeneratorResponse> response =
                restTemplate.exchange(

                        URI,
                        HttpMethod.POST,
                        entity,
                        TourGeneratorResponse.class);



            System.out.println("55555555555555");


       TourGeneratorResponse tourGeneratorResponse = response.getBody();
       Country country = countryRepository.findByCodeAndActiveTrue(tourGeneratorResponse.getCountryCode())
               .orElseThrow(() ->new Exception404("country non trovato con codice" +tourGeneratorResponse.getCountryCode() ));


        /*
        if(response.getStatusCode().equals(HttpStatus.UNAUTHORIZED))
            throw new Exception400("Api Key errata o scaduta");

        if(!response.getStatuZ2A3SZ2A1  1   sCode().equals(HttpStatus.CREATED))
            throw new Exception500("Qualcosa è andato storto nella generazione del tour da parte di AI");
*/

           LocalDate startDate = LocalDate.now().plusDays(15);
           LocalDate endDate = startDate.plusDays(req.getDuration());
            tour = new Tour(

        branch,country,
                tourGeneratorResponse.getTitle(),
                tourGeneratorResponse.getDescription(),
                    startDate,
                    endDate,
                tourGeneratorResponse.getMinPax(),
                tourGeneratorResponse.getMaxPax(),
                tourGeneratorResponse.getPrice());
            System.out.print(tour);
    tourRepository.save(tour);
  System.out.println("ooooooooooooooooooooooo");
       }catch (Exception e) {

           System.out.println(e.getMessage());
       }
            return TourDetailResponse.fromEntityToDto(tour);

      } catch (HttpClientErrorException.Unauthorized e){
          throw new Exception401("Token non valido o scaduto: filiale non autorizzata all'uso del servizio di AI");
      }




    }


}
