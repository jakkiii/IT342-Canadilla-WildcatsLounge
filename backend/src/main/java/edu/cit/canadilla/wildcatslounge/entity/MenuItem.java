package edu.cit.canadilla.wildcatslounge.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "menu_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 30)
    private String category; // coffee, non-coffee, treat

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "allow_hot", nullable = false)
    private Boolean allowHot = false;

    @Column(name = "allow_iced", nullable = false)
    private Boolean allowIced = false;

    @Column(name = "allow_blended", nullable = false)
    private Boolean allowBlended = false;

    @Column(name = "allow_addons", nullable = false)
    private Boolean allowAddons = false;

    @Column(name = "allow_sugar_level", nullable = false)
    private Boolean allowSugarLevel = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isAvailable == null) {
            isAvailable = true;
        }
        if (allowHot == null) {
            allowHot = false;
        }
        if (allowIced == null) {
            allowIced = false;
        }
        if (allowBlended == null) {
            allowBlended = false;
        }
        if (allowAddons == null) {
            allowAddons = false;
        }
        if (allowSugarLevel == null) {
            allowSugarLevel = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
