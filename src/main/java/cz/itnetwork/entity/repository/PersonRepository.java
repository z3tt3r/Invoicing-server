package cz.itnetwork.entity.repository;

import cz.itnetwork.entity.PersonEntity;
import cz.itnetwork.entity.PersonLookup;
import org.springframework.data.jpa.repository.JpaRepository;
import cz.itnetwork.dto.PersonStatisticsDTO;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PersonRepository extends JpaRepository<PersonEntity, Long> {

    List<PersonEntity> findByHidden(boolean hidden);

    @Query(name = "get_revenue_statistics", nativeQuery = true)
    List<PersonStatisticsDTO> getPersonRevenueStatistics();

    List<PersonLookup> findAllByHiddenFalse();;
}
