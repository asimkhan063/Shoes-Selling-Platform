package com.shoestore.api.cart;

import com.shoestore.api.product.Product;
import com.shoestore.api.user.User;
import jakarta.persistence.*;

@Entity
@Table(
        name = "cart_items",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "product_id"}
        )
)
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    protected CartItem() {}

    public CartItem(User user, Product product, Integer quantity) {
        this.user = user;
        this.product = product;
        this.quantity = quantity;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Product getProduct() { return product; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}