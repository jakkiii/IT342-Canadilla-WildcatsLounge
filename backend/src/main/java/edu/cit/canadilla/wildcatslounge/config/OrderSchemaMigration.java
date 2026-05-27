package edu.cit.canadilla.wildcatslounge.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Aligns the PostgreSQL orders_status_check constraint with application status values.
 * Hibernate ddl-auto=update does not alter existing CHECK constraints.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderSchemaMigration {

    private final JdbcTemplate jdbcTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void migrateOrderStatusConstraint() {
        try {
            int normalized = jdbcTemplate.update(
                    "UPDATE orders SET status = LOWER(TRIM(status)) WHERE status <> LOWER(TRIM(status))");
            if (normalized > 0) {
                log.info("Normalized {} order status value(s) to lowercase", normalized);
            }

            jdbcTemplate.execute("ALTER TABLE orders DROP CONSTRAINT IF EXISTS orders_status_check");
            jdbcTemplate.execute(
                    "ALTER TABLE orders ADD CONSTRAINT orders_status_check "
                            + "CHECK (status IN ('pending', 'preparing', 'ready', 'completed'))");
            log.info("orders_status_check constraint updated for pending/preparing/ready/completed");
        } catch (Exception e) {
            log.warn("Order status constraint migration skipped: {}", e.getMessage());
        }
    }
}
