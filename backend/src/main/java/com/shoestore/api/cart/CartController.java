package com.shoestore.api.cart;

import com.shoestore.api.product.Product;
import com.shoestore.api.product.ProductRepository;
import com.shoestore.api.user.User;
import com.shoestore.api.user.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartItemRepository cartItems;
    private final ProductRepository products;
    private final UserRepository users;

    public CartController(
            CartItemRepository cartItems,
            ProductRepository products,
            UserRepository users
    ) {
        this.cartItems = cartItems;
        this.products = products;
        this.users = users;
    }

    public record AddItemRequest(
            @NotNull Long productId,
            @NotNull @Min(1) Integer quantity
    ) {}

    public record CartLine(
            Long id,
            Long productId,
            String name,
            String brand,
            String imageUrl,
            BigDecimal price,
            Integer quantity,
            BigDecimal lineTotal
    ) {}

    public record CartResponse(List<CartLine> items, BigDecimal total) {}

    @GetMapping
    public CartResponse getCart(Authentication authentication) {
        User user = currentUser(authentication);
        return cartResponse(user);
    }

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public CartResponse addItem(
            Authentication authentication,
            @Valid @RequestBody AddItemRequest request
    ) {
        User user = currentUser(authentication);
        Product product = products.findById(request.productId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found"
                ));

        if (request.quantity() > product.getStock()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Not enough stock"
            );
        }

        CartItem item = cartItems.findByUserIdAndProductId(
                user.getId(), product.getId()
        ).orElse(null);

        int newQuantity = request.quantity()
                + (item == null ? 0 : item.getQuantity());

        if (newQuantity > product.getStock()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Not enough stock"
            );
        }

        if (item == null) {
            item = new CartItem(user, product, newQuantity);
        } else {
            item.setQuantity(newQuantity);
        }

        cartItems.save(item);
        return cartResponse(user);
    }

    @PatchMapping("/items/{itemId}")
    @Transactional
    public CartResponse updateQuantity(
            Authentication authentication,
            @PathVariable Long itemId,
            @Valid @RequestBody AddItemRequest request
    ) {
        User user = currentUser(authentication);
        CartItem item = cartItems.findByIdAndUserId(itemId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cart item not found"
                ));

        if (!item.getProduct().getId().equals(request.productId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Product does not match cart item"
            );
        }

        if (request.quantity() > item.getProduct().getStock()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Not enough stock"
            );
        }

        item.setQuantity(request.quantity());
        return cartResponse(user);
    }

    @DeleteMapping("/items/{itemId}")
    @Transactional
    public CartResponse removeItem(
            Authentication authentication,
            @PathVariable Long itemId
    ) {
        User user = currentUser(authentication);
        CartItem item = cartItems.findByIdAndUserId(itemId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cart item not found"
                ));

        cartItems.delete(item);
        return cartResponse(user);
    }

    @DeleteMapping
    @Transactional
    public void clearCart(Authentication authentication) {
        User user = currentUser(authentication);
        cartItems.deleteAllByUserId(user.getId());
    }

    private User currentUser(Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Login required"
            );
        }

        String email = authentication.getName();
        return users.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not found"
                ));
    }

    private CartResponse cartResponse(User user) {
        List<CartLine> lines = cartItems.findAllByUserId(user.getId())
                .stream()
                .map(item -> {
                    Product product = item.getProduct();
                    BigDecimal lineTotal = product.getPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));

                    return new CartLine(
                            item.getId(),
                            product.getId(),
                            product.getName(),
                            product.getBrand(),
                            product.getImageUrl(),
                            product.getPrice(),
                            item.getQuantity(),
                            lineTotal
                    );
                })
                .toList();

        BigDecimal total = lines.stream()
                .map(CartLine::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(lines, total);
    }
}