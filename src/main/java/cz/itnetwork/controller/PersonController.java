package cz.itnetwork.controller;

import cz.itnetwork.dto.PersonDTO;
import cz.itnetwork.dto.PersonFilterDTO;
import cz.itnetwork.dto.PersonStatisticsDTO;
import cz.itnetwork.entity.PersonLookup;
import cz.itnetwork.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page; // Přidán import pro Page
import org.springframework.data.domain.Pageable; // Přidán import pro Pageable
import org.springframework.data.web.PageableDefault; // Přidán import pro PageableDefault
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PersonController {

    @Autowired
    private PersonService personService;

    @PostMapping("/persons")
    public PersonDTO addPerson(@RequestBody PersonDTO personDTO) {
        return  personService.addPerson(personDTO);
    }

    // Původní metoda pro získání všech osob
    // @GetMapping("/persons")
    // public List<PersonDTO> getPersons() {
    //     return personService.getAll();
    // }

    // vracelo cele DTO - pro seznam nepotrbne, zvazit smazani
//    @GetMapping("/persons")
//    public Page<PersonDTO> getPersons(@PageableDefault(size = 20) Pageable pageable) {
//        return personService.getPersons(pageable);
//    }

    @GetMapping("/persons")
    public Page<PersonLookup> getPersons(@PageableDefault(size = 20) Pageable pageable) {
        return personService.getPersonsLookup(pageable); // Volání nové servisní metody
    }

//    @GetMapping("/persons/statistics")
//    public Page<PersonStatisticsDTO> getPersonStatistics(
//            @PageableDefault(size = 10) Pageable pageable) {
//        return personService.getPersonStatistics(pageable);
//    }

    @GetMapping("/persons/statistics")
    public Page<PersonStatisticsDTO> getPersonStatistics(
            @PageableDefault(size = 10, sort = "personName") Pageable pageable) {
        return personService.getPersonStatistics(pageable);
    }

    @GetMapping("/persons/{personId}")
    public PersonDTO getPerson(@PathVariable Long personId) {
        return personService.getPerson(personId);
    }

    @DeleteMapping("/persons/{personId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePerson(@PathVariable Long personId) {
        personService.removePerson(personId);
    }

    @PutMapping("/persons/{personId}")
    public PersonDTO editPerson(@PathVariable Long personId, @RequestBody PersonDTO personDTO) {
        return personService.editPerson(personId, personDTO);
    }

    @GetMapping("/persons/lookup")
    public List<PersonLookup> getAllPersonsLookup() {
        return personService.getAllPersonsLookup();
    }

    @GetMapping("/persons/lookup/{id}")
    public PersonLookup getPersonLookupById(@PathVariable Long id) {
        return personService.getPersonLookupById(id);
    }

    /**
     * Nový endpoint pro získání seznamu všech osob (kupujících a prodejců) pro filtrování faktur.
     * Tento endpoint vrátí seznam DTO objektů s id a názvem.
     */
    @GetMapping("/persons/invoice-related")
    public List<PersonFilterDTO> getInvoiceRelatedPersons() {
        return personService.getInvoiceRelatedPersons();
    }
}
