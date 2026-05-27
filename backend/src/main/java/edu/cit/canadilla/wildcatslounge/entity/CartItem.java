package edu.cit.canadilla.wildcatslounge.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_cart_item_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CartItem parentItem;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "customization_notes", length = 255)
    private String customizationNotes;

    @Enumerated(EnumType.STRING)
    @Column(name = "serving_type", length = 20)
    private edu.cit.canadilla.wildcatslounge.common.ServingType servingType;

    @Column(name = "sugar_level_percent")
    private Integer sugarLevelPercent;
}
