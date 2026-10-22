package com.aura.cosmetics.config;

import com.aura.cosmetics.entity.*;
import com.aura.cosmetics.enums.OrderStatus;
import com.aura.cosmetics.enums.ProductStatus;
import com.aura.cosmetics.enums.Role;
import com.aura.cosmetics.repository.CategoryRepository;
import com.aura.cosmetics.repository.OrderRepository;
import com.aura.cosmetics.repository.ProductRepository;
import com.aura.cosmetics.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(CategoryRepository categoryRepository,
                           ProductRepository productRepository,
                           OrderRepository orderRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (categoryRepository.count() == 0) {
            initCategoriesAndProducts();
        }
        if (orderRepository.count() == 0) {
            initOrders();
        }
        if (userRepository.count() == 0) {
            initUsers();
        }
    }

    private void initUsers() {
        // Compte ADMIN par défaut
        userRepository.save(new User(
                "Administrateur AURA",
                "admin@aura-cosmetics.fr",
                passwordEncoder.encode("admin2026"),
                "01 23 45 67 89",
                "Maison AURA, 24 Rue du Faubourg Saint-Honoré, 75008 Paris",
                Role.ROLE_ADMIN
        ));

        // Compte CLIENT de démo
        userRepository.save(new User(
                "Sophie Delacroix",
                "sophie@example.fr",
                passwordEncoder.encode("client2026"),
                "06 55 44 33 22",
                "3 Allée des Acacias, 33000 Bordeaux",
                Role.ROLE_CLIENT
        ));
    }

    private void initCategoriesAndProducts() {
        // 1. Catégories
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

        // 2. Produits
        Product p1 = new Product();
        p1.setName("Élixir Céleste Régénérant");
        p1.setBrand("AURA Botanique");
        p1.setDescription("Sérum ultra-concentré à l’acide hyaluronique pur et extraits de rose sauvage de Provence. Repulpe instantanément l’épiderme et illumine le teint.");
        p1.setPrice(49.00);
        p1.setStock(18);
        p1.setRating(4.9);
        p1.setStatus(ProductStatus.NOUVEAU);
        p1.setCategory(catSerums);
        p1.addImage(new ProductImage("elixir.jpg", "https://images.unsplash.com/photo-1598440947619-2c35fc9aa908?auto=format&fit=crop&w=800&q=80", 1));
        productRepository.save(p1);

        Product p2 = new Product();
        p2.setName("Crème Velours Éclat Divin");
        p2.setBrand("Maison AURA");
        p2.setDescription("Crème onctueuse enrichie en beurre de karité bio et peptides de fleurs blanches. Nourrit en profondeur et renforce la barrière cutanée.");
        p2.setPrice(42.50);
        p2.setStock(12);
        p2.setRating(4.8);
        p2.setStatus(ProductStatus.EN_STOCK);
        p2.setCategory(catCremes);
        p2.addImage(new ProductImage("creme.jpg", "https://images.unsplash.com/photo-1571781926291-c477ebfd024b?auto=format&fit=crop&w=800&q=80", 1));
        productRepository.save(p2);

        Product p3 = new Product();
        p3.setName("Huile Précieuse d’Argan & Néroli");
        p3.setBrand("AURA Botanique");
        p3.setDescription("Huile sèche réparatrice pour le visage et le cou. Fini satiné non gras au parfum délicat et envoûtant de fleur d’oranger.");
        p3.setPrice(38.00);
        p3.setStock(4);
        p3.setRating(4.9);
        p3.setStatus(ProductStatus.EN_STOCK);
        p3.setCategory(catSerums);
        p3.addImage(new ProductImage("huile.jpg", "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?auto=format&fit=crop&w=800&q=80", 1));
        productRepository.save(p3);

        Product p4 = new Product();
        p4.setName("Lotion Tonique Florale Immortelle");
        p4.setBrand("Maison AURA");
        p4.setDescription("Hydrolat pur de fleurs immortelles et camomille romaine. Apaise les rougeurs, tonifie les pores et prépare la peau à recevoir les soins.");
        p4.setPrice(26.00);
        p4.setStock(25);
        p4.setRating(4.7);
        p4.setStatus(ProductStatus.EN_STOCK);
        p4.setCategory(catNettoyants);
        p4.addImage(new ProductImage("lotion.jpg", "https://images.unsplash.com/photo-1556228720-195a672e8a03?auto=format&fit=crop&w=800&q=80", 1));
        productRepository.save(p4);

        Product p5 = new Product();
        p5.setName("Masque Purifiant Argile Blanche & Spiruline");
        p5.setBrand("AURA Botanique");
        p5.setDescription("Masque crémeux détoxifiant aux minéraux marins. Resserre le grain de peau sans dessécher pour un effet peau neuve garanti.");
        p5.setPrice(32.00);
        p5.setStock(0);
        p5.setRating(4.6);
        p5.setStatus(ProductStatus.RUPTURE);
        p5.setCategory(catMasques);
        p5.addImage(new ProductImage("masque.jpg", "https://images.unsplash.com/photo-1556228722-d0b71946ec98?auto=format&fit=crop&w=800&q=80", 1));
        productRepository.save(p5);

        Product p6 = new Product();
        p6.setName("Baume Nuit Renaissance Cellulaire");
        p6.setBrand("Maison AURA");
        p6.setDescription("Soin nocturne intensif à la cire d’abeille bio et céramides végétales. Régénère l’épiderme pendant le sommeil pour un réveil lumineux.");
        p6.setPrice(54.00);
        p6.setStock(9);
        p6.setRating(5.0);
        p6.setStatus(ProductStatus.NOUVEAU);
        p6.setCategory(catCremes);
        p6.addImage(new ProductImage("baume.jpg", "https://images.unsplash.com/photo-1608248597359-0091871e411b?auto=format&fit=crop&w=800&q=80", 1));
        productRepository.save(p6);
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
        o1.addItem(new OrderItem(1L, "Élixir Céleste Régénérant", 1, 49.00));
        o1.addItem(new OrderItem(2L, "Crème Velours Éclat Divin", 1, 42.50));
        orderRepository.save(o1);

        Order o2 = new Order();
        o2.setCustomerName("Julien Mercier");
        o2.setEmail("j.mercier@orange.fr");
        o2.setPhone("07 89 01 23 45");
        o2.setAddress("8 Boulevard des Belges, 69006 Lyon");
        o2.setTotal(76.00);
        o2.setStatus(OrderStatus.EXPEDIEE);
        o2.setCreatedAt(LocalDateTime.now().minusDays(1));
        o2.addItem(new OrderItem(3L, "Huile Précieuse d’Argan & Néroli", 2, 38.00));
        orderRepository.save(o2);
    }
}
