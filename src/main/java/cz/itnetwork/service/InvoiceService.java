package cz.itnetwork.service;

import cz.itnetwork.dto.InvoiceDTO;
import cz.itnetwork.dto.InvoiceStatisticsDTO;

import java.util.List;

public interface InvoiceService {

    InvoiceDTO addInvoice(InvoiceDTO invoiceDTO);

    void removeInvoice(long id);

    List<InvoiceDTO> getAll();

    InvoiceDTO getInvoice(long id);

    InvoiceDTO editInvoice(long invoiceId, InvoiceDTO invoiceDTO);

    List<InvoiceDTO> getInvoicesByBuyer(long buyerId);
    List<InvoiceDTO> getInvoicesBySeller(long sellerId);

    InvoiceStatisticsDTO getInvoiceStatistics();
}
