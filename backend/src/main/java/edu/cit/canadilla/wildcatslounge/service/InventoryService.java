package edu.cit.canadilla.wildcatslounge.service;

import edu.cit.canadilla.wildcatslounge.dto.IngredientResponse;
import edu.cit.canadilla.wildcatslounge.entity.Ingredient;
import edu.cit.canadilla.wildcatslounge.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final IngredientRepository ingredientRepository;

    @Transactional(readOnly = true)
    public List<IngredientResponse> getAllIngredients() {
        return ingredientRepository.findAllByOrderByNameAsc().stream()
                .map(IngredientResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public IngredientResponse restock(Long id, BigDecimal amount) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Restock amount must be greater than 0");
        }
        BigDecimal current = ingredient.getQuantityOnHand() != null
                ? ingredient.getQuantityOnHand()
                : BigDecimal.ZERO;
        ingredient.setQuantityOnHand(current.add(amount));
        return IngredientResponse.from(ingredientRepository.save(ingredient));
    }
}
