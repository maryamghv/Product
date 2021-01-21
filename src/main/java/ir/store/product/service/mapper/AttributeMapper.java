package ir.store.product.service.mapper;


import ir.store.product.domain.*;
import ir.store.product.service.dto.AttributeDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Attribute} and its DTO {@link AttributeDTO}.
 */
@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface AttributeMapper extends EntityMapper<AttributeDTO, Attribute> {

    @Mapping(source = "product.id", target = "productId")
    AttributeDTO toDto(Attribute attribute);

    @Mapping(source = "productId", target = "product")
    Attribute toEntity(AttributeDTO attributeDTO);

    default Attribute fromId(Long id) {
        if (id == null) {
            return null;
        }
        Attribute attribute = new Attribute();
        attribute.setId(id);
        return attribute;
    }
}
