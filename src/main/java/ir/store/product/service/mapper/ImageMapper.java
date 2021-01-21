package ir.store.product.service.mapper;


import ir.store.product.domain.*;
import ir.store.product.service.dto.ImageDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Image} and its DTO {@link ImageDTO}.
 */
@Mapper(componentModel = "spring", uses = {ProductMapper.class, CategoryMapper.class})
public interface ImageMapper extends EntityMapper<ImageDTO, Image> {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "category.id", target = "categoryId")
    ImageDTO toDto(Image image);

    @Mapping(source = "productId", target = "product")
    @Mapping(source = "categoryId", target = "category")
    Image toEntity(ImageDTO imageDTO);

    default Image fromId(Long id) {
        if (id == null) {
            return null;
        }
        Image image = new Image();
        image.setId(id);
        return image;
    }
}
