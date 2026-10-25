package com.aura.cosmetics.service;

import com.aura.cosmetics.dto.OrderDTO;
import com.aura.cosmetics.dto.OrderItemDTO;
import com.aura.cosmetics.entity.Order;
import com.aura.cosmetics.entity.OrderItem;
import com.aura.cosmetics.entity.Product;
import com.aura.cosmetics.entity.ProductStock;
import com.aura.cosmetics.enums.OrderStatus;
import com.aura.cosmetics.enums.ProductStatus;
import com.aura.cosmetics.repository.OrderRepository;
import com.aura.cosmetics.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public OrderDTO createOrder(OrderDTO dto) {
        Order order = new Order();
        order.setCustomerName(dto.getCustomerName());
        order.setEmail(dto.getEmail());
        order.setPhone(dto.getPhone());
        order.setAddress(dto.getAddress());
        order.setTotal(dto.getTotal() != null ? dto.getTotal() : 0.0);
        order.setStatus(dto.getStatus() != null ? dto.getStatus() : OrderStatus.PAYEE);
        order.setCreatedAt(LocalDateTime.now());

        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            List<OrderItem> items = new ArrayList<>();
            for (OrderItemDTO itemDto : dto.getItems()) {
                OrderItem item = new OrderItem(
                        itemDto.getProductId(),
                        itemDto.getProductName(),
                        itemDto.getQuantity() != null ? itemDto.getQuantity() : 1,
                        itemDto.getUnitPrice() != null ? itemDto.getUnitPrice() : 0.0,
                        Boolean.TRUE.equals(itemDto.getIsWholesale()),
                        itemDto.getWholesaleStock()
                );
                items.add(item);

                // Décrémenter le stock du produit en temps réel
                if (itemDto.getProductId() != null) {
                    productRepository.findById(itemDto.getProductId()).ifPresent(prod -> {
                        int currentTotal = prod.getStock().getTotalUnits();
                        int remaining = Math.max(0, currentTotal - item.getQuantity());
                        prod.setStock(remaining);
                        if (remaining == 0) {
                            prod.setStatus(ProductStatus.RUPTURE);
                        }
                        productRepository.save(prod);
                    });
                }
            }
            order.setItems(items);
        }

        Order saved = orderRepository.save(order);
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public OrderDTO updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID: " + id));

        order.setStatus(status);
        Order updated = orderRepository.save(order);
        return toDTO(updated);
    }

    public OrderDTO toDTO(Order order) {
        if (order == null) return null;
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setCustomerName(order.getCustomerName());
        dto.setEmail(order.getEmail());
        dto.setPhone(order.getPhone());
        dto.setAddress(order.getAddress());
        dto.setTotal(order.getTotal());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());

        if (order.getItems() != null) {
            List<OrderItemDTO> itemDtos = order.getItems().stream()
                    .map(it -> new OrderItemDTO(
                            it.getProductId(),
                            it.getProductName(),
                            it.getQuantity(),
                            it.getUnitPrice(),
                            it.getIsWholesale(),
                            it.getWholesaleStock()
                    ))
                    .collect(Collectors.toList());
            dto.setItems(itemDtos);
        }

        return dto;
    }
}
