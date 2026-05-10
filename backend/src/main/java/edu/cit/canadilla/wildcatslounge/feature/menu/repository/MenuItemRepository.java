package edu.cit.canadilla.wildcatslounge.feature.menu.repository;

import edu.cit.canadilla.wildcatslounge.feature.menu.entity.MenuCategory;
import edu.cit.canadilla.wildcatslounge.feature.menu.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByIsAvailableTrueOrderByNameAsc();

    List<MenuItem> findByCategoryAndIsAvailableTrueOrderByNameAsc(MenuCategory category);

    Optional<MenuItem> findByNameIgnoreCase(String name);
}
