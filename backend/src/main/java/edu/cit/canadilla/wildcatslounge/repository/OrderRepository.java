package edu.cit.canadilla.wildcatslounge.repository;

import edu.cit.canadilla.wildcatslounge.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsByOrderNumber(String orderNumber);
}
