package com.shoestore.api.cart;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findAllByUserId(Long userId);

    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);

    Optional<CartItem> findByIdAndUserId(Long id, Long userId);

    void deleteAllByUserId(Long userId);
}