package cz.itnetwork.service;

import cz.itnetwork.dto.InvoiceDTO;
import cz.itnetwork.dto.InvoiceStatisticsDTO;
import cz.itnetwork.entity.InvoiceSummary;

import java.util.List;

public interface InvoiceService {

    InvoiceDTO addInvoice(InvoiceDTO invoiceDTO);

    void removeInvoice(long invoiceId);

    // Původní metoda pro získání všech faktur (List<InvoiceDTO>)
    // Zakomentováno, protože pro seznamy na frontendu se nyní používá projekce getAllInvoiceSummaries().
    // Zvažte smazání, pokud už není nikde jinde potřeba.
    // List<InvoiceDTO> getAll();

    InvoiceDTO getInvoice(long id);

    InvoiceDTO editInvoice(long invoiceId, InvoiceDTO invoiceDTO);

    // Původní metoda pro získání faktur podle kupujícího (List<InvoiceDTO>)
    // Zakomentováno, protože pro seznamy na frontendu se nyní používá projekce getInvoiceSummariesByBuyer().
    // Zvažte smazání, pokud už není nikde jinde potřeba.
    // List<InvoiceDTO> getInvoicesByBuyer(long buyerId);

    // Původní metoda pro získání faktur podle prodávajícího (List<InvoiceDTO>)
    // Zakomentováno, protože pro seznamy na frontendu se nyní používá projekce getInvoiceSummariesBySeller().
    // Zvažte smazání, pokud už není nikde jinde potřeba.
    // List<InvoiceDTO> getInvoicesBySeller(long sellerId);

    InvoiceStatisticsDTO getInvoiceStatistics();

    /**
     * Retrieves a list of all visible invoices, projected into a simplified InvoiceSummary format.
     * This method is optimized for frontend list displays.
     * @return A list of InvoiceSummary objects.
     */
    List<InvoiceSummary> getAllInvoiceSummaries();

    /**
     * Retrieves a list of visible invoices where the specified person is the buyer,
     * projected into a simplified InvoiceSummary format. This method is optimized
     * for frontend list displays.
     * @param buyerId The ID of the buyer.
     * @return A list of InvoiceSummary objects.
     */
    List<InvoiceSummary> getInvoiceSummariesByBuyer(long buyerId);

    /**
     * Retrieves a list of visible invoices where the specified person is the seller,
     * projected into a simplified InvoiceSummary format. This method is optimized
     * for frontend list displays.
     * @param sellerId The ID of the seller.
     * @return A list of InvoiceSummary objects.
     */
    List<InvoiceSummary> getInvoiceSummariesBySeller(long sellerId);
}