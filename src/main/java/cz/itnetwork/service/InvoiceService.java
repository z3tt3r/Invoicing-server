package cz.itnetwork.service;

import cz.itnetwork.dto.InvoiceDTO;
import cz.itnetwork.dto.InvoiceStatisticsDTO;
import cz.itnetwork.dto.InvoiceSummary;
import org.springframework.data.domain.Page; // NOVÝ IMPORT
import org.springframework.data.domain.Pageable; // NOVÝ IMPORT

import java.math.BigDecimal;
import java.util.List;

public interface InvoiceService {

    InvoiceDTO addInvoice(InvoiceDTO invoiceDTO);
    void removeInvoice(long invoiceId);
    InvoiceDTO getInvoice(long id);
    InvoiceDTO editInvoice(long invoiceId, InvoiceDTO invoiceDTO);
    InvoiceStatisticsDTO getInvoiceStatistics();

    // Jediná flexibilní metoda pro získání faktur s filtry
    List<InvoiceSummary> getFilteredInvoiceSummaries(
            Long buyerId,
            Long sellerId,
            String product,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer limit
    );

    // *** NOVÁ METODA S PODPOROU STRÁNKOVÁNÍ ***
    Page<InvoiceSummary> getFilteredInvoiceSummaries(
            Pageable pageable,
            Long buyerId,
            Long sellerId,
            String product,
            BigDecimal minPrice,
            BigDecimal maxPrice
    );

    List<InvoiceSummary> getAllInvoiceSummaries();

    List<InvoiceDTO> getInvoicesBySellerIdentificationNumber(String identificationNumber);

    // *** NOVÁ METODA S PODPOROU STRÁNKOVÁNÍ ***
    Page<InvoiceDTO> getInvoicesBySellerIdentificationNumber(String identificationNumber, Pageable pageable);

    List<InvoiceDTO> getInvoicesByBuyerIdentificationNumber(String identificationNumber);

    // *** NOVÁ METODA S PODPOROU STRÁNKOVÁNÍ ***
    Page<InvoiceDTO> getInvoicesByBuyerIdentificationNumber(String identificationNumber, Pageable pageable);
}