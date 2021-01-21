package ir.store.product.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import ir.store.product.web.rest.TestUtil;

public class AttributeDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AttributeDTO.class);
        AttributeDTO attributeDTO1 = new AttributeDTO();
        attributeDTO1.setId(1L);
        AttributeDTO attributeDTO2 = new AttributeDTO();
        assertThat(attributeDTO1).isNotEqualTo(attributeDTO2);
        attributeDTO2.setId(attributeDTO1.getId());
        assertThat(attributeDTO1).isEqualTo(attributeDTO2);
        attributeDTO2.setId(2L);
        assertThat(attributeDTO1).isNotEqualTo(attributeDTO2);
        attributeDTO1.setId(null);
        assertThat(attributeDTO1).isNotEqualTo(attributeDTO2);
    }
}
