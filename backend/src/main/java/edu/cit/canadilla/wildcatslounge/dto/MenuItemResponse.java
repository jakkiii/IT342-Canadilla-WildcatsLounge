package edu.cit.canadilla.wildcatslounge.dto;

import edu.cit.canadilla.wildcatslounge.entity.MenuItem;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MenuItemResponse {
    private Long id;
    private String name;
    private String description;
    private String category;
    private BigDecimal price;
    private Boolean isAvailable;
    private Boolean inventorySufficient;
    private String imageUrl;
    private Boolean allowHot;
    private Boolean allowIced;
    private Boolean allowBlended;
    private Boolean allowAddons;
    private Boolean allowSugarLevel;

    public static MenuItemResponse from(MenuItem item) {
        MenuItemResponse r = new MenuItemResponse();
        r.setId(item.getId());
        r.setName(item.getName());
        r.setDescription(item.getDescription());
        r.setCategory(item.getCategory());
        r.setPrice(item.getPrice());
        r.setIsAvailable(item.getIsAvailable());
        r.setInventorySufficient(true);
        r.setImageUrl(item.getImageUrl());
        r.setAllowHot(item.getAllowHot());
        r.setAllowIced(item.getAllowIced());
        r.setAllowBlended(item.getAllowBlended());
        r.setAllowAddons(item.getAllowAddons());
        r.setAllowSugarLevel(item.getAllowSugarLevel());
        return r;
    }

    public static MenuItemResponse from(MenuItem item, boolean inventorySufficient) {
        MenuItemResponse r = from(item);
        r.setInventorySufficient(inventorySufficient);
        r.setIsAvailable(Boolean.TRUE.equals(item.getIsAvailable()) && inventorySufficient);
        return r;
    }
}
