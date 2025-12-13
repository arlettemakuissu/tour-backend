package com.odissay.tour.service;

import com.odissay.tour.exception.Exception404;
import com.odissay.tour.exception.Exception409;
import com.odissay.tour.model.dto.reponse.CustomerDetailResponse;
import com.odissay.tour.model.dto.reponse.CustomerListResponse;
import com.odissay.tour.model.dto.reponse.CustomerResponse;
import com.odissay.tour.model.dto.request.CustomerUpdateRequest;
import com.odissay.tour.model.entity.Country;
import com.odissay.tour.model.entity.Customer;
import com.odissay.tour.model.entity.emurator.Role;
import com.odissay.tour.model.entity.User;
import com.odissay.tour.repository.CountryRepository;
import com.odissay.tour.repository.CustomerRepository;
import com.odissay.tour.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
    private CustomerListResponse customerListResponse;
    private  final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final CustomerRepository customerRepository;

    public CustomerResponse updateCustomer(UserDetails userDetails, CustomerUpdateRequest req){
        // verificare che email e username non siano già in uso da altro utente
        try {
           System.out.println("eeeeee") ;
        User user = (User) userDetails;
            System.out.println("gggggggg") ;

        String username = req.getUsername().trim();
        String email = req.getEmail().trim();
            System.out.println("eeeuueee") ;
        if(userRepository.existsByIdNotAndUsernameOrEmail(user.getId(), username, email)>0 )
            throw new Exception409("Un utente con email "+email+" o username "+username+" esiste già a sistema.");
            System.out.println("yyyyyy") ;
        user.setEmail(req.getEmail());
        user.setUsername(req.getUsername());
        user.setFirstname(req.getFirstname().trim());
        user.setLastname(req.getLastname().trim());

        // verificare esistenza Country
        Country country = countryRepository.findByIdAndActiveTrue(req.getCountryId())
                .orElseThrow(()-> new Exception404("Nazione non trovata con id "+req.getCountryId()));
            System.out.println("uuuuu") ;
        // istanziare oggetto customer...
        Customer customer = new Customer();
        customer.setAddress(req.getAddress());
        customer.setCity(req.getCity());
        customer.setCountry(country);
            System.out.println("eeeeee") ;
        // ... e settarlo su User
        user.setCustomer(customer);
            System.out.println("kkkkkk") ;
        // aggiornare l'utente/customer
        userRepository.save(user);

            return CustomerResponse.fromEntityToDto(user);
        } catch ( Exception e){
            System.out.println(e.getMessage());
            return null;
        }



    }

    public List<CustomerListResponse> getActiveCustomersByLastLoginAndCountry() {
        return customerRepository.getActiveCustomersByLastLoginAndCountry(Role.CUSTOMER);
    }

    public CustomerDetailResponse getActiveCustomerByLastLoginAndCountryAndId(int id) {
        return customerRepository.getActiveCustomerByLastLoginAndCountryAndId(id, Role.CUSTOMER)
                .orElseThrow(()-> new Exception404("Customer non trovato con id "+id)); }
}
