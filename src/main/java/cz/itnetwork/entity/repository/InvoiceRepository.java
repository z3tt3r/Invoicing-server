package cz.itnetwork.entity.repository;

import cz.itnetwork.entity.InvoiceEntity;
import cz.itnetwork.entity.InvoiceSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface InvoiceRepository  extends JpaRepository<InvoiceEntity, Long> {
    List<InvoiceEntity> findByHidden(boolean hidden);

    List<InvoiceEntity> findByBuyer_Id(long buyerId);

    List<InvoiceEntity> findBySeller_Id(long sellerId);

    @Query("SELECT SUM(i.price) FROM invoice i WHERE i.hidden = false")
    BigDecimal sumAllTimePricesWithoutVat();

    @Query("SELECT SUM(i.price) FROM invoice i WHERE YEAR(i.issued) = YEAR(CURDATE()) AND i.hidden = false")
    BigDecimal sumCurrentYearPricesWithoutVat();

    long countByHidden(boolean hidden);

    List<InvoiceSummary> findAllByHiddenFalse();
}
