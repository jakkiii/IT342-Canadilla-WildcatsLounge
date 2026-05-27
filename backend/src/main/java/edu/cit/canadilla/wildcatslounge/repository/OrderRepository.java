package edu.cit.canadilla.wildcatslounge.repository;

import edu.cit.canadilla.wildcatslounge.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.user JOIN FETCH o.items WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    List<Order> findByUserWithDetails(@Param("userId") Long userId);

    @Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.user JOIN FETCH o.items WHERE LOWER(o.status) IN :statuses ORDER BY o.createdAt ASC")
    List<Order> findActiveWithDetails(@Param("statuses") List<String> statuses);

    @Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.items ORDER BY o.createdAt ASC")
    List<Order> findAllWithItems();

    @Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.user JOIN FETCH o.items WHERE o.id = :id")
    Optional<Order> findByIdWithDetails(@Param("id") Long id);

    Optional<Order> findByOrderNumber(String orderNumber);
    @Query("SELECT COUNT(o) FROM Order o WHERE LOWER(o.status) IN :statuses")
    long countByStatusInIgnoreCase(@Param("statuses") List<String> statuses);
}
