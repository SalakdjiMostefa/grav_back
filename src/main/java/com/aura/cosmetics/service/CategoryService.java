package com.aura.cosmetics.service;

import com.aura.cosmetics.dto.CategoryDTO;
import com.aura.cosmetics.entity.Category;
import com.aura.cosmetics.entity.Product;
import com.aura.cosmetics.repository.CategoryRepository;
import com.aura.cosmetics.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    @Transactional
    public CategoryDTO createCategory(CategoryDTO dto) {
        Category cat = new Category(dto.getName(), dto.getDescription(), dto.getImageUrl());
        Category saved = categoryRepository.save(cat);
        return toDTO(saved);
    }

    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryDTO dto) {
        Category cat = categoryRepository.findById(id).orElse(null);
        if (cat == null) return null;
        if (dto.getName() != null) cat.setName(dto.getName());
        if (dto.getDescription() != null) cat.setDescription(dto.getDescription());
        if (dto.getImageUrl() != null) cat.setImageUrl(dto.getImageUrl());
        Category updated = categoryRepository.save(cat);
        return toDTO(updated);
    }

    @Transactional
    public boolean deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) return false;
        List<Product> products = productRepository.findByCategoryId(id);
        for (Product p : products) {
            p.setCategory(null);
            productRepository.save(p);
        }
        categoryRepository.deleteById(id);
        return true;
    }

    public CategoryDTO toDTO(Category cat) {
        if (cat == null) return null;
        return new CategoryDTO(cat.getId(), cat.getName(), cat.getDescription(), cat.getImageUrl());
    }
}