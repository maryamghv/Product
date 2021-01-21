package ir.store.product.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class BrandMapperTest {

    private BrandMapper brandMapper;

    @BeforeEach
    public void setUp() {
        brandMapper = new BrandMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(brandMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(brandMapper.fromId(null)).isNull();
    }
}
