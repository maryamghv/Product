package ir.store.product.domain;


import javax.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Category.
 */
@Entity
@Table(name = "category")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "parent")
    private Integer parent;

    @Column(name = "status")
    private Integer status;

    @OneToMany(mappedBy = "category")
    private Set<Product> products = new HashSet<>();

    @OneToMany(mappedBy = "category")
    private Set<Brand> brands = new HashSet<>();

    @OneToMany(mappedBy = "category")
    private Set<Image> images = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Category name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getParent() {
        return parent;
    }

    public Category parent(Integer parent) {
        this.parent = parent;
        return this;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

    public Integer getStatus() {
        return status;
    }

    public Category status(Integer status) {
        this.status = status;
        return this;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public Category products(Set<Product> products) {
        this.products = products;
        return this;
    }

    public Category addProduct(Product product) {
        this.products.add(product);
        product.setCategory(this);
        return this;
    }

    public Category removeProduct(Product product) {
        this.products.remove(product);
        product.setCategory(null);
        return this;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public Set<Brand> getBrands() {
        return brands;
    }

    public Category brands(Set<Brand> brands) {
        this.brands = brands;
        return this;
    }

    public Category addBrand(Brand brand) {
        this.brands.add(brand);
        brand.setCategory(this);
        return this;
    }

    public Category removeBrand(Brand brand) {
        this.brands.remove(brand);
        brand.setCategory(null);
        return this;
    }

    public void setBrands(Set<Brand> brands) {
        this.brands = brands;
    }

    public Set<Image> getImages() {
        return images;
    }

    public Category images(Set<Image> images) {
        this.images = images;
        return this;
    }

    public Category addImage(Image image) {
        this.images.add(image);
        image.setCategory(this);
        return this;
    }

    public Category removeImage(Image image) {
        this.images.remove(image);
        image.setCategory(null);
        return this;
    }

    public void setImages(Set<Image> images) {
        this.images = images;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Category)) {
            return false;
        }
        return id != null && id.equals(((Category) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Category{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", parent=" + getParent() +
            ", status=" + getStatus() +
            "}";
    }
}
