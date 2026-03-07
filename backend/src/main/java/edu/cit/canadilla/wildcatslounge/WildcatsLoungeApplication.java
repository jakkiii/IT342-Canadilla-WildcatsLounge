package edu.cit.canadilla.wildcatslounge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WildcatsLoungeApplication {

    public static void main(String[] args) {
        SpringApplication.run(WildcatsLoungeApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("  Wildcats Lounge API is running!");
        System.out.println("  Access at: http://localhost:8080");
        System.out.println("========================================\n");
    }
}
