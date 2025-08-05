package cz.itnetwork.controller;

import cz.itnetwork.dto.InvoiceDTO;
import cz.itnetwork.dto.InvoiceStatisticsDTO;
import cz.itnetwork.dto.InvoiceSummary;
import cz.itnetwork.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceDTO addInvoice(@RequestBody InvoiceDTO invoiceDTO) {
        return invoiceService.addInvoice(invoiceDTO);
    }

    // *** NOVÝ ENDPOINT PRO STRÁNKOVÁNÍ a FILTROVÁNÍ ***
    @GetMapping("/summary")
    public Page<InvoiceSummary> getInvoicesSummary(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) Long buyerId,
            @RequestParam(required = false) Long sellerId,
            @RequestParam(required = false) String product,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer limit) {

        // Pokud je zadán limit, vytvoříme nový Pageable objekt s omezenou velikostí stránky.
        Pageable finalPageable = (limit != null && limit > 0) ? Pageable.ofSize(limit).withPage(pageable.getPageNumber()) : pageable;

        return invoiceService.getFilteredInvoiceSummaries(finalPageable, buyerId, sellerId, product, minPrice, maxPrice);
    }

    @GetMapping("/{invoiceId}")
    public InvoiceDTO getInvoice(@PathVariable Long invoiceId) {
        return invoiceService.getInvoice(invoiceId);
    }

    @PutMapping("/{invoiceId}")
    public InvoiceDTO editInvoice(@PathVariable Long invoiceId, @RequestBody InvoiceDTO invoiceDTO) {
        return invoiceService.editInvoice(invoiceId, invoiceDTO);
    }

    @DeleteMapping("/{invoiceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInvoice(@PathVariable Long invoiceId) {
        invoiceService.removeInvoice(invoiceId);
    }

    /**
     * Získá seznam vystavených faktur podle IČ prodávajícího s podporou stránkování.
     */
    @GetMapping("/identification/{identificationNumber}/sales")
    public Page<InvoiceDTO> getInvoicesBySellerIdentificationNumber(
            @PathVariable String identificationNumber,
            @PageableDefault(size = 20) Pageable pageable) {
        return invoiceService.getInvoicesBySellerIdentificationNumber(identificationNumber, pageable);
    }

    /**
     * Získá seznam přijatých faktur podle IČ kupujícího s podporou stránkování.
     */
    @GetMapping("/identification/{identificationNumber}/purchases")
    public Page<InvoiceDTO> getInvoicesByBuyerIdentificationNumber(
            @PathVariable String identificationNumber,
            @PageableDefault(size = 20) Pageable pageable) {
        return invoiceService.getInvoicesByBuyerIdentificationNumber(identificationNumber, pageable);
    }

    @GetMapping("/statistics")
    public InvoiceStatisticsDTO getInvoiceStatistics() {
        return invoiceService.getInvoiceStatistics();
    }
}