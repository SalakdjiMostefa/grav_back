package com.aura.cosmetics.service;

import com.aura.cosmetics.dto.CategoryDTO;
import com.aura.cosmetics.dto.ProductDTO;
import com.aura.cosmetics.dto.ProductImageDTO;
import com.aura.cosmetics.entity.Category;
import com.aura.cosmetics.entity.Product;
import com.aura.cosmetics.entity.ProductImage;
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
        product.setPrice(dto.getPrice() != null ? dto.getPrice() : 0.0);
        product.setWholesalePrice(dto.getWholesalePrice());
        product.setWholesaleMinQty(dto.getWholesaleMinQty() != null ? dto.getWholesaleMinQty() : 12);
        product.setStock(dto.getStock() != null ? dto.getStock() : 0);
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

        if (canSeeWholesale) {
            dto.setWholesalePrice(p.getWholesalePrice());
            dto.setWholesaleMinQty(p.getWholesaleMinQty() != null ? p.getWholesaleMinQty() : 12);
        } else {
            dto.setWholesalePrice(null);
            dto.setWholesaleMinQty(null);
        }

        dto.setStock(p.getStock());
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
