package cz.itnetwork.entity;

import cz.itnetwork.constant.Countries;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity(name = "person")
@Getter
@Setter
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

    // Vazby na faktury, kde je tato osoba prodejce
    @OneToMany(mappedBy = "seller")
    private List<InvoiceEntity> sales;

    // Vazby na faktury, kde je tato osoba kupující
    @OneToMany(mappedBy = "buyer")
    private List<InvoiceEntity> purchases;
}