package cz.itnetwork.service;

import cz.itnetwork.dto.InvoiceDTO;
import cz.itnetwork.dto.InvoiceStatisticsDTO;
import cz.itnetwork.dto.InvoiceSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface InvoiceService {

    InvoiceDTO addInvoice(InvoiceDTO invoiceDTO);
    void removeInvoice(long invoiceId);
    InvoiceDTO getInvoice(long id);
    InvoiceDTO editInvoice(long invoiceId, InvoiceDTO invoiceDTO);
    InvoiceStatisticsDTO getInvoiceStatistics();

    // Nová paged metoda pro získání faktur s filtry
    Page<InvoiceSummary> getFilteredInvoiceSummaries(
            Pageable pageable,
            String buyerId,
            String sellerId,
            String product,
            BigDecimal minPrice,
            BigDecimal maxPrice
    );

    List<InvoiceSummary> getAllInvoiceSummaries();

    List<InvoiceDTO> getInvoicesBySellerIdentificationNumber(String identificationNumber);

    Page<InvoiceDTO> getInvoicesBySellerIdentificationNumber(String identificationNumber, Pageable pageable);

    List<InvoiceDTO> getInvoicesByBuyerIdentificationNumber(String identificationNumber);

    Page<InvoiceDTO> getInvoicesByBuyerIdentificationNumber(String identificationNumber, Pageable pageable);
}
