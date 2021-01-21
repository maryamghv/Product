package ir.store.product.service.mapper;


import ir.store.product.domain.*;
import ir.store.product.service.dto.ProductDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Product} and its DTO {@link ProductDTO}.
 */
@Mapper(componentModel = "spring", uses = {CategoryMapper.class, BrandMapper.class})
public interface ProductMapper extends EntityMapper<ProductDTO, Product> {

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "brand.id", target = "brandId")
    ProductDTO toDto(Product product);

    @Mapping(target = "attributes", ignore = true)
    @Mapping(target = "removeAttribute", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "removeImage", ignore = true)
    @Mapping(source = "categoryId", target = "category")
    @Mapping(source = "brandId", target = "brand")
    Product toEntity(ProductDTO productDTO);

    default Product fromId(Long id) {
        if (id == null) {
            return null;
        }
        Product product = new Product();
        product.setId(id);
        return product;
    }
}
