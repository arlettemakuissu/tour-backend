package com.odissey.tour.service;

import com.odissey.tour.exception.Exception404;
import com.odissey.tour.exception.Exception409;
import com.odissey.tour.exception.Exception422;
import com.odissey.tour.model.dto.request.BranchRequest;
import com.odissey.tour.model.dto.response.BranchResponse;
import com.odissey.tour.model.entity.Agency;
import com.odissey.tour.model.entity.Branch;
import com.odissey.tour.repository.AgencyRepository;
import com.odissey.tour.repository.BranchRepository;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;
    private final AgencyRepository agencyRepository;

    public BranchResponse save(BranchRequest req){
        // verificare esistenza dell'agenzia di riferimento
        Agency agency = agencyRepository.findAgencyByIdWithCountry(req.getAgencyId())
                .orElseThrow(()-> new Exception404("Nessuna agenzia trova con id "+req.getAgencyId()));
        // verificare che l'agenzia sia attiva
        if(!agency.isActive())  // 422 Unprocessable Content: The request was well-formed (i.e., syntactically correct) but could not be processed
            throw new Exception422("L'agenzia di riferimento è disattivata");
        // verificare il vincolo di unicità name, agency_id
        if(branchRepository.existsByNameAndAgencyId(req.getName(), agency.getId()))
            throw new Exception409("Una filiale con lo stesso nome e dipendente dalla stessa agenzia esiste già");
        if(branchRepository.existsByVat(req.getVat().trim()))
            throw new Exception409("Una filiale con lo stesso vat è già presente a sistema");
        Branch branch = new Branch(
                req.getName().trim(),
                req.getCity().trim(),
                req.getAddress().trim(),
                req.getVat().trim(),
                agency
        );
        branchRepository.save(branch);

        return BranchResponse.fromEntityToDto(branch);
    }

    public List<BranchResponse> getBranchesByAgency(int agencyId) {
        return branchRepository.getBranchesByAgency(agencyId);
    }

    public List<BranchResponse> getBranchesByCountry(short countryId){
        return branchRepository.getBranchesByCountry(countryId);
    }

    public BranchResponse update(BranchRequest req, int id){
        Branch branch = branchRepository.findById(id)
                .orElseThrow(()-> new Exception404("Nessuna filiale trovata con id "+id));
        Agency agency = agencyRepository.findAgencyByIdWithCountry(req.getAgencyId())
               .orElseThrow(()-> new Exception404("Nessuna agenzia trovata con id "+req.getAgencyId()));

        String branchName = req.getName().trim();
        String vat = req.getVat().trim();

        // verifico che non esista già una filiale con lo stesso nome dipendente dalla stessa agenzia
        if(branchRepository.existsByNameAndAgencyIdAndIdNot(branchName, agency.getId(), id))
            throw new Exception409("Esiste già una filiale con lo stesso nome dipendente dalla stessa agenzia");
        // verifico che non esista già una filiale con lo stesso vat
        if(branchRepository.existsByVatAndIdNot(vat, id))
            throw new Exception409("Una filiale con lo stesso vat esiste già a sistema");

        branch.setName(branchName);
        branch.setCity(req.getCity().trim());
        branch.setAddress(req.getAddress().trim());
        branch.setAgency(agency);
        branch.setVat(vat);

        branchRepository.save(branch);

        return BranchResponse.fromEntityToDto(branch);

    }

    public void switchBranchStatus(int id){
        Branch branch = branchRepository.findById(id)
                .orElseThrow(()-> new Exception404("Nessuna filiale trovata con id "+id));
        branch.setActive(!branch.isActive());
        branchRepository.save(branch);
    }

    public BranchResponse getBranch(int id) {
        return branchRepository.getBranch(id)
                .orElseThrow(()-> new Exception404("Filiale non trovata"));
    }
}
