package edu.cit.canadilla.wildcatslounge;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WildcatsLoungeApplication {

    public static void main(String[] args) {
        loadDotEnv();
        validateRequiredEnv();
        SpringApplication.run(WildcatsLoungeApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("  Wildcats Lounge API is running!");
        System.out.println("  Access at: http://localhost:8080");
        System.out.println("========================================\n");
    }

    /** Loads backend/.env into system properties for Spring ${...} placeholders. */
    private static void loadDotEnv() {
        for (String dir : new String[] { ".", "backend" }) {
            try {
                Dotenv dotenv = Dotenv.configure()
                        .directory(dir)
                        .ignoreIfMissing()
                        .load();
                if (!dotenv.entries().isEmpty()) {
                    dotenv.entries().forEach(entry ->
                            System.setProperty(entry.getKey(), entry.getValue()));
                    return;
                }
            } catch (Exception ignored) {
                // try next directory
            }
        }
    }

    /** Fail fast with a clear message when backend/.env is still using placeholders. */
    private static void validateRequiredEnv() {
        String dbUrl = System.getProperty("DB_URL", "");
        if (dbUrl.isBlank() || dbUrl.contains("YOUR_PROJECT_REF") || dbUrl.contains("POOLER_HOST")) {
            System.err.println("""
                    
                    ========================================
                      BACKEND CANNOT START — FIX backend/.env
                    ========================================
                    Database URL is not configured.
                    
                    Supabase direct host (db.xxx.supabase.co) is IPv6-only and often
                    fails on Windows/home networks. Use the SESSION POOLER instead:
                    
                    1. Supabase → Settings → Database
                    2. Click "Pooler settings" (near the IPv4 warning)
                    3. Copy the Session mode connection string, e.g.:
                       postgres://postgres.vgnzucuubhwfizlzwslp:[PASSWORD]@
                       aws-0-ap-southeast-1.pooler.supabase.com:5432/postgres
                    4. Set in backend/.env:
                       DB_URL=jdbc:postgresql://aws-0-REGION.pooler.supabase.com:5432/postgres
                       DB_USERNAME=postgres.vgnzucuubhwfizlzwslp
                    
                    5. Restart the backend
                    
                    """);
            System.exit(1);
        }
        if (System.getProperty("JWT_SECRET", "").isBlank()) {
            System.err.println("ERROR: JWT_SECRET is missing in backend/.env");
            System.exit(1);
        }
    }
}
