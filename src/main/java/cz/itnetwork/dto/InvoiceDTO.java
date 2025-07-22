package cz.itnetwork.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDTO {

    @JsonProperty("id")
    private Long id;

    private Integer invoiceNumber;

    private LocalDate issued;

    private LocalDate dueDate;

    private String product;

    private BigDecimal price;

    private Integer vat;

    private String note;

    private PersonDTO buyer;

    private PersonDTO seller;
}
