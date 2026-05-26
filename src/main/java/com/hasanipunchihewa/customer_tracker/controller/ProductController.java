package com.hasanipunchihewa.customer_tracker.controller;

import com.hasanipunchihewa.customer_tracker.dto.ProductRequest;
import com.hasanipunchihewa.customer_tracker.dto.ProductResponse;
import com.hasanipunchihewa.customer_tracker.model.Product;
import com.hasanipunchihewa.customer_tracker.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ProductResponse getProduct(@PathVariable UUID id) {
        return toResponse(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(productService.createProduct(product)));
    }

    @PutMapping("/{id}")
    public ProductResponse updateProduct(@PathVariable UUID id, @RequestBody ProductRequest request) {
        Product updated = new Product();
        updated.setName(request.getName());
        updated.setDescription(request.getDescription());
        updated.setPrice(request.getPrice());

        return toResponse(productService.updateProduct(id, updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    private ProductResponse toResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
