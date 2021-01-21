package ir.store.product.service.mapper;


import ir.store.product.domain.*;
import ir.store.product.service.dto.CategoryDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Category} and its DTO {@link CategoryDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface CategoryMapper extends EntityMapper<CategoryDTO, Category> {


    @Mapping(target = "products", ignore = true)
    @Mapping(target = "removeProduct", ignore = true)
    @Mapping(target = "brands", ignore = true)
    @Mapping(target = "removeBrand", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "removeImage", ignore = true)
    Category toEntity(CategoryDTO categoryDTO);

    default Category fromId(Long id) {
        if (id == null) {
            return null;
        }
        Category category = new Category();
        category.setId(id);
        return category;
    }
}
