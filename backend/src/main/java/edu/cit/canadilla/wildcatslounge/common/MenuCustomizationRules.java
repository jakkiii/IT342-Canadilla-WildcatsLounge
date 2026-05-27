package edu.cit.canadilla.wildcatslounge.common;

import edu.cit.canadilla.wildcatslounge.entity.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class MenuCustomizationRules {

    private MenuCustomizationRules() {
    }

    public static void applyDefaults(MenuItem item) {
        Rules defaults = defaultsFor(item.getCategory(), item.getName());
        if (item.getAllowHot() == null) {
            item.setAllowHot(defaults.allowHot());
        }
        if (item.getAllowIced() == null) {
            item.setAllowIced(defaults.allowIced());
        }
        if (item.getAllowBlended() == null) {
            item.setAllowBlended(defaults.allowBlended());
        }
        if (item.getAllowAddons() == null) {
            item.setAllowAddons(defaults.allowAddons());
        }
        if (item.getAllowSugarLevel() == null) {
            item.setAllowSugarLevel(defaults.allowSugarLevel());
        }
    }

    public static void syncToDefaults(MenuItem item) {
        Rules defaults = defaultsFor(item.getCategory(), item.getName());
        item.setAllowHot(defaults.allowHot());
        item.setAllowIced(defaults.allowIced());
        item.setAllowBlended(defaults.allowBlended());
        item.setAllowAddons(defaults.allowAddons());
        item.setAllowSugarLevel(defaults.allowSugarLevel());
    }

    public static boolean allowsServingType(MenuItem item, ServingType servingType) {
        if (servingType == null || servingType == ServingType.NONE) {
            return !requiresServingType(item);
        }
        return switch (servingType) {
            case HOT -> Boolean.TRUE.equals(item.getAllowHot());
            case ICED -> Boolean.TRUE.equals(item.getAllowIced());
            case BLENDED -> Boolean.TRUE.equals(item.getAllowBlended());
            case NONE -> !requiresServingType(item);
        };
    }

    public static boolean requiresServingType(MenuItem item) {
        return Boolean.TRUE.equals(item.getAllowHot())
                || Boolean.TRUE.equals(item.getAllowIced())
                || Boolean.TRUE.equals(item.getAllowBlended());
    }

    public static boolean supportsAddons(MenuItem item) {
        return Boolean.TRUE.equals(item.getAllowAddons());
    }

    public static boolean supportsSugarLevel(MenuItem item) {
        return Boolean.TRUE.equals(item.getAllowSugarLevel());
    }

    public static boolean isValidSugarLevel(Integer sugarLevelPercent) {
        return sugarLevelPercent != null
                && (sugarLevelPercent == 0
                || sugarLevelPercent == 25
                || sugarLevelPercent == 50
                || sugarLevelPercent == 75
                || sugarLevelPercent == 100);
    }

    public static boolean isAddonCategory(String category) {
        String normalized = normalize(category);
        return normalized.contains("add-on") || normalized.contains("addon") || normalized.endsWith("-add-on");
    }

    public static List<ServingType> allowedServingTypes(MenuItem item) {
        List<ServingType> allowed = new ArrayList<>();
        if (Boolean.TRUE.equals(item.getAllowHot())) {
            allowed.add(ServingType.HOT);
        }
        if (Boolean.TRUE.equals(item.getAllowIced())) {
            allowed.add(ServingType.ICED);
        }
        if (Boolean.TRUE.equals(item.getAllowBlended())) {
            allowed.add(ServingType.BLENDED);
        }
        return allowed;
    }

    private static Rules defaultsFor(String category, String itemName) {
        String normalizedCategory = normalize(category);
        String normalizedName = normalize(itemName);

        if (isAddonCategory(normalizedCategory)) {
            return new Rules(false, false, false, false, false);
        }

        if (normalizedCategory.equals("treat") || normalizedCategory.equals("treats")) {
            return new Rules(false, false, false, false, false);
        }

        if (normalizedName.contains("matcha latte")) {
            return new Rules(true, true, false, false, true);
        }

        if (normalizedName.contains("hot choco") || normalizedName.contains("hot chocolate")) {
            return new Rules(true, false, false, false, true);
        }

        if (normalizedCategory.equals("coffee")
                || normalizedCategory.equals("flavored-latte")
                || normalizedCategory.equals("matcha-series")) {
            return new Rules(true, true, true, true, false);
        }

        if (normalizedCategory.equals("beverages") || normalizedCategory.equals("non-coffee")) {
            return new Rules(false, true, false, false, true);
        }

        return new Rules(false, false, false, false, false);
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim()
                .toLowerCase(Locale.ENGLISH)
                .replace('_', '-')
                .replaceAll("\\s+", "-");
    }

    private record Rules(
            boolean allowHot,
            boolean allowIced,
            boolean allowBlended,
            boolean allowAddons,
            boolean allowSugarLevel) {
    }
}
