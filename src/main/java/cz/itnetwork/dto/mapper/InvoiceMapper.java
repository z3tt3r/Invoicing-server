package cz.itnetwork.dto.mapper;

import cz.itnetwork.dto.InvoiceDTO;
import cz.itnetwork.entity.InvoiceEntity;
import cz.itnetwork.dto.InvoiceSummary; // NOVÝ IMPORT
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    // Mapování z DTO na Entitu pro vytváření
    // Ignorujeme buyer a seller, protože se načítají a nastavují v service
    @Mapping(target = "buyer", ignore = true)
    @Mapping(target = "seller", ignore = true)
    InvoiceEntity toEntity(InvoiceDTO source);

    InvoiceDTO toDTO(InvoiceEntity source);

    // Metoda pro aktualizaci existující entity
    // Ignorujeme ID, buyer a seller, protože ID se nemění a buyer/seller se aktualizují ručně
    @Mapping(target = "id", ignore = true) // ID se nemění
    @Mapping(target = "buyer", ignore = true)
    @Mapping(target = "seller", ignore = true)
    void updateEntityFromDto(InvoiceDTO invoiceDTO, @MappingTarget InvoiceEntity entity);

    // *** TATO METODA JE NOVÁ ***
    // Převod z Entity na projekci (pro seznam faktur)
    @Mapping(target = "buyerName", source = "buyer.name")
    @Mapping(target = "sellerName", source = "seller.name")
    @Mapping(target = "buyerIdentificationNumber", source = "buyer.identificationNumber")
    @Mapping(target = "sellerIdentificationNumber", source = "seller.identificationNumber")
    InvoiceSummary toSummary(InvoiceEntity source);
}