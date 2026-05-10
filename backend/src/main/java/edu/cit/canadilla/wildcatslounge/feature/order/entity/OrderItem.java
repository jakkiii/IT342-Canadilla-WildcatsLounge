package edu.cit.canadilla.wildcatslounge.feature.order.entity;

import edu.cit.canadilla.wildcatslounge.common.ServingType;
import edu.cit.canadilla.wildcatslounge.feature.menu.entity.MenuItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Column(name = "item_name", nullable = false, length = 120)
    private String itemName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "price_at_purchase", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceAtPurchase;

    @Column(name = "customization_notes", length = 255)
    private String customizationNotes;

    @Enumerated(EnumType.STRING)
    @Column(name = "serving_type", nullable = false, length = 16)
    private ServingType servingType;
}
