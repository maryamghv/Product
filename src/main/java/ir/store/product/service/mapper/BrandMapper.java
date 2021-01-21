package ir.store.product.service.mapper;


import ir.store.product.domain.*;
import ir.store.product.service.dto.BrandDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Brand} and its DTO {@link BrandDTO}.
 */
@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface BrandMapper extends EntityMapper<BrandDTO, Brand> {

    @Mapping(source = "category.id", target = "categoryId")
    BrandDTO toDto(Brand brand);

    @Mapping(source = "categoryId", target = "category")
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "removeProduct", ignore = true)
    Brand toEntity(BrandDTO brandDTO);

    default Brand fromId(Long id) {
        if (id == null) {
            return null;
        }
        Brand brand = new Brand();
        brand.setId(id);
        return brand;
    }
}
