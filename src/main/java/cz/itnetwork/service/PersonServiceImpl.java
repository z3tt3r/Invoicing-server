package cz.itnetwork.service;

import cz.itnetwork.dto.PersonDTO;
import cz.itnetwork.dto.PersonStatisticsDTO;
import cz.itnetwork.dto.mapper.PersonMapper;
import cz.itnetwork.entity.PersonEntity;
import cz.itnetwork.entity.PersonLookup;
import cz.itnetwork.entity.repository.InvoiceRepository;
import cz.itnetwork.entity.repository.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.webjars.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonMapper personMapper;
    private final PersonRepository personRepository;
    private final InvoiceRepository invoiceRepository;

    public PersonServiceImpl(PersonMapper personMapper, PersonRepository personRepository, InvoiceRepository invoiceRepository) {
        this.personMapper = personMapper;
        this.personRepository = personRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    @Transactional
    public PersonDTO addPerson(PersonDTO personDTO) {
        PersonEntity entity = personMapper.toEntity(personDTO);
        entity = personRepository.save(entity);
        return personMapper.toDTO(entity);
    }

    @Override
    public void removePerson(long id) {
        try {
            PersonEntity person = fetchPersonById(id);
            person.setHidden(true);
            personRepository.save(person);
        } catch (NotFoundException ignored) {
            // The contract in the interface states, that no exception is thrown, if the entity is not found.
        }
    }

    @Override
    public List<PersonDTO> getAll() {
        return personRepository.findByHidden(false)
                .stream()
                .map(personMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<PersonDTO> getPersons(Pageable pageable) {
        Page<PersonEntity> personsPage = personRepository.findByHidden(false, pageable);
        return personsPage.map(personMapper::toDTO);
    }

    private PersonEntity fetchPersonById(long id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Person with id " + id + " wasn't found in the database."));
    }

    @Override
    public PersonDTO getPerson(long personId) {
        return personMapper.toDTO(fetchPersonById(personId));
    }

    // UPRAVENÁ METODA: Zachovává původní logiku, ale přidává kontrolu IČO.
    @Override
    @Transactional
    public PersonDTO editPerson(long personId, PersonDTO personDTO) {
        // Načteme původní osobu z databáze
        PersonEntity originalPerson = fetchPersonById(personId);

        // KLÍČOVÁ KONTROLA: Zabráníme změně IČO
        if (!originalPerson.getIdentificationNumber().equals(personDTO.getIdentificationNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Změna IČO u existující osoby není povolena.");
        }

        // Zbytek tvé původní správné logiky, která uchovává historii:
        // 1. Skryjeme původní entitu
        originalPerson.setHidden(true);
        personRepository.save(originalPerson);

        // 2. Vytvoříme a uložíme novou entitu s novými daty, ale se stejným IČO
        PersonEntity newPerson = personMapper.toEntity(personDTO);
        newPerson.setId(null); // Zajistíme, že se vytvoří nový záznam
        newPerson = personRepository.save(newPerson);

        // 3. Vrátíme DTO nové entity
        return personMapper.toDTO(newPerson);
    }

    @Override
    public Page<PersonStatisticsDTO> getPersonStatistics(Pageable pageable) {
        return personRepository.getPersonRevenueStatistics(pageable);
    }

    @Override
    public List<PersonLookup> getAllPersonsLookup() {
        return personRepository.findAllByHiddenFalse();
    }

    @Override
    public PersonLookup getPersonLookupById(Long id) {
        return personRepository.findById(id)
                .map(person -> new PersonLookup() {
                    @Override
                    public Long getId() {
                        return person.getId();
                    }

                    @Override
                    public String getName() {
                        return person.getName();
                    }

                    @Override
                    public String getIdentificationNumber() {
                        return person.getIdentificationNumber();
                    }
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Osoba s ID " + id + " nebyla nalezena."));
    }
}