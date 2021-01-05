package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.FoodcalcException;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.model.dish.SimpleDishCategory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DishCategoryService {

    private final List<SimpleDishCategory> categories = new ArrayList<>();

    public List<SimpleDishCategory> getDishCategories() {
        return categories;
    }


    public SimpleDishCategory getDishCategory(long id) {
        final Optional<SimpleDishCategory> first = categories.stream()
                .filter(c -> c.id == id)
                .findFirst();
        return first.orElseThrow(() -> new NotFoundException("Dish Category wasn't found"));
    }

    public SimpleDishCategory addDishCategory(SimpleDishCategory category) {
        category.id = categories.stream()
                .map(c -> c.id)
                .max(Long::compareTo)
                .orElse((long) categories.size())
                + 1;
        categories.add(category);
        return category;
    }

    public boolean updateDishCategory(SimpleDishCategory category) {
        final Optional<SimpleDishCategory> first = categories.stream()
                .filter(c -> c.id == category.id)
                .findFirst();
        if (!first.isPresent()) {
            return false;
        }
        SimpleDishCategory original = first.get();
        original.name = category.name;
        return true;
    }

//    public void deleteMealType(long id) {
//        int index = 0;
//        while (index < categories.size()) {
//            if (categories.get(index).id == id) {
//                categories.remove(index);
//                return;
//            }
//            index++;
//        }
//        throw new FoodcalcException("Dish Category wasn't found");
//    }

    public boolean deleteDishCategory(long id) {
        final Optional<SimpleDishCategory> first = categories.stream().
                filter(c -> c.id == id).
                findFirst();
        return first.map(categories::remove)
                .orElse(false);
    }

}
