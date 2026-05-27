package edu.cit.canadilla.wildcatslounge.config;

import edu.cit.canadilla.wildcatslounge.common.MenuCustomizationRules;
import edu.cit.canadilla.wildcatslounge.entity.Event;
import edu.cit.canadilla.wildcatslounge.entity.Ingredient;
import edu.cit.canadilla.wildcatslounge.entity.LoungeStatus;
import edu.cit.canadilla.wildcatslounge.entity.MenuItem;
import edu.cit.canadilla.wildcatslounge.entity.User;
import edu.cit.canadilla.wildcatslounge.repository.EventRepository;
import edu.cit.canadilla.wildcatslounge.repository.IngredientRepository;
import edu.cit.canadilla.wildcatslounge.repository.LoungeStatusRepository;
import edu.cit.canadilla.wildcatslounge.repository.MenuItemRepository;
import edu.cit.canadilla.wildcatslounge.repository.UserRepository;
import edu.cit.canadilla.wildcatslounge.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final String ADMIN_EMAIL = "staff.administrator@gmail.com";
    private static final String ADMIN_PASSWORD = "Welcome1!";

    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;
    private final EventRepository eventRepository;
    private final LoungeStatusRepository loungeStatusRepository;
    private final IngredientRepository ingredientRepository;
    private final PasswordUtil passwordUtil;

    @Override
    public void run(String... args) {
        seedAll();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        // Ensures admin exists after DB is fully connected (e.g. IntelliJ / Supabase)
        seedAdminUser();
    }

    public void seedAll() {
        seedAdminUser();
        seedMenuItems();
        seedIngredients();
        seedEvents();
        seedLoungeStatus();
    }

    @Transactional
    public void seedAdminUser() {
        User admin = userRepository.findByEmailIgnoreCase(ADMIN_EMAIL).orElseGet(() -> {
            User u = new User();
            u.setFirstname("Staff");
            u.setLastname("Administrator");
            u.setEmail(ADMIN_EMAIL);
            u.setStudentId(null);
            u.setRole("staff");
            return u;
        });
        admin.setPassword(passwordUtil.hashPassword(ADMIN_PASSWORD));
        admin.setRole("staff");
        userRepository.saveAndFlush(admin);
        System.out.println("✓ Staff admin ready: " + ADMIN_EMAIL + " (password reset to default)");
    }

    private void seedMenuItems() {
        if (menuItemRepository.count() > 0) {
            return;
        }
        saveMenu("Wildcats Brew", "Signature house blend espresso", "coffee", "89.00");
        saveMenu("Caramel Macchiato", "Espresso with steamed milk and caramel", "coffee", "120.00");
        saveMenu("Iced Americano", "Chilled espresso over ice", "coffee", "95.00");
        saveMenu("Matcha Latte", "Premium matcha with oat milk", "non-coffee", "130.00");
        saveMenu("Strawberry Lemonade", "Fresh strawberry citrus cooler", "non-coffee", "85.00");
        saveMenu("Mango Yakult", "Tropical probiotic drink", "non-coffee", "75.00");
        saveMenu("Chocolate Chip Cookie", "Fresh-baked daily", "treat", "45.00");
        saveMenu("Blueberry Muffin", "Soft muffin with wild blueberries", "treat", "55.00");
        saveMenu("Grilled Cheese Panini", "Classic comfort snack", "treat", "95.00");
        System.out.println("✓ Seeded menu items");
    }

    private void saveMenu(String name, String desc, String category, String price) {
        MenuItem item = new MenuItem();
        item.setName(name);
        item.setDescription(desc);
        item.setCategory(category);
        item.setPrice(new BigDecimal(price));
        item.setIsAvailable(true);
        MenuCustomizationRules.applyDefaults(item);
        menuItemRepository.save(item);
    }

    private void seedIngredients() {
        // Upsert seed so devs can tweak stock levels for testing.
        saveIngredient("Espresso Beans", "g", "5000", "500");

        // Example test stock levels:
        // - Fresh Milk: out of stock (affects milk-based drinks)
        // - Matcha Powder: low stock (still orderable)
        // - Hazelnut Syrup: out of stock (affects hazelnut add-on)
        saveIngredient("Fresh Milk", "ml", "0", "2000");
        saveIngredient("Oat Milk", "ml", "15000", "1500");
        saveIngredient("Simple Syrup", "ml", "8000", "800");
        saveIngredient("Vanilla Syrup", "ml", "5000", "500");
        saveIngredient("Hazelnut Syrup", "ml", "0", "400");
        saveIngredient("Matcha Powder", "g", "150", "200");
        saveIngredient("Chocolate Sauce", "ml", "6000", "600");
        saveIngredient("Whipped Cream", "ml", "10000", "1000");
        saveIngredient("Ice", "g", "30000", "3000");
        System.out.println("✓ Seeded / updated ingredients");
    }

    private void saveIngredient(String name, String unit, String qty, String lowThreshold) {
        Ingredient ing = ingredientRepository.findByNameIgnoreCase(name).orElseGet(Ingredient::new);
        ing.setName(name);
        ing.setUnit(unit);
        ing.setQuantityOnHand(new BigDecimal(qty));
        ing.setLowStockThreshold(new BigDecimal(lowThreshold));
        ingredientRepository.save(ing);
    }

    private void seedEvents() {
        if (eventRepository.count() > 0) {
            return;
        }
        User admin = userRepository.findByEmailIgnoreCase(ADMIN_EMAIL).orElse(null);

        Event e1 = new Event();
        e1.setTitle("Acoustic Night at the Lounge");
        e1.setDescription("Live student performers and open mic.");
        e1.setStartDatetime(LocalDateTime.now().plusDays(3).withHour(17).withMinute(0));
        e1.setEndDatetime(LocalDateTime.now().plusDays(3).withHour(20).withMinute(0));
        e1.setCreatedBy(admin);
        eventRepository.save(e1);

        Event e2 = new Event();
        e2.setTitle("Wildcats Study Jam");
        e2.setDescription("Quiet study hours with free Wi-Fi and promo drinks.");
        e2.setStartDatetime(LocalDateTime.now().plusDays(7).withHour(14).withMinute(0));
        e2.setEndDatetime(LocalDateTime.now().plusDays(7).withHour(18).withMinute(0));
        e2.setCreatedBy(admin);
        eventRepository.save(e2);

        System.out.println("✓ Seeded events");
    }

    private void seedLoungeStatus() {
        if (loungeStatusRepository.count() > 0) {
            return;
        }
        LoungeStatus status = new LoungeStatus();
        status.setOccupancyLevel("low");
        User admin = userRepository.findByEmailIgnoreCase(ADMIN_EMAIL).orElse(null);
        status.setUpdatedBy(admin);
        loungeStatusRepository.save(status);
        System.out.println("✓ Seeded lounge status");
    }
}
