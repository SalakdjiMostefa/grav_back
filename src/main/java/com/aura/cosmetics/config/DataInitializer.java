package com.aura.cosmetics.config;

import com.aura.cosmetics.entity.Category;
import com.aura.cosmetics.entity.Order;
import com.aura.cosmetics.entity.OrderItem;
import com.aura.cosmetics.entity.Product;
import com.aura.cosmetics.entity.ProductPrice;
import com.aura.cosmetics.enums.PriceType;
import com.aura.cosmetics.enums.PromotionType;
import com.aura.cosmetics.entity.ProductImage;
import com.aura.cosmetics.entity.User;
import com.aura.cosmetics.enums.OrderStatus;
import com.aura.cosmetics.enums.ProductStatus;
import com.aura.cosmetics.enums.Role;
import com.aura.cosmetics.enums.WholesaleStatus;
import com.aura.cosmetics.repository.CategoryRepository;
import com.aura.cosmetics.repository.OrderRepository;
import com.aura.cosmetics.repository.ProductRepository;
import com.aura.cosmetics.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    public DataInitializer(CategoryRepository categoryRepository,
                           ProductRepository productRepository,
                           OrderRepository orderRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        try {
            jdbcTemplate.execute("ALTER TABLE users MODIFY COLUMN role VARCHAR(50) NOT NULL");
        } catch (Exception ignored) {}
        try {
            jdbcTemplate.execute("ALTER TABLE users MODIFY COLUMN wholesale_status VARCHAR(50) NOT NULL DEFAULT 'NONE'");
        } catch (Exception ignored) {}
        if (categoryRepository.count() == 0) {
            initCategoriesAndProducts();
        }
        if (orderRepository.count() == 0) {
            initOrders();
        }
        if (userRepository.count() == 0) {
            initUsers();
        } else {
            ensureDemoAccountsAndProducts();
        }
    }

    private void ensureDemoAccountsAndProducts() {
        // Standard clients who are not wholesale applicants should have status NONE
        userRepository.findAll().forEach(user -> {
            if (user.getRole() == Role.ROLE_CLIENT && (user.getCompanyName() == null || user.getCompanyName().isBlank())) {
                user.setWholesaleStatus(WholesaleStatus.NONE);
                userRepository.save(user);
            }
        });

        // Seed demo approved wholesale partner
        if (userRepository.findByEmail("revendeur@salon-paris.fr").isEmpty()) {
            User proApproved = new User();
            proApproved.setFullName("Leila Benali");
            proApproved.setEmail("revendeur@salon-paris.fr");
            proApproved.setPassword(passwordEncoder.encode("pro2026"));
            proApproved.setPhone("06 77 88 99 00");
            proApproved.setAddress("12 Rue Royale, 75008 Paris");
            proApproved.setRole(Role.ROLE_WHOLESALE);
            proApproved.setWholesaleStatus(WholesaleStatus.APPROVED);
            proApproved.setCompanyName("Institut Beauté Spa Royal");
            proApproved.setTradeRegister("RC-PARIS-2024-B-89412");
            proApproved.setBusinessPhone("01 42 68 55 00");
            proApproved.setBusinessAddress("12 Rue Royale, 75008 Paris");
            proApproved.setWholesaleRequestedAt(LocalDateTime.now().minusDays(5));
            proApproved.setWholesaleApprovedAt(LocalDateTime.now().minusDays(3));
            userRepository.save(proApproved);
        }

        // Seed demo pending wholesale applicant
        if (userRepository.findByEmail("contact@parapharmacie-alger.dz").isEmpty()) {
            User proPending = new User();
            proPending.setFullName("Karim Mansouri");
            proPending.setEmail("contact@parapharmacie-alger.dz");
            proPending.setPassword(passwordEncoder.encode("pro2026"));
            proPending.setPhone("+213 550 12 34 56");
            proPending.setAddress("45 Boulevard Didouche Mourad, Alger");
            proPending.setRole(Role.ROLE_CLIENT);
            proPending.setWholesaleStatus(WholesaleStatus.PENDING);
            proPending.setCompanyName("Parapharmacie El Hana");
            proPending.setTradeRegister("RC-16/00-0987654B20");
            proPending.setBusinessPhone("+213 21 63 45 78");
            proPending.setBusinessAddress("45 Boulevard Didouche Mourad, Alger");
            proPending.setWholesaleRequestedAt(LocalDateTime.now().minusHours(2));
            userRepository.save(proPending);
        }

        // Populate wholesale prices and sample promotions for products if missing
        Double[] defaultWholesalePrices = new Double[] { 24.50, 21.00, 19.00, 14.00, 16.50, 27.00 };
        var products = productRepository.findAll();
        for (int i = 0; i < products.size(); i++) {
            var p = products.get(i);
            boolean updated = false;
            if (p.getWholesalePrice() == null) {
                p.setWholesalePrice(i < defaultWholesalePrices.length ? defaultWholesalePrices[i] : Math.round(p.getPrice() * 0.5 * 100.0) / 100.0);
                updated = true;
            }
            if (p.getWholesaleMinQty() == null || p.getWholesaleMinQty() <= 0) {
                p.setWholesaleMinQty(12);
                updated = true;
            }
            // Demo promotions
            if (i == 0 && p.getPromotionType() == null) {
                p.setPromotionType(PromotionType.PERCENTAGE);
                p.setPromotionValue(20.0); // -20%
                p.setPromotionStartDate(LocalDateTime.now().minusDays(2));
                updated = true;
            } else if (i == 1 && p.getPromotionType() == null) {
                p.setPromotionType(PromotionType.FIXED_AMOUNT);
                p.setPromotionValue(5.0); // -5.00 DZD
                p.setPromotionStartDate(LocalDateTime.now().minusDays(1));
                updated = true;
            }
            if (updated) {
                productRepository.save(p);
            }
        }
    }

    private void initUsers() {
        // Admin
        userRepository.save(new User(
                "Administrateur AURA",
                "admin@aura-cosmetics.fr",
                passwordEncoder.encode("admin2026"),
                "01 23 45 67 89",
                "Maison AURA, 24 Rue du Faubourg Saint-Honoré, 75008 Paris",
                Role.ROLE_ADMIN
        ));

        // Client détail
        userRepository.save(new User(
                "Sophie Delacroix",
                "sophie@example.fr",
                passwordEncoder.encode("client2026"),
                "06 55 44 33 22",
                "3 Allée des Acacias, 33000 Bordeaux",
                Role.ROLE_CLIENT
        ));

        // Grossiste Approuvé
        User proApproved = new User();
        proApproved.setFullName("Leila Benali");
        proApproved.setEmail("revendeur@salon-paris.fr");
        proApproved.setPassword(passwordEncoder.encode("pro2026"));
        proApproved.setPhone("06 77 88 99 00");
        proApproved.setAddress("12 Rue Royale, 75008 Paris");
        proApproved.setRole(Role.ROLE_WHOLESALE);
        proApproved.setWholesaleStatus(WholesaleStatus.APPROVED);
        proApproved.setCompanyName("Institut Beauté & Spa Royal");
        proApproved.setTradeRegister("RC-PARIS-2024-B-89412");
        proApproved.setBusinessPhone("01 42 68 55 00");
        proApproved.setBusinessAddress("12 Rue Royale, 75008 Paris");
        proApproved.setWholesaleRequestedAt(LocalDateTime.now().minusDays(5));
        proApproved.setWholesaleApprovedAt(LocalDateTime.now().minusDays(3));
        userRepository.save(proApproved);

        // Grossiste En Attente
        User proPending = new User();
        proPending.setFullName("Karim Mansouri");
        proPending.setEmail("contact@parapharmacie-alger.dz");
        proPending.setPassword(passwordEncoder.encode("pro2026"));
        proPending.setPhone("+213 550 12 34 56");
        proPending.setAddress("45 Boulevard Didouche Mourad, Alger");
        proPending.setRole(Role.ROLE_CLIENT);
        proPending.setWholesaleStatus(WholesaleStatus.PENDING);
        proPending.setCompanyName("Parapharmacie El Hana");
        proPending.setTradeRegister("RC-16/00-0987654B20");
        proPending.setBusinessPhone("+213 21 63 45 78");
        proPending.setBusinessAddress("45 Boulevard Didouche Mourad, Alger");
        proPending.setWholesaleRequestedAt(LocalDateTime.now().minusHours(2));
        userRepository.save(proPending);
    }

    private void initCategoriesAndProducts() {
        Category catSerums = categoryRepository.save(new Category(
                "Sérums & Huiles",
                "Formules botaniques ultra-actives pour régénérer la peau",
                "https://images.unsplash.com/photo-1598440947619-2c35fc9aa908?auto=format&fit=crop&w=600&q=80"
        ));

        Category catCremes = categoryRepository.save(new Category(
                "Crèmes de Jour & Nuit",
                "Hydratation profonde et protection cellulaire quotidienne",
                "https://images.unsplash.com/photo-1571781926291-c477ebfd024b?auto=format&fit=crop&w=600&q=80"
        ));

        Category catNettoyants = categoryRepository.save(new Category(
                "Nettoyants & Lotions",
                "Pureté et apaisement des épidermes délicats",
                "https://images.unsplash.com/photo-1556228720-195a672e8a03?auto=format&fit=crop&w=600&q=80"
        ));

        Category catMasques = categoryRepository.save(new Category(
                "Masques & Rituels",
                "Soins intenses pour une renaissance cutanée hebdomadaire",
                "https://images.unsplash.com/photo-1556228722-d0b71946ec98?auto=format&fit=crop&w=600&q=80"
        ));

        Product p1 = new Product();
        p1.setName("Élixir Céleste Régénérant");
        p1.setBrand("AURA Botanique");
        p1.setDescription("Sérum ultra-concentré à l'acide hyaluronique pur et extraits de rose sauvage de Provence. Repulpe instantanément l'épiderme et illumine le teint.");
        p1.setPrice(49.00);
        p1.setWholesalePrice(24.50);
        p1.setWholesaleMinQty(12);
        p1.setStock(180);
        p1.setRating(4.9);
        p1.setStatus(ProductStatus.NOUVEAU);
        p1.setCategory(catSerums);
        p1.setPromotionType(PromotionType.PERCENTAGE);
        p1.setPromotionValue(20.0);
        p1.setPromotionStartDate(LocalDateTime.now().minusDays(2));
        p1.addImage(new ProductImage("elixir.jpg", "https://images.unsplash.com/photo-1598440947619-2c35fc9aa908?auto=format&fit=crop&w=800&q=80", 1));
        Product p1Saved = productRepository.save(p1);
        p1Saved.addPrice(new ProductPrice(PriceType.DETAIL, 49.0, 1, "Vente au Détail"));
        p1Saved.addPrice(new ProductPrice(PriceType.GROS, 24.5, 12, "Prix Grossiste - Carton de 12 pcs"));
        productRepository.save(p1Saved);

        Product p2 = new Product();
        p2.setName("Crème Velours Éclat Divin");
        p2.setBrand("Maison AURA");
        p2.setDescription("Crème onctueuse enrichie en beurre de karité bio et peptides de fleurs blanches. Nourrit en profondeur et renforce la barrière cutanée.");
        p2.setPrice(42.50);
        p2.setWholesalePrice(21.00);
        p2.setWholesaleMinQty(12);
        p2.setStock(120);
        p2.setRating(4.8);
        p2.setStatus(ProductStatus.EN_STOCK);
        p2.setCategory(catCremes);
        p2.setPromotionType(PromotionType.FIXED_AMOUNT);
        p2.setPromotionValue(5.0);
        p2.setPromotionStartDate(LocalDateTime.now().minusDays(1));
        p2.addImage(new ProductImage("creme.jpg", "https://images.unsplash.com/photo-1571781926291-c477ebfd024b?auto=format&fit=crop&w=800&q=80", 1));
        Product p2Saved = productRepository.save(p2);
        p2Saved.addPrice(new ProductPrice(PriceType.DETAIL, 42.5, 1, "Vente au Détail"));
        p2Saved.addPrice(new ProductPrice(PriceType.GROS, 21.0, 12, "Prix Grossiste - Carton de 12 pcs"));
        productRepository.save(p2Saved);

        Product p3 = new Product();
        p3.setName("Huile Précieuse d'Argan & Néroli");
        p3.setBrand("AURA Botanique");
        p3.setDescription("Huile sèche réparatrice pour le visage et le cou. Fini satiné non gras au parfum délicat et envoûtant de fleur d'oranger.");
        p3.setPrice(38.00);
        p3.setWholesalePrice(19.00);
        p3.setWholesaleMinQty(12);
        p3.setStock(140);
        p3.setRating(4.9);
        p3.setStatus(ProductStatus.EN_STOCK);
        p3.setCategory(catSerums);
        p3.addImage(new ProductImage("huile.jpg", "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?auto=format&fit=crop&w=800&q=80", 1));
        Product p3Saved = productRepository.save(p3);
        p3Saved.addPrice(new ProductPrice(PriceType.DETAIL, 38.0, 1, "Vente au Détail"));
        p3Saved.addPrice(new ProductPrice(PriceType.GROS, 19.0, 12, "Prix Grossiste - Carton de 12 pcs"));
        productRepository.save(p3Saved);

        Product p4 = new Product();
        p4.setName("Lotion Tonique Florale Immortelle");
        p4.setBrand("Maison AURA");
        p4.setDescription("Hydrolat pur de fleurs immortelles et camomille romaine. Apaise les rougeurs, tonifie les pores et prépare la peau à recevoir les soins.");
        p4.setPrice(26.00);
        p4.setWholesalePrice(13.00);
        p4.setWholesaleMinQty(12);
        p4.setStock(250);
        p4.setRating(4.7);
        p4.setStatus(ProductStatus.EN_STOCK);
        p4.setCategory(catNettoyants);
        p4.addImage(new ProductImage("lotion.jpg", "https://images.unsplash.com/photo-1556228720-195a672e8a03?auto=format&fit=crop&w=800&q=80", 1));
        Product p4Saved = productRepository.save(p4);
        p4Saved.addPrice(new ProductPrice(PriceType.DETAIL, 26.0, 1, "Vente au Détail"));
        p4Saved.addPrice(new ProductPrice(PriceType.GROS, 13.0, 12, "Prix Grossiste - Carton de 12 pcs"));
        productRepository.save(p4Saved);

        Product p5 = new Product();
        p5.setName("Masque Purifiant Argile Blanche & Spiruline");
        p5.setBrand("AURA Botanique");
        p5.setDescription("Masque crémeux détoxifiant aux minéraux marins. Resserre le grain de peau sans dessécher pour un effet peau neuve garanti.");
        p5.setPrice(32.00);
        p5.setWholesalePrice(16.00);
        p5.setWholesaleMinQty(12);
        p5.setStock(100);
        p5.setRating(4.6);
        p5.setStatus(ProductStatus.EN_STOCK);
        p5.setCategory(catMasques);
        p5.addImage(new ProductImage("masque.jpg", "https://images.unsplash.com/photo-1556228722-d0b71946ec98?auto=format&fit=crop&w=800&q=80", 1));
        Product p5Saved = productRepository.save(p5);
        p5Saved.addPrice(new ProductPrice(PriceType.DETAIL, 32.0, 1, "Vente au Détail"));
        p5Saved.addPrice(new ProductPrice(PriceType.GROS, 16.0, 12, "Prix Grossiste - Carton de 12 pcs"));
        productRepository.save(p5Saved);

        Product p6 = new Product();
        p6.setName("Baume Nuit Renaissance Cellulaire");
        p6.setBrand("Maison AURA");
        p6.setDescription("Soin nocturne intensif à la cire d'abeille bio et céramides végétales. Régénère l'épiderme pendant le sommeil pour un réveil lumineux.");
        p6.setPrice(54.00);
        p6.setWholesalePrice(27.00);
        p6.setWholesaleMinQty(12);
        p6.setStock(90);
        p6.setRating(5.0);
        p6.setStatus(ProductStatus.NOUVEAU);
        p6.setCategory(catCremes);
        p6.addImage(new ProductImage("baume.jpg", "https://images.unsplash.com/photo-1608248597359-0091871e411b?auto=format&fit=crop&w=800&q=80", 1));
        Product p6Saved = productRepository.save(p6);
        p6Saved.addPrice(new ProductPrice(PriceType.DETAIL, 54.0, 1, "Vente au Détail"));
        p6Saved.addPrice(new ProductPrice(PriceType.GROS, 27.0, 12, "Prix Grossiste - Carton de 12 pcs"));
        productRepository.save(p6Saved);
    }

    private void initOrders() {
        Order o1 = new Order();
        o1.setCustomerName("Éléonore de Montmirail");
        o1.setEmail("eleonore.montmirail@email.fr");
        o1.setPhone("06 12 34 56 78");
        o1.setAddress("14 Rue de la Paix, 75002 Paris");
        o1.setTotal(91.50);
        o1.setStatus(OrderStatus.PAYEE);
        o1.setCreatedAt(LocalDateTime.now().minusHours(4));
        o1.addItem(new OrderItem(1L, "Élixir Céleste Régénérant", 1, 49.00, false, 15));
        o1.addItem(new OrderItem(2L, "Crème Velours Éclat Divin", 1, 42.50, false, 10));
        orderRepository.save(o1);
    }
}
