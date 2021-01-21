package ir.store.product.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class AttributeMapperTest {

    private AttributeMapper attributeMapper;

    @BeforeEach
    public void setUp() {
        attributeMapper = new AttributeMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(attributeMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(attributeMapper.fromId(null)).isNull();
    }
}
