package com.odissay.tour.controller;

import com.odissay.tour.model.dto.reponse.AgencyResponse;
import com.odissay.tour.model.dto.reponse.BrancheReponse;
import com.odissay.tour.model.dto.request.AgencyRequest;
import com.odissay.tour.model.dto.request.BranchRequest;
import com.odissay.tour.service.BranchService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/branches") //mi cercherà tutto nella tabella countries
@Validated
public class BranchController {

    private final BranchService branchService;

    @PostMapping
    public ResponseEntity<BrancheReponse> create(@RequestBody @Valid BranchRequest req){

        return new ResponseEntity<>(branchService.save(req), HttpStatus.CREATED);
    }


    @GetMapping("/agencies/{agencyId}")
    public ResponseEntity<List<BrancheReponse>> getByAgency(
            @PathVariable @Min(value = 1, message = "L'id della nazione deve essere un numero intero maggiore di 0.") short agencyId
    ) {
        System.out.println(agencyId);
        System.out.println("helllo");
        List<BrancheReponse> list = branchService.getBranchesByAgency(agencyId);


            if(list.isEmpty())
                return new ResponseEntity<>(list,HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(list,HttpStatus.OK);


    }
      @GetMapping("/countries/{countryId}")
      public ResponseEntity<List<BrancheReponse>> getBranchCountry(
              @PathVariable @Min(value = 1, message = "L'id della nazione deve essere un numero intero maggiore di 0.") short countryId
      ){
        List<BrancheReponse> list = branchService.getBranchesByCountry(countryId);

          if(list.isEmpty())
              return new ResponseEntity<>(list,HttpStatus.NOT_FOUND);
          return new ResponseEntity<>(list,HttpStatus.OK);

    }
    @PutMapping("/{id}")
    public ResponseEntity<BrancheReponse> update(@RequestBody @Valid BranchRequest req,
                                                 @PathVariable @Min(value = 1, message = "L'id della nazione deve essere un numero intero maggiore di 0.") int id
    ){

     return new ResponseEntity<>(branchService.update(req,id),HttpStatus.OK);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<Void> switchBranchStatus(
            @PathVariable @Min(value = 1, message = "L'id della nazione deve essere un numero intero maggiore di 0.") int id
    ){
      branchService.switchBranchStatus(id);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }
      public ResponseEntity<BrancheReponse> getBranch(
              @PathVariable @Min(value = 1, message = "L'id della nazione deve essere un numero intero maggiore di 0.") int id
      ){

       return new ResponseEntity<>(branchService.getBranch(id),HttpStatus.OK) ;

    }
}
