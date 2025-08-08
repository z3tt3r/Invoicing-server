package cz.itnetwork.service;

import cz.itnetwork.dto.PersonDTO;
import cz.itnetwork.dto.PersonFilterDTO;
import cz.itnetwork.dto.PersonStatisticsDTO;
import cz.itnetwork.dto.mapper.PersonMapper;
import cz.itnetwork.entity.InvoiceEntity;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    // NOVÁ METODA pro stránkovaný seznam "lehkých" objektů
    @Override
    public Page<PersonLookup> getPersonsLookup(Pageable pageable) {
        return personRepository.findByHidden(false, pageable);
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

    // NOVÁ METODA: Získání unikátního seznamu osob z faktur
    @Override
    public List<PersonFilterDTO> getInvoiceRelatedPersons() {
        // Získáme všechny faktury, abychom měli kompletní seznam kupujících a prodejců
        List<InvoiceEntity> allInvoices = invoiceRepository.findAll();

        // Používáme Set, abychom automaticky eliminovali duplicity
        Set<PersonFilterDTO> uniquePersons = new HashSet<>();

        for (InvoiceEntity invoice : allInvoices) {
            // Přidáme kupujícího, pokud existuje
            if (invoice.getBuyer() != null && invoice.getBuyer().getIdentificationNumber() != null) {
                uniquePersons.add(new PersonFilterDTO(invoice.getBuyer().getIdentificationNumber(), invoice.getBuyer().getName()));
            }

            // Přidáme prodejce, pokud existuje
            if (invoice.getSeller() != null && invoice.getSeller().getIdentificationNumber() != null) {
                uniquePersons.add(new PersonFilterDTO(invoice.getSeller().getIdentificationNumber(), invoice.getSeller().getName()));
            }
        }

        // Vrátíme List pro konzistentní formát odpovědi
        return new ArrayList<>(uniquePersons);
    }
}
