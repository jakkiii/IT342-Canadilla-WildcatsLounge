package edu.cit.canadilla.wildcatslounge.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_order_item_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private OrderItem parentItem;

    @Column(name = "menu_item_id")
    private Long menuItemId;

    @Column(name = "item_name", nullable = false, length = 150)
    private String itemName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "price_at_purchase", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceAtPurchase;

    @Column(name = "customization_notes", length = 255)
    private String customizationNotes;

    @Enumerated(EnumType.STRING)
    @Column(name = "serving_type", length = 20)
    private edu.cit.canadilla.wildcatslounge.common.ServingType servingType;

    @Column(name = "sugar_level_percent")
    private Integer sugarLevelPercent;
}
