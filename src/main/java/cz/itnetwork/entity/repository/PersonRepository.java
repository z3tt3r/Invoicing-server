package cz.itnetwork.entity.repository;

import cz.itnetwork.dto.PersonStatisticsDTO;
import cz.itnetwork.entity.PersonEntity;
import cz.itnetwork.entity.PersonLookup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<PersonEntity, Long> {

    List<PersonEntity> findByHidden(boolean hidden);

    Page<PersonLookup> findByHidden(boolean hidden, Pageable pageable);

    // Stara metoda pro celou entitu -zvazit smazani
    // Page<PersonEntity> findByHidden(boolean hidden, Pageable pageable);

    List<PersonEntity> findByIdentificationNumber(String identificationNumber);

    List<PersonLookup> findAllByHiddenFalse();

    @Query(value = "SELECT new cz.itnetwork.dto.PersonStatisticsDTO(" +
            "p.id AS personId, " +
            "p.name AS personName, " +
            "COALESCE(SUM(sell_inv.price), 0) + COALESCE(SUM(buy_inv.price), 0) AS revenue) " +
            "FROM person p " +
            "LEFT JOIN p.sales sell_inv " +
            "LEFT JOIN p.purchases buy_inv " +
            "WHERE p.hidden = FALSE " +
            "GROUP BY p.id, p.name " +
            "ORDER BY p.id")
    Page<PersonStatisticsDTO> getPersonRevenueStatistics(Pageable pageable);
}