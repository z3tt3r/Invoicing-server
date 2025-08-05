package cz.itnetwork.service;

import cz.itnetwork.dto.PersonDTO;
import cz.itnetwork.dto.PersonStatisticsDTO;
import cz.itnetwork.entity.PersonLookup;
import org.springframework.data.domain.Page; // Přidán import pro Page
import org.springframework.data.domain.Pageable; // Přidán import pro Pageable

import java.util.List;

public interface PersonService {

    /**
     * Creates a new person
     *
     * @param personDTO Person to create
     * @return newly created person
     */
    PersonDTO addPerson(PersonDTO personDTO);

    /**
     * <p>Sets hidden flag to true for the person with the matching [id]</p>
     * <p>In case a person with the passed [id] isn't found, the method <b>silently fails</b></p>
     *
     * @param id Person to delete
     */
    void removePerson(long id);

    /**
     * Fetches all non-hidden persons
     *
     * @return List of all non-hidden persons
     */
    List<PersonDTO> getAll();

    /**
     * Fetches a paginated list of all non-hidden persons.
     *
     * @param pageable Pagination and sorting information
     * @return Page of non-hidden persons
     */
    Page<PersonDTO> getPersons(Pageable pageable); // NOVÁ METODA PRO STRÁNKOVÁNÍ

    PersonDTO getPerson(long personId);

    PersonDTO editPerson(long personId, PersonDTO personDTO);

//    List<PersonStatisticsDTO> getPersonStatistics();

    Page<PersonStatisticsDTO> getPersonStatistics(Pageable pageable);

    List<PersonLookup> getAllPersonsLookup();

    PersonLookup getPersonLookupById(Long id);
}