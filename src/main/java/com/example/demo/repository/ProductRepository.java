package com.example.demo.repository;

import com.example.demo.entity.Product;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProductRepository {

    @PersistenceContext(unitName = "default")
    private EntityManager entityManager;

    public List<Product> findAll() {
        return entityManager.createNamedQuery("Product.findAll", Product.class)
                .getResultList();
    }

    public Optional<Product> findById(Long id) {
        Product product = entityManager.find(Product.class, id);
        return Optional.ofNullable(product);
    }

    public List<Product> findByCategory(String category) {
        return entityManager.createNamedQuery("Product.findByCategory", Product.class)
                .setParameter("category", category)
                .getResultList();
    }

    public List<Product> findInStock() {
        return entityManager.createNamedQuery("Product.findInStock", Product.class)
                .getResultList();
    }
}
