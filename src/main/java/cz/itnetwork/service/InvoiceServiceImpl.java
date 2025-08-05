package cz.itnetwork.service;

import cz.itnetwork.dto.InvoiceDTO;
import cz.itnetwork.dto.InvoiceStatisticsDTO;
import cz.itnetwork.dto.mapper.InvoiceMapper;
import cz.itnetwork.entity.InvoiceEntity;
import cz.itnetwork.dto.InvoiceSummary;
import cz.itnetwork.entity.PersonEntity;
import cz.itnetwork.entity.repository.InvoiceRepository;
import cz.itnetwork.entity.repository.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // NOVÝ IMPORT
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceMapper invoiceMapper;
    private final InvoiceRepository invoiceRepository;
    private final PersonRepository personRepository;

    public InvoiceServiceImpl(InvoiceMapper invoiceMapper, InvoiceRepository invoiceRepository, PersonRepository personRepository) {
        this.invoiceMapper = invoiceMapper;
        this.invoiceRepository = invoiceRepository;
        this.personRepository = personRepository;
    }

    @Override
    @Transactional
    public InvoiceDTO addInvoice(InvoiceDTO invoiceDTO) {
        InvoiceEntity entity = invoiceMapper.toEntity(invoiceDTO);
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
    public InvoiceDTO getInvoice(long id) {
        return invoiceMapper.toDTO(fetchInvoiceById(id));
    }

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

    @Override
    public List<InvoiceSummary> getAllInvoiceSummaries() {
        Specification<InvoiceEntity> spec = Specification.where(null);
        spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("hidden"), false));

        List<InvoiceEntity> entities = invoiceRepository.findAll(spec);
        return entities.stream().map(invoiceMapper::toSummary).collect(Collectors.toList());
    }

    @Override
    public Page<InvoiceSummary> getFilteredInvoiceSummaries(
            Pageable pageable,
            Long buyerId,
            Long sellerId,
            String product,
            BigDecimal minPrice,
            BigDecimal maxPrice) {

        Specification<InvoiceEntity> spec = Specification.where(null);
        spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("hidden"), false));

        if (buyerId != null) {
            PersonEntity buyer = personRepository.findById(buyerId)
                    .orElseThrow(() -> new NotFoundException("Buyer with ID " + buyerId + " not found."));
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("buyer"), buyer));
        }

        if (sellerId != null) {
            PersonEntity seller = personRepository.findById(sellerId)
                    .orElseThrow(() -> new NotFoundException("Seller with ID " + sellerId + " not found."));
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("seller"), seller));
        }

        if (product != null && !product.trim().isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("product")), "%" + product.toLowerCase() + "%"));
        }

        if (minPrice != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
        }

        if (maxPrice != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
        }

        // Zde voláme findAll s Pageable a vracíme rovnou Page<InvoiceSummary>
        return invoiceRepository.findAll(spec, pageable).map(invoiceMapper::toSummary);
    }

    @Override
    public List<InvoiceSummary> getFilteredInvoiceSummaries(
            Long buyerId,
            Long sellerId,
            String product,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer limit) {
        // Stará implementace, která vrací List a je zde ponechána pro zpětnou kompatibilitu.
        Specification<InvoiceEntity> spec = Specification.where(null);
        spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("hidden"), false));

        if (buyerId != null) {
            PersonEntity buyer = personRepository.findById(buyerId)
                    .orElseThrow(() -> new NotFoundException("Buyer with ID " + buyerId + " not found."));
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("buyer"), buyer));
        }

        if (sellerId != null) {
            PersonEntity seller = personRepository.findById(sellerId)
                    .orElseThrow(() -> new NotFoundException("Seller with ID " + sellerId + " not found."));
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("seller"), seller));
        }

        if (product != null && !product.trim().isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("product")), "%" + product.toLowerCase() + "%"));
        }

        if (minPrice != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
        }

        if (maxPrice != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
        }

        List<InvoiceEntity> entities;
        if (limit != null && limit > 0) {
            Page<InvoiceEntity> pageOfEntities = invoiceRepository.findAll(spec, PageRequest.of(0, limit));
            entities = pageOfEntities.getContent();
        } else {
            entities = invoiceRepository.findAll(spec);
        }

        return entities.stream().map(invoiceMapper::toSummary).collect(Collectors.toList());
    }

    @Override
    public List<InvoiceDTO> getInvoicesBySellerIdentificationNumber(String identificationNumber) {
        // Původní metoda
        List<PersonEntity> sellers = personRepository.findByIdentificationNumber(identificationNumber);
        if (sellers.isEmpty()) {
            return List.of();
        }
        List<Long> sellerIds = sellers.stream().map(PersonEntity::getId).collect(Collectors.toList());
        List<InvoiceEntity> invoices = invoiceRepository.findBySellerIdIn(sellerIds);
        return invoices.stream().map(invoiceMapper::toDTO).collect(Collectors.toList());
    }

    // *** NOVÁ METODA PRO STRÁNKOVÁNÍ u prodejů podle IČ ***
    @Override
    public Page<InvoiceDTO> getInvoicesBySellerIdentificationNumber(String identificationNumber, Pageable pageable) {
        List<PersonEntity> sellers = personRepository.findByIdentificationNumber(identificationNumber);
        if (sellers.isEmpty()) {
            return Page.empty(pageable);
        }
        List<Long> sellerIds = sellers.stream().map(PersonEntity::getId).collect(Collectors.toList());
        return invoiceRepository.findBySellerIdInAndHiddenFalse(sellerIds, pageable).map(invoiceMapper::toDTO);
    }

    @Override
    public List<InvoiceDTO> getInvoicesByBuyerIdentificationNumber(String identificationNumber) {
        // Původní metoda
        List<PersonEntity> buyers = personRepository.findByIdentificationNumber(identificationNumber);
        if (buyers.isEmpty()) {
            return List.of();
        }
        List<Long> buyerIds = buyers.stream().map(PersonEntity::getId).collect(Collectors.toList());
        List<InvoiceEntity> invoices = invoiceRepository.findByBuyerIdIn(buyerIds);
        return invoices.stream().map(invoiceMapper::toDTO).collect(Collectors.toList());
    }

    // *** NOVÁ METODA PRO STRÁNKOVÁNÍ u nákupů podle IČ ***
    @Override
    public Page<InvoiceDTO> getInvoicesByBuyerIdentificationNumber(String identificationNumber, Pageable pageable) {
        List<PersonEntity> buyers = personRepository.findByIdentificationNumber(identificationNumber);
        if (buyers.isEmpty()) {
            return Page.empty(pageable);
        }
        List<Long> buyerIds = buyers.stream().map(PersonEntity::getId).collect(Collectors.toList());
        return invoiceRepository.findByBuyerIdInAndHiddenFalse(buyerIds, pageable).map(invoiceMapper::toDTO);
    }
}