package cz.itnetwork.entity;

import cz.itnetwork.constant.Countries;
import cz.itnetwork.dto.PersonStatisticsDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity(name = "person")
@Getter
@Setter
@NamedNativeQuery(
        name = "get_revenue_statistics", // Jméno dotazu, na které se bude odkazovat z repozitáře
        query = """
                SELECT
                    p.id AS personId,
                    p.name AS personName,
                    CAST((COALESCE(SUM(CASE WHEN i.buyer_id = p.id THEN i.price ELSE 0 END), 0) +
                    COALESCE(SUM(CASE WHEN i.seller_id = p.id THEN i.price ELSE 0 END), 0)) AS DECIMAL(19, 2)) AS revenue
                FROM person p
                LEFT JOIN invoice i ON (i.buyer_id = p.id OR i.seller_id = p.id) AND i.hidden = FALSE
                WHERE p.hidden = FALSE
                GROUP BY p.id, p.name
                ORDER BY p.name
                """,
        resultSetMapping = "PersonStatisticsMapping" // Odkaz na mapování výsledků
)
@SqlResultSetMapping(
        name = "PersonStatisticsMapping",
        classes = @ConstructorResult(
                targetClass = PersonStatisticsDTO.class,
                columns = {
                        @ColumnResult(name = "personId", type = Long.class),
                        @ColumnResult(name = "personName", type = String.class),
                        @ColumnResult(name = "revenue", type = BigDecimal.class)
                }
        )
)
public class PersonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String identificationNumber;

    private String taxNumber;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String bankCode;

    private String iban;

    @Column(nullable = false)
    private String telephone;

    @Column(nullable = false)
    private String mail;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String zip;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Countries country;

    private String note;

    private boolean hidden = false;

}
