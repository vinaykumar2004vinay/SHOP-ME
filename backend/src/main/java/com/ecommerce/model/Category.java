package com.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Category entity — groups products for easy browsing.
 *
 * WHY @JsonIgnore on products: Category has a bidirectional relationship with
 * Product. Without @JsonIgnore, Jackson serializes Category → products list →
 * each Product → its category → products list again → INFINITE LOOP. This
 * causes StackOverflowError and a 500 on GET /api/categories.
 *
 * WHY @Getter/@Setter instead of @Data:
 *
 * @Data generates equals/hashCode using ALL fields including lazy collections,
 * which causes LazyInitializationException when Hibernate tries to compare
 * detached entities. @Getter/@Setter is safe.
 */
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Category name must be unique — no duplicate Electronics
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Icon/emoji for visual display in frontend category chips
    @Column(length = 50)
    private String icon;

    /**
     * @JsonIgnore — breaks Category → Product → Category infinite loop. The
     * products list is never needed when serializing a category to JSON.
     * Products carry their category info (name/id), not the other way.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Product> products;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	@Override
	public String toString() {
		return "Category [id=" + id + ", name=" + name + ", description=" + description + ", icon=" + icon
				+ ", products=" + products + "]";
	}

	public Category(String name, String description, String icon, List<Product> products) {
		super();
		this.name = name;
		this.description = description;
		this.icon = icon;
		this.products = products;
	}

	public Category(Long id, String name, String description, String icon, List<Product> products) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.icon = icon;
		this.products = products;
	}

	public Category() {
		super();
	}

	
	
    
    
}
