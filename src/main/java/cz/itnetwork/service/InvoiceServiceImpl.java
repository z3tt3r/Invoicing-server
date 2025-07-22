package cz.itnetwork.service;

import cz.itnetwork.dto.InvoiceDTO;
import cz.itnetwork.dto.InvoiceStatisticsDTO;
import cz.itnetwork.dto.mapper.InvoiceMapper;
import cz.itnetwork.entity.InvoiceEntity;
import cz.itnetwork.entity.InvoiceSummary;
import cz.itnetwork.entity.PersonEntity;
import cz.itnetwork.entity.repository.InvoiceRepository;
import cz.itnetwork.entity.repository.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private InvoiceMapper invoiceMapper;

    private InvoiceRepository invoiceRepository;

    private PersonRepository personRepository;

    public InvoiceServiceImpl(InvoiceMapper invoiceMapper, InvoiceRepository invoiceRepository, PersonRepository personRepository) {
        this.invoiceMapper = invoiceMapper;
        this.invoiceRepository = invoiceRepository;
        this.personRepository = personRepository;
    }

    @Override
    @Transactional
    public InvoiceDTO addInvoice(InvoiceDTO invoiceDTO) {
        InvoiceEntity entity = invoiceMapper.toEntity(invoiceDTO);

        // Volání pomocné metody pro nastavení kupujícího a prodávajícího
        setBuyerAndSellerForInvoice(invoiceDTO, entity);

        entity = invoiceRepository.save(entity);

        return invoiceMapper.toDTO(entity);
    }

    @Override
    public void removeInvoice(long invoiceId) {
        try {
            InvoiceEntity invoice = fetchInvoiceById(invoiceId);
            invoice.setHidden(true);

            invoiceRepository.save(invoice);
        } catch (NotFoundException ignored) {
            // Ignorujeme, pokud faktura nebyla nalezena
        }
    }

    @Override
    public List<InvoiceDTO> getAll() {
        return invoiceRepository.findByHidden(false) // Zobrazujeme pouze ne-skryté faktury
                .stream()
                .map(i -> invoiceMapper.toDTO(i))
                .collect(Collectors.toList());
    }

    @Override
    public InvoiceDTO getInvoice(long id) {
        return invoiceMapper.toDTO(fetchInvoiceById(id));
    }

    //pomocna fetch metoda
    private InvoiceEntity fetchInvoiceById(long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Invoice with id " + id + " wasn't found in the database."));
    }

    @Override
    @Transactional
    public InvoiceDTO editInvoice(long invoiceId, InvoiceDTO invoiceDTO) {
        InvoiceEntity originalInvoice = fetchInvoiceById(invoiceId);
        originalInvoice.setHidden(true);
        invoiceRepository.save(originalInvoice);

        InvoiceEntity newInvoice = invoiceMapper.toEntity(invoiceDTO);
        newInvoice.setId(null);
        setBuyerAndSellerForInvoice(invoiceDTO, newInvoice);
        newInvoice = invoiceRepository.save(newInvoice);
        return invoiceMapper.toDTO(newInvoice);
    }

    @Override
    public List<InvoiceDTO> getInvoicesByBuyer(long buyerId) {
        return invoiceRepository.findByBuyer_Id(buyerId)
                .stream()
                .map(invoiceMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceDTO> getInvoicesBySeller(long sellerId) {
        return invoiceRepository.findBySeller_Id(sellerId)
                .stream()
                .map(invoiceMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InvoiceStatisticsDTO getInvoiceStatistics() {
        long invoicesCount = invoiceRepository.countByHidden(false);

        BigDecimal allTimeSum = invoiceRepository.sumAllTimePricesWithoutVat();
        if (allTimeSum == null) {
            allTimeSum = BigDecimal.ZERO;
        }

        BigDecimal currentYearSum = invoiceRepository.sumCurrentYearPricesWithoutVat();
        if (currentYearSum == null) {
            currentYearSum = BigDecimal.ZERO;
        }

        return new InvoiceStatisticsDTO(currentYearSum, allTimeSum, invoicesCount);
    }

    // pomocna metoda pro prirazeni seller a buyer
    /**
     * Helper method to fetch and set buyer and seller PersonEntities for an InvoiceEntity
     * based on the IDs provided in the InvoiceDTO.
     *
     * @param invoiceDTO The DTO containing buyer and seller IDs.
     * @param invoiceEntity The InvoiceEntity to which buyer and seller should be set.
     * @throws IllegalArgumentException if buyer or seller ID is null.
     * @throws NotFoundException if buyer or seller PersonEntity is not found in the database.
     */
    private void setBuyerAndSellerForInvoice(InvoiceDTO invoiceDTO, InvoiceEntity invoiceEntity) {
        if (invoiceDTO.getBuyer() == null || invoiceDTO.getBuyer().getId() == null) {
            throw new IllegalArgumentException("ID kupujícího musí být uvedeno.");
        }
        PersonEntity buyer = personRepository.findById(invoiceDTO.getBuyer().getId())
                .orElseThrow(() -> new NotFoundException("Kupující s ID " + invoiceDTO.getBuyer().getId() + " nebyl nalezen."));
        invoiceEntity.setBuyer(buyer);

        if (invoiceDTO.getSeller() == null || invoiceDTO.getSeller().getId() == null) {
            throw new IllegalArgumentException("ID prodávajícího musí být uvedeno.");
        }
        PersonEntity seller = personRepository.findById(invoiceDTO.getSeller().getId())
                .orElseThrow(() -> new NotFoundException("Prodávající s ID " + invoiceDTO.getSeller().getId() + " nebyl nalezen."));
        invoiceEntity.setSeller(seller);
    }

    public List<InvoiceSummary> getAllInvoiceSummaries() {
        // Použijte metodu z repozitáře, která vrací projekci
        // Předpokládáme, že chcete jen ty ne-hidden, takže findAllByHiddenFalse()
        return invoiceRepository.findAllByHiddenFalse();
    }
}
