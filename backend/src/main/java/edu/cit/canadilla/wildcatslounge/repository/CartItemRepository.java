package edu.cit.canadilla.wildcatslounge.repository;

import edu.cit.canadilla.wildcatslounge.entity.CartItem;
import edu.cit.canadilla.wildcatslounge.entity.ServingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCartIdOrderByIdAsc(Long cartId);

    Optional<CartItem> findByCartIdAndMenuItemIdAndServingType(Long cartId, Long menuItemId, ServingType servingType);

    Optional<CartItem> findByIdAndCartId(Long id, Long cartId);

    void deleteByCartId(Long cartId);
}
