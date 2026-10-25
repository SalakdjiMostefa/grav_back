package com.aura.cosmetics.service;

import com.aura.cosmetics.dto.CategoryDTO;
import com.aura.cosmetics.dto.ProductDTO;
import com.aura.cosmetics.dto.ProductStockDTO;
import com.aura.cosmetics.entity.ProductStock;
import com.aura.cosmetics.dto.ProductImageDTO;
import com.aura.cosmetics.dto.ProductPriceDTO;
import com.aura.cosmetics.entity.Category;
import com.aura.cosmetics.entity.Product;
import com.aura.cosmetics.entity.ProductImage;
import com.aura.cosmetics.entity.ProductPrice;
import com.aura.cosmetics.enums.PriceType;
import com.aura.cosmetics.enums.ProductStatus;
import com.aura.cosmetics.repository.CategoryRepository;
import com.aura.cosmetics.repository.ProductRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        boolean canSeeWholesale = isWholesaleAuthorized();
        return productRepository.findAll().stream()
                .map(p -> toDTO(p, canSeeWholesale))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        boolean canSeeWholesale = isWholesaleAuthorized();
        return productRepository.findById(id)
                .map(p -> toDTO(p, canSeeWholesale))
                .orElse(null);
    }

    public ProductDTO createProduct(ProductDTO dto) {
        Product product = new Product();
        mapDtoToEntity(dto, product);
        Product saved = productRepository.save(product);
        return toDTO(saved, true);
    }

    public ProductDTO updateProduct(Long id, ProductDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'ID: " + id));

        mapDtoToEntity(dto, product);
        Product updated = productRepository.save(product);
        return toDTO(updated, true);
    }

    public boolean deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void mapDtoToEntity(ProductDTO dto, Product product) {
        product.setName(dto.getName());
        product.setBrand(dto.getBrand() != null ? dto.getBrand() : "AURA Cosmétiques");
        product.setDescription(dto.getDescription());

        // --- Prix rapide (legacy champs plats) ---
        product.setPrice(dto.getPrice() != null ? dto.getPrice() : 0.0);

        // --- Gestion de la promotion ---
        product.setPromotionType(dto.getPromotionType());
        product.setPromotionValue(dto.getPromotionValue());
        product.setPromotionStartDate(dto.getPromotionStartDate());
        product.setPromotionEndDate(dto.getPromotionEndDate());

        // --- Prix structurés (liste) : priorité sur les champs plats ---
        if (dto.getPrices() != null && !dto.getPrices().isEmpty()) {
            // Reconstruire la liste à partir du DTO
            product.getPrices().clear();
            for (ProductPriceDTO ppDto : dto.getPrices()) {
                if (ppDto.getPrice() == null || ppDto.getType() == null) continue;
                ProductPrice pp = new ProductPrice(
                    ppDto.getType(),
                    ppDto.getPrice(),
                    ppDto.getMinQty() != null ? ppDto.getMinQty() : 1,
                    ppDto.getLabel()
                );
                product.addPrice(pp);
            }
            // Synchroniser aussi les champs plats à partir de la liste
            dto.getPrices().stream()
                .filter(p -> p.getType() == PriceType.DETAIL)
                .findFirst()
                .ifPresent(p -> product.setPrice(p.getPrice()));
            dto.getPrices().stream()
                .filter(p -> p.getType() == PriceType.GROS)
                .findFirst()
                .ifPresent(p -> {
                    product.setWholesalePrice(p.getPrice());
                    product.setWholesaleMinQty(p.getMinQty() != null ? p.getMinQty() : 12);
                });
        } else {
            // Pas de liste -> utiliser les champs plats legacy
            product.setWholesalePrice(dto.getWholesalePrice());
            product.setWholesaleMinQty(dto.getWholesaleMinQty() != null ? dto.getWholesaleMinQty() : 12);

            // Générer automatiquement la liste de prix depuis les champs plats
            product.getPrices().clear();
            ProductPrice detailPrice = new ProductPrice(
                PriceType.DETAIL,
                product.getPrice(),
                1,
                "Vente au Détail"
            );
            product.addPrice(detailPrice);
            if (dto.getWholesalePrice() != null && dto.getWholesalePrice() > 0) {
                ProductPrice grossPrice = new ProductPrice(
                    PriceType.GROS,
                    dto.getWholesalePrice(),
                    dto.getWholesaleMinQty() != null ? dto.getWholesaleMinQty() : 12,
                    "Prix Grossiste - Carton de " + (dto.getWholesaleMinQty() != null ? dto.getWholesaleMinQty() : 12) + " pcs"
                );
                product.addPrice(grossPrice);
            }
        }

        // --- Stock & Stock Gros (Cartons) ---
        if (dto.getStock() != null) {
            ProductStockDTO sDto = dto.getStock();
            int upc = (sDto.getUnitsPerCarton() != null && sDto.getUnitsPerCarton() > 0) ? sDto.getUnitsPerCarton() : 12;
            int cartons = sDto.getCartons() != null ? sDto.getCartons() : (dto.getWholesaleStock() != null ? dto.getWholesaleStock() : 0);
            int loose = sDto.getLooseUnits() != null ? sDto.getLooseUnits() : 0;

            ProductStock st = product.getStock();
            if (st == null) st = new ProductStock();
            st.setUnitsPerCarton(upc);
            st.setCartons(cartons);
            st.setLooseUnits(loose);
            st.recalculateTotal();
            if (sDto.getAlertThresholdCartons() != null) {
                st.setAlertThresholdCartons(sDto.getAlertThresholdCartons());
            }
            product.setStock(st);

            // Mettre à jour automatiquement le statut selon le stock
            if (st.getTotalUnits() == 0) {
                product.setStatus(ProductStatus.RUPTURE);
            } else if (product.getStatus() == ProductStatus.RUPTURE) {
                product.setStatus(ProductStatus.EN_STOCK);
            }
        } else if (dto.getWholesaleStock() != null) {
            product.setWholesaleStock(dto.getWholesaleStock());
        }

        product.setRating(dto.getRating() != null ? dto.getRating() : 5.0);
        product.setStatus(dto.getStatus() != null ? dto.getStatus() : ProductStatus.EN_STOCK);

        Long categoryId = extractCategoryId(dto.getCategory());
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId).orElse(null);
            product.setCategory(category);
        }

        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            List<ProductImage> imageEntities = new ArrayList<>();
            for (ProductImageDTO imgDto : dto.getImages()) {
                ProductImage img = new ProductImage(
                        imgDto.getOriginalName() != null ? imgDto.getOriginalName() : product.getName(),
                        imgDto.getStoredFileName(),
                        imgDto.getDisplayOrder() != null ? imgDto.getDisplayOrder() : 1
                );
                imageEntities.add(img);
            }
            product.setImages(imageEntities);
        }
    }

    private Long extractCategoryId(Object categoryObj) {
        if (categoryObj == null) return null;
        if (categoryObj instanceof Number) {
            return ((Number) categoryObj).longValue();
        }
        if (categoryObj instanceof Map) {
            Object idVal = ((Map<?, ?>) categoryObj).get("id");
            if (idVal instanceof Number) {
                return ((Number) idVal).longValue();
            }
        }
        try {
            return Long.parseLong(categoryObj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean isWholesaleAuthorized() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return false;
            }
            return auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_WHOLESALE"));
        } catch (Exception e) {
            return false;
        }
    }

    public ProductDTO toDTO(Product p) {
        return toDTO(p, isWholesaleAuthorized());
    }

    public ProductDTO toDTO(Product p, boolean canSeeWholesale) {
        if (p == null) return null;
        ProductDTO dto = new ProductDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setBrand(p.getBrand());
        dto.setDescription(p.getDescription());
        dto.setPrice(p.getPrice());

        // Promotion info
        dto.setPromotionType(p.getPromotionType());
        dto.setPromotionValue(p.getPromotionValue());
        dto.setPromotionStartDate(p.getPromotionStartDate());
        dto.setPromotionEndDate(p.getPromotionEndDate());
        dto.setPromotionActive(p.isPromotionActive());
        dto.setDiscountedPrice(p.getDiscountedPrice());

        // Transmet toujours les informations grossiste (champs plats compatibles)
        dto.setWholesalePrice(p.getWholesalePrice());
        dto.setWholesaleMinQty(p.getWholesaleMinQty() != null ? p.getWholesaleMinQty() : 12);
        dto.setWholesaleStock(p.getWholesaleStock());

        // Transmet la liste structurée de prix
        if (p.getPrices() != null && !p.getPrices().isEmpty()) {
            List<ProductPriceDTO> priceDtos = p.getPrices().stream()
                    .map(pp -> new ProductPriceDTO(pp.getId(), pp.getType(), pp.getPrice(), pp.getMinQty(), pp.getLabel()))
                    .collect(Collectors.toList());
            dto.setPrices(priceDtos);
        } else {
            // Générer la liste depuis les champs plats si elle est vide (migration progressive)
            List<ProductPriceDTO> generated = new java.util.ArrayList<>();
            generated.add(new ProductPriceDTO(null, PriceType.DETAIL, p.getPrice(), 1, "Vente au Détail"));
            if (p.getWholesalePrice() != null && p.getWholesalePrice() > 0) {
                int qty = p.getWholesaleMinQty() != null ? p.getWholesaleMinQty() : 12;
                generated.add(new ProductPriceDTO(null, PriceType.GROS, p.getWholesalePrice(), qty,
                        "Prix Grossiste - Carton de " + qty + " pcs"));
            }
            dto.setPrices(generated);
        }

        if (p.getStock() != null) {
            ProductStock st = p.getStock();
            dto.setStock(new ProductStockDTO(
                st.getCartons(),
                st.getUnitsPerCarton(),
                st.getLooseUnits(),
                st.getTotalUnits(),
                st.getAlertThresholdCartons()
            ));
        } else {
            dto.setStock(new ProductStockDTO(0, 12, 0, 0, 2));
        }
        dto.setRating(p.getRating());
        dto.setStatus(p.getStatus());

        if (p.getCategory() != null) {
            dto.setCategory(new CategoryDTO(
                    p.getCategory().getId(),
                    p.getCategory().getName(),
                    p.getCategory().getDescription(),
                    p.getCategory().getImageUrl()
            ));
        }

        if (p.getImages() != null) {
            List<ProductImageDTO> imgDtos = p.getImages().stream()
                    .map(img -> new ProductImageDTO(img.getId(), img.getOriginalName(), img.getStoredFileName(), img.getDisplayOrder()))
                    .collect(Collectors.toList());
            dto.setImages(imgDtos);
        }

        return dto;
    }
}
