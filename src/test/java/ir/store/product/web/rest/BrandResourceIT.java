package ir.store.product.web.rest;

import ir.store.product.StoreMicroserviceApp;
import ir.store.product.domain.Brand;
import ir.store.product.domain.Category;
import ir.store.product.domain.Product;
import ir.store.product.repository.BrandRepository;
import ir.store.product.service.BrandService;
import ir.store.product.service.dto.BrandDTO;
import ir.store.product.service.mapper.BrandMapper;
import ir.store.product.service.dto.BrandCriteria;
import ir.store.product.service.BrandQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link BrandResource} REST controller.
 */
@SpringBootTest(classes = StoreMicroserviceApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class BrandResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_STATUS = 1;
    private static final Integer UPDATED_STATUS = 2;
    private static final Integer SMALLER_STATUS = 1 - 1;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private BrandService brandService;

    @Autowired
    private BrandQueryService brandQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBrandMockMvc;

    private Brand brand;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Brand createEntity(EntityManager em) {
        Brand brand = new Brand()
            .name(DEFAULT_NAME)
            .status(DEFAULT_STATUS);
        return brand;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Brand createUpdatedEntity(EntityManager em) {
        Brand brand = new Brand()
            .name(UPDATED_NAME)
            .status(UPDATED_STATUS);
        return brand;
    }

    @BeforeEach
    public void initTest() {
        brand = createEntity(em);
    }

    @Test
    @Transactional
    public void createBrand() throws Exception {
        int databaseSizeBeforeCreate = brandRepository.findAll().size();
        // Create the Brand
        BrandDTO brandDTO = brandMapper.toDto(brand);
        restBrandMockMvc.perform(post("/api/brands")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(brandDTO)))
            .andExpect(status().isCreated());

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeCreate + 1);
        Brand testBrand = brandList.get(brandList.size() - 1);
        assertThat(testBrand.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testBrand.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    public void createBrandWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = brandRepository.findAll().size();

        // Create the Brand with an existing ID
        brand.setId(1L);
        BrandDTO brandDTO = brandMapper.toDto(brand);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBrandMockMvc.perform(post("/api/brands")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(brandDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllBrands() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList
        restBrandMockMvc.perform(get("/api/brands?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(brand.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }
    
    @Test
    @Transactional
    public void getBrand() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get the brand
        restBrandMockMvc.perform(get("/api/brands/{id}", brand.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(brand.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS));
    }


    @Test
    @Transactional
    public void getBrandsByIdFiltering() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        Long id = brand.getId();

        defaultBrandShouldBeFound("id.equals=" + id);
        defaultBrandShouldNotBeFound("id.notEquals=" + id);

        defaultBrandShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultBrandShouldNotBeFound("id.greaterThan=" + id);

        defaultBrandShouldBeFound("id.lessThanOrEqual=" + id);
        defaultBrandShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllBrandsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where name equals to DEFAULT_NAME
        defaultBrandShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the brandList where name equals to UPDATED_NAME
        defaultBrandShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllBrandsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where name not equals to DEFAULT_NAME
        defaultBrandShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the brandList where name not equals to UPDATED_NAME
        defaultBrandShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllBrandsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where name in DEFAULT_NAME or UPDATED_NAME
        defaultBrandShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the brandList where name equals to UPDATED_NAME
        defaultBrandShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllBrandsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where name is not null
        defaultBrandShouldBeFound("name.specified=true");

        // Get all the brandList where name is null
        defaultBrandShouldNotBeFound("name.specified=false");
    }
                @Test
    @Transactional
    public void getAllBrandsByNameContainsSomething() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where name contains DEFAULT_NAME
        defaultBrandShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the brandList where name contains UPDATED_NAME
        defaultBrandShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllBrandsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where name does not contain DEFAULT_NAME
        defaultBrandShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the brandList where name does not contain UPDATED_NAME
        defaultBrandShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }


    @Test
    @Transactional
    public void getAllBrandsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where status equals to DEFAULT_STATUS
        defaultBrandShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the brandList where status equals to UPDATED_STATUS
        defaultBrandShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllBrandsByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where status not equals to DEFAULT_STATUS
        defaultBrandShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the brandList where status not equals to UPDATED_STATUS
        defaultBrandShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllBrandsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultBrandShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the brandList where status equals to UPDATED_STATUS
        defaultBrandShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllBrandsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where status is not null
        defaultBrandShouldBeFound("status.specified=true");

        // Get all the brandList where status is null
        defaultBrandShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    public void getAllBrandsByStatusIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where status is greater than or equal to DEFAULT_STATUS
        defaultBrandShouldBeFound("status.greaterThanOrEqual=" + DEFAULT_STATUS);

        // Get all the brandList where status is greater than or equal to UPDATED_STATUS
        defaultBrandShouldNotBeFound("status.greaterThanOrEqual=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllBrandsByStatusIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where status is less than or equal to DEFAULT_STATUS
        defaultBrandShouldBeFound("status.lessThanOrEqual=" + DEFAULT_STATUS);

        // Get all the brandList where status is less than or equal to SMALLER_STATUS
        defaultBrandShouldNotBeFound("status.lessThanOrEqual=" + SMALLER_STATUS);
    }

    @Test
    @Transactional
    public void getAllBrandsByStatusIsLessThanSomething() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where status is less than DEFAULT_STATUS
        defaultBrandShouldNotBeFound("status.lessThan=" + DEFAULT_STATUS);

        // Get all the brandList where status is less than UPDATED_STATUS
        defaultBrandShouldBeFound("status.lessThan=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllBrandsByStatusIsGreaterThanSomething() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList where status is greater than DEFAULT_STATUS
        defaultBrandShouldNotBeFound("status.greaterThan=" + DEFAULT_STATUS);

        // Get all the brandList where status is greater than SMALLER_STATUS
        defaultBrandShouldBeFound("status.greaterThan=" + SMALLER_STATUS);
    }


    @Test
    @Transactional
    public void getAllBrandsByCategoryIsEqualToSomething() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);
        Category category = CategoryResourceIT.createEntity(em);
        em.persist(category);
        em.flush();
        brand.setCategory(category);
        brandRepository.saveAndFlush(brand);
        Long categoryId = category.getId();

        // Get all the brandList where category equals to categoryId
        defaultBrandShouldBeFound("categoryId.equals=" + categoryId);

        // Get all the brandList where category equals to categoryId + 1
        defaultBrandShouldNotBeFound("categoryId.equals=" + (categoryId + 1));
    }


    @Test
    @Transactional
    public void getAllBrandsByProductIsEqualToSomething() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);
        Product product = ProductResourceIT.createEntity(em);
        em.persist(product);
        em.flush();
        brand.addProduct(product);
        brandRepository.saveAndFlush(brand);
        Long productId = product.getId();

        // Get all the brandList where product equals to productId
        defaultBrandShouldBeFound("productId.equals=" + productId);

        // Get all the brandList where product equals to productId + 1
        defaultBrandShouldNotBeFound("productId.equals=" + (productId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBrandShouldBeFound(String filter) throws Exception {
        restBrandMockMvc.perform(get("/api/brands?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(brand.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));

        // Check, that the count call also returns 1
        restBrandMockMvc.perform(get("/api/brands/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBrandShouldNotBeFound(String filter) throws Exception {
        restBrandMockMvc.perform(get("/api/brands?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBrandMockMvc.perform(get("/api/brands/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingBrand() throws Exception {
        // Get the brand
        restBrandMockMvc.perform(get("/api/brands/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBrand() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        int databaseSizeBeforeUpdate = brandRepository.findAll().size();

        // Update the brand
        Brand updatedBrand = brandRepository.findById(brand.getId()).get();
        // Disconnect from session so that the updates on updatedBrand are not directly saved in db
        em.detach(updatedBrand);
        updatedBrand
            .name(UPDATED_NAME)
            .status(UPDATED_STATUS);
        BrandDTO brandDTO = brandMapper.toDto(updatedBrand);

        restBrandMockMvc.perform(put("/api/brands")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(brandDTO)))
            .andExpect(status().isOk());

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeUpdate);
        Brand testBrand = brandList.get(brandList.size() - 1);
        assertThat(testBrand.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBrand.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void updateNonExistingBrand() throws Exception {
        int databaseSizeBeforeUpdate = brandRepository.findAll().size();

        // Create the Brand
        BrandDTO brandDTO = brandMapper.toDto(brand);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBrandMockMvc.perform(put("/api/brands")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(brandDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteBrand() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        int databaseSizeBeforeDelete = brandRepository.findAll().size();

        // Delete the brand
        restBrandMockMvc.perform(delete("/api/brands/{id}", brand.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
