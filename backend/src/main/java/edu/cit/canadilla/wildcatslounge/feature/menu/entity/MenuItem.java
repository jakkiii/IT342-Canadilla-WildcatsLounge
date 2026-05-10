package edu.cit.canadilla.wildcatslounge.feature.menu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "menu_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MenuCategory category;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "hot_price", precision = 10, scale = 2)
    private BigDecimal hotPrice;

    @Column(name = "iced_price", precision = 10, scale = 2)
    private BigDecimal icedPrice;

    @Column(name = "blended_price", precision = 10, scale = 2)
    private BigDecimal blendedPrice;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (isAvailable == null) {
            isAvailable = true;
        }
        createdAt = LocalDateTime.now();
    }
}
