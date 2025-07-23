package cz.itnetwork.service;

import cz.itnetwork.dto.PersonDTO;
import cz.itnetwork.dto.PersonStatisticsDTO;
import cz.itnetwork.dto.mapper.PersonMapper;
import cz.itnetwork.entity.PersonEntity;
import cz.itnetwork.entity.repository.InvoiceRepository;
import cz.itnetwork.entity.repository.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonServiceImpl implements PersonService {

    private PersonMapper personMapper;

    private PersonRepository personRepository;

    private InvoiceRepository invoiceRepository;

    public PersonServiceImpl(PersonMapper personMapper, PersonRepository personRepository) {
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
            person.setHidden(true); // Nastavení příznaku skrytí

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

    // region: Private methods
    /**
     * <p>Attempts to fetch a person.</p>
     * <p>In case a person with the passed [id] doesn't exist a [{@link org.webjars.NotFoundException}] is thrown.</p>
     *
     * @param id Person to fetch
     * @return Fetched entity
     * @throws org.webjars.NotFoundException In case a person with the passed [id] isn't found
     */
    private PersonEntity fetchPersonById(long id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Person with id " + id + " wasn't found in the database."));
    }
    // endregion

    @Override
    public PersonDTO getPerson(long personId) {
        return personMapper.toDTO(fetchPersonById(personId));
    }

    @Override
    @Transactional
    public PersonDTO editPerson(long personId, PersonDTO personDTO) {
        PersonEntity originalPerson = fetchPersonById(personId);
        originalPerson.setHidden(true);
        personRepository.save(originalPerson);

        PersonEntity newPerson = personMapper.toEntity(personDTO);
        newPerson.setId(null);
        newPerson = personRepository.save(newPerson);
        return personMapper.toDTO(newPerson);
    }

    @Override
    public List<PersonStatisticsDTO> getPersonStatistics() {
        // Voláme nativní query z PersonRepository
        return personRepository.getPersonRevenueStatistics();
    }
}
