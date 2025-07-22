package cz.itnetwork.controller;

import cz.itnetwork.dto.InvoiceDTO;
import cz.itnetwork.dto.InvoiceStatisticsDTO;
import cz.itnetwork.entity.InvoiceSummary;
import cz.itnetwork.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping("/invoices")
    public InvoiceDTO addInvoice(@RequestBody InvoiceDTO invoiceDTO) {
        return invoiceService.addInvoice(invoiceDTO);
    }

    @GetMapping("/invoices/summary")
    public List<InvoiceSummary> getInvoiceSummaries() {
        return invoiceService.getAllInvoiceSummaries();
    }

    // zvazit smazani, pokud nezyuziju
    @GetMapping("/invoices")
    public List<InvoiceDTO> getInvoices() {return invoiceService.getAll();}

    @GetMapping("/invoices/{invoiceId}")
    public InvoiceDTO getInvoice(@PathVariable Long invoiceId) {
        return invoiceService.getInvoice(invoiceId);
    }

    @PutMapping("/invoices/{invoiceId}")
    public InvoiceDTO editInvoice(@PathVariable Long invoiceId, @RequestBody InvoiceDTO invoiceDTO) {
        return invoiceService.editInvoice(invoiceId, invoiceDTO);
    }

    @GetMapping("/invoices/by-buyer/{buyerId}")
    public List<InvoiceDTO> getInvoicesByBuyer(@PathVariable Long buyerId) {
        return invoiceService.getInvoicesByBuyer(buyerId);
    }

    @GetMapping("/invoices/by-seller/{sellerId}")
    public List<InvoiceDTO> getInvoicesBySeller(@PathVariable Long sellerId) {
        return invoiceService.getInvoicesBySeller(sellerId);
    }

    @DeleteMapping("/invoices/{invoiceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInvoice(@PathVariable Long invoiceId) {
        invoiceService.removeInvoice(invoiceId);
    }

    @GetMapping("/invoices/statistics")
    public InvoiceStatisticsDTO getInvoiceStatistics() {
        return invoiceService.getInvoiceStatistics();
    }
}
