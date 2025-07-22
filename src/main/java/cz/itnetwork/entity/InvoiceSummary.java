package cz.itnetwork.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface InvoiceSummary {
    Long getId();
    int getInvoiceNumber();
    String getProduct();
    BigDecimal getPrice();
    LocalDate getIssued();
    String getBuyerName();
    String getSellerName();
}
