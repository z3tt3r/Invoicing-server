package cz.itnetwork.entity.repository;

import cz.itnetwork.dto.InvoiceSummary;
import cz.itnetwork.entity.InvoiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<InvoiceEntity, Long>, JpaSpecificationExecutor<InvoiceEntity> {

    @Query(value = "SELECT SUM(i.price) FROM invoice i WHERE i.hidden = false", nativeQuery = true)
    BigDecimal sumAllTimePricesWithoutVat();

    @Query(value = "SELECT SUM(i.price) FROM invoice i WHERE YEAR(i.issued) = YEAR(CURDATE()) AND i.hidden = false", nativeQuery = true)
    BigDecimal sumCurrentYearPricesWithoutVat();

    long countByHidden(boolean hidden);

    List<InvoiceEntity> findBySellerIdIn(List<Long> sellerIds);
    List<InvoiceEntity> findByBuyerIdIn(List<Long> buyerIds);

    Page<InvoiceEntity> findBySellerIdInAndHiddenFalse(List<Long> sellerIds, Pageable pageable);
    Page<InvoiceEntity> findByBuyerIdInAndHiddenFalse(List<Long> buyerIds, Pageable pageable);

    // *** UPRAVENO: FROM invoice i ***
    @Query(value = "SELECT new cz.itnetwork.dto.InvoiceSummary(i.id, i.invoiceNumber, i.product, i.price, i.issued, " +
            "i.buyer.name, i.seller.name, i.buyer.identificationNumber, i.seller.identificationNumber) " +
            "FROM invoice i " +
            "WHERE (:buyerId IS NULL OR i.buyer.id = :buyerId) " +
            "AND (:sellerId IS NULL OR i.seller.id = :sellerId) " +
            "AND (:product IS NULL OR i.product LIKE CONCAT('%', :product, '%')) " +
            "AND (:minPrice IS NULL OR i.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR i.price <= :maxPrice) " +
            "AND (i.hidden = false)")
    Page<InvoiceSummary> getFilteredInvoiceSummaries(
            @Param("buyerId") Long buyerId,
            @Param("sellerId") Long sellerId,
            @Param("product") String product,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

    // *** UPRAVENO: FROM invoice i ***
    @Query(value = "SELECT new cz.itnetwork.dto.InvoiceSummary(i.id, i.invoiceNumber, i.product, i.price, i.issued, " +
            "i.buyer.name, i.seller.name, i.buyer.identificationNumber, i.seller.identificationNumber) " +
            "FROM invoice i " +
            "WHERE i.seller.identificationNumber = :ic " +
            "AND i.hidden = false")
    Page<InvoiceSummary> getSalesInvoicesByPersonIdentificationNumber(
            @Param("ic") String ic,
            Pageable pageable
    );

    // *** UPRAVENO: FROM invoice i ***
    @Query(value = "SELECT new cz.itnetwork.dto.InvoiceSummary(i.id, i.invoiceNumber, i.product, i.price, i.issued, " +
            "i.buyer.name, i.seller.name, i.buyer.identificationNumber, i.seller.identificationNumber) " +
            "FROM invoice i " +
            "WHERE i.buyer.identificationNumber = :ic " +
            "AND i.hidden = false")
    Page<InvoiceSummary> getPurchaseInvoicesByPersonIdentificationNumber(
            @Param("ic") String ic,
            Pageable pageable
    );
}