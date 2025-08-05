package cz.itnetwork.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvoiceSummary(
        Long id,
        String invoiceNumber,
        String product,
        BigDecimal price,
        LocalDate issued,
        String buyerName,
        String sellerName,
        String buyerIdentificationNumber,
        String sellerIdentificationNumber
) {
}