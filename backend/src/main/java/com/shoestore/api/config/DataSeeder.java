package com.shoestore.api.config;

import com.shoestore.api.product.Product;
import com.shoestore.api.product.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedProducts(ProductRepository products) {
        return args -> {
            if (products.count() > 0) {
                return;
            }

            products.saveAll(List.of(
                    new Product(
                            "Cloudrunner 2",
                            "On",
                            "Running",
                            "Lightweight daily running shoe with a cushioned, stable ride.",
                            new BigDecimal("12999.00"),
                            "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=900&q=85",
                            18
                    ),
                    new Product(
                            "Air Max Pulse",
                            "Nike",
                            "Lifestyle",
                            "A bold everyday sneaker with responsive cushioning.",
                            new BigDecimal("11995.00"),
                            "https://images.unsplash.com/photo-1552346154-21d32810aba3?auto=format&fit=crop&w=900&q=85",
                            24
                    ),
                    new Product(
                            "574 Core",
                            "New Balance",
                            "Lifestyle",
                            "A versatile classic built for comfort from morning to night.",
                            new BigDecimal("8999.00"),
                            "https://images.unsplash.com/photo-1539185441755-769473a23570?auto=format&fit=crop&w=900&q=85",
                            14
                    ),
                    new Product(
                            "Forum Low",
                            "Adidas",
                            "Lifestyle",
                            "A court-inspired low top with a clean leather look.",
                            new BigDecimal("9999.00"),
                            "https://images.unsplash.com/photo-1608231387042-66d1773070a5?auto=format&fit=crop&w=900&q=85",
                            20
                    )
            ));
        };
    }
}