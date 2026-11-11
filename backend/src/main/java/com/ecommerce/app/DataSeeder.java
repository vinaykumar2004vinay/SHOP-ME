package com.ecommerce.app;

import com.ecommerce.model.Cart;
import com.ecommerce.model.Category;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * DataSeeder — runs on startup to seed default data if DB is empty.
 *
 * CommandLineRunner.run() is called by Spring Boot after application context
 * loads. The existsByEmail / existsByName checks ensure idempotency — safe to
 * run multiple times.
 *
 * Seeds: - 1 default admin account (admin@shopease.com / admin123) - 6 product
 * categories with icons - 12 sample products across categories
 */
@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;

    /**
     * @Transactional — makes the entire seed operation atomic. If any save
     * fails mid-way (e.g. a category), the whole seed rolls back instead of
     * leaving partial data. Safe to re-run — existsByEmail / existsByName
     * guards prevent duplicate inserts.
     */
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("[SEED] Checking if initial data needs seeding...");

        seedAdmin();
        seedCategories();
        seedProducts();

        System.out.println("[SEED] Data seeding complete.");
    }

    /**
     * Create default admin account if none exists
     */
    private void seedAdmin() {
        if (!userRepository.existsByEmail("admin@shopease.com")) {
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("ShopEase");
            admin.setEmail("admin@shopease.com");
            admin.setPassword("admin123");
            admin.setShippingAddress("ShopEase HQ, Tech Park, Bangalore");
            admin.setRole(User.Role.ADMIN);
            User saved = userRepository.save(admin);

            // Admin also gets a cart (consistent data model)
            Cart cart = new Cart();
            cart.setUser(saved);
            cartRepository.save(cart);

            System.out.println("[SEED] Admin account created: admin@shopease.com / admin123");
        }
    }

    /**
     * Seed 6 product categories
     */
    private void seedCategories() {
        seedCategory("Electronics", "Phones, laptops, gadgets and more", "💻");
        seedCategory("Fashion", "Trendy clothing, shoes and accessories", "👗");
        seedCategory("Home & Living", "Furniture, decor and kitchen essentials", "🏠");
        seedCategory("Sports & Fitness", "Equipment, activewear and nutrition", "🏋️");
        seedCategory("Books", "Fiction, non-fiction, textbooks and more", "📚");
        seedCategory("Beauty & Health", "Skincare, makeup and wellness products", "✨");
    }

    private void seedCategory(String name, String description, String icon) {
        if (!categoryRepository.existsByNameIgnoreCase(name)) {
            Category cat = new Category();
            cat.setName(name);
            cat.setDescription(description);
            cat.setIcon(icon);
            categoryRepository.save(cat);
            System.out.println("[SEED] Category created: " + name);
        }
    }

    /**
     * Seed sample products — only if products table is empty
     */
    private void seedProducts() {
        if (productRepository.count() > 0) {
            return;
        }

        Category electronics = categoryRepository.findByNameIgnoreCase("Electronics").orElse(null);
        Category fashion = categoryRepository.findByNameIgnoreCase("Fashion").orElse(null);
        Category home = categoryRepository.findByNameIgnoreCase("Home & Living").orElse(null);
        Category sports = categoryRepository.findByNameIgnoreCase("Sports & Fitness").orElse(null);
        Category books = categoryRepository.findByNameIgnoreCase("Books").orElse(null);
        Category beauty = categoryRepository.findByNameIgnoreCase("Beauty & Health").orElse(null);

        // Electronics
        addProduct("iPhone 15 Pro", "Apple iPhone 15 Pro with A17 chip, 48MP camera, titanium design",
                new BigDecimal("134900"), 50, "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=400", electronics);
        addProduct("Samsung Galaxy S24", "Samsung flagship with AI features, 200MP camera",
                new BigDecimal("89999"), 75, "https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=400", electronics);
        addProduct("Sony WH-1000XM5", "Industry-leading noise cancelling wireless headphones",
                new BigDecimal("29990"), 100, "https://images.unsplash.com/photo-1546435770-a3e426bf472b?w=400", electronics);

        // Fashion
        addProduct("Classic White Sneakers", "Minimalist leather sneakers, all-day comfort",
                new BigDecimal("3499"), 200, "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400", fashion);
        addProduct("Premium Denim Jacket", "Classic blue denim jacket, regular fit",
                new BigDecimal("2999"), 150, "https://images.unsplash.com/photo-1542272604-787c3835535d?w=400", fashion);

        // Home
        addProduct("Ergonomic Office Chair", "Lumbar support, adjustable height, breathable mesh",
                new BigDecimal("15999"), 30, "https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=400", home);
        addProduct("Minimalist Desk Lamp", "LED lamp with touch dimmer and USB port",
                new BigDecimal("1799"), 80, "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=400", home);

        // Sports
        addProduct("Yoga Mat Premium", "Non-slip, extra thick 6mm yoga mat with carry strap",
                new BigDecimal("999"), 300, "https://images.unsplash.com/photo-1601925228041-7b1e7c2498df?w=400", sports);
        addProduct("Adjustable Dumbbells Set", "5-25kg adjustable dumbbell pair, space-saving design",
                new BigDecimal("8999"), 40, "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400", sports);

        // Books
        addProduct("Atomic Habits", "James Clear — Build good habits and break bad ones",
                new BigDecimal("499"), 500, "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400", books);

        // Beauty
        addProduct("Vitamin C Serum", "Brightening serum with 20% Vitamin C and hyaluronic acid",
                new BigDecimal("799"), 200, "https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=400", beauty);
        addProduct("Face Wash Charcoal", "Deep cleansing charcoal face wash for all skin types",
                new BigDecimal("299"), 400, "https://images.unsplash.com/photo-1556228578-8c89e6adf883?w=400", beauty);

        System.out.println("[SEED] 12 sample products created.");
    }

    private void addProduct(String name, String desc, BigDecimal price,
            int qty, String img, Category category) {
        if (category == null) {
            return;
        }
        Product p = new Product();
        p.setName(name);
        p.setDescription(desc);
        p.setPrice(price);
        p.setQuantity(qty);
        p.setImageUrl(img);
        p.setCategory(category);
        p.setIsActive(true);
        productRepository.save(p);
    }

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public CartRepository getCartRepository() {
		return cartRepository;
	}

	public void setCartRepository(CartRepository cartRepository) {
		this.cartRepository = cartRepository;
	}

	public CategoryRepository getCategoryRepository() {
		return categoryRepository;
	}

	public void setCategoryRepository(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	public ProductRepository getProductRepository() {
		return productRepository;
	}

	public void setProductRepository(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@Override
	public String toString() {
		return "DataSeeder [userRepository=" + userRepository + ", cartRepository=" + cartRepository
				+ ", categoryRepository=" + categoryRepository + ", productRepository=" + productRepository + "]";
	}

	public DataSeeder(UserRepository userRepository, CartRepository cartRepository,
			CategoryRepository categoryRepository, ProductRepository productRepository) {
		super();
		this.userRepository = userRepository;
		this.cartRepository = cartRepository;
		this.categoryRepository = categoryRepository;
		this.productRepository = productRepository;
	}

	public DataSeeder() {
		super();
	}
    
    
    
    
    
    
    
    
    
    
}
