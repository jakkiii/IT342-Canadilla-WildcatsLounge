package edu.cit.canadilla.wildcatslounge.feature.menu.dto;

import edu.cit.canadilla.wildcatslounge.feature.menu.entity.MenuCategory;
import edu.cit.canadilla.wildcatslounge.common.ServingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemResponse {
    private Long id;
    private String name;
    private String description;
    private MenuCategory category;
    private BigDecimal price;
    private BigDecimal hotPrice;
    private BigDecimal icedPrice;
    private BigDecimal blendedPrice;
    private List<ServingType> availableServingTypes;
    private Boolean isAvailable;
    private String imageUrl;
}
