package com.outdoor.foodcalc.domain.service.plan;

import com.outdoor.foodcalc.domain.model.plan.Hiker;
import com.outdoor.foodcalc.domain.model.plan.pack.*;
import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

public class ToStringTest {

    @Test
    void getFoodPackageTest() {
        FoodPackage foodPackage = FoodPackage.builder()
                .id(1L)
                .name("Тушонка")
                .description("М'ясна консерва")
                .volumeCoefficient(0.9f)
                .additionalWeight(1200) // 120.0 г
                .build();

        System.out.println("FoodPackage → " + foodPackage);
    }

    @Test
    void getPackageDayProductsTest() {
        ProductRef p1 = createTestProductRef(1L, "Каша", 100f);
        ProductRef p2 = createTestProductRef(2L, "Чай", 20f);

        PackageDayProducts day = PackageDayProducts.builder()
                .dayId(1L)
                .date(LocalDate.of(2025, 8, 1))
                .products(List.of(p1, p2))
                .build();

        System.out.println("PackageDayProducts → " + day);
    }

    @Test
    void getPackageWithProductsTest() {
        ProductRef p1 = createTestProductRef(1L, "Каша", 100f);
        ProductRef p2 = createTestProductRef(2L, "Чай", 20f);

        PackageDayProducts d1 = PackageDayProducts.builder()
                .dayId(1L)
                .date(LocalDate.of(2025, 8, 1))
                .products(List.of(p1, p2))
                .build();

        Map<Long, PackageDayProducts> map = new HashMap<>();
        map.put(1L, d1);

        FoodPackage fp = FoodPackage.builder()
                .id(5L)
                .name("Сніданок 1")
                .volumeCoefficient(0.95f)
                .additionalWeight(100)
                .build();

        PackageWithProducts pkg = PackageWithProducts.builder()
                .foodPackage(fp)
                .dayProducts(map)
                .build();

        System.out.println("PackageWithProducts → " + pkg);
    }

    @Test
    void getHikerStateTest() {
        Hiker hiker = Hiker.builder()
                .id(1L)
                .name("Оля")
                .weightCoefficient(1.0f)
                .build();

        FoodPackage fp = FoodPackage.builder()
                .id(5L)
                .name("Сніданок 1")
                .volumeCoefficient(0.95f)
                .additionalWeight(100)
                .build();

        ProductRef p1 = createTestProductRef(1L, "Каша", 100f);
        ProductRef p2 = createTestProductRef(2L, "Чай", 20f);

        PackageDayProducts d1 = PackageDayProducts.builder()
                .dayId(1L)
                .date(LocalDate.of(2025, 8, 1))
                .products(List.of(p1, p2))
                .build();

        Map<Long, PackageDayProducts> map = Map.of(1L, d1);

        PackageWithProducts pack = PackageWithProducts.builder()
                .foodPackage(fp)
                .dayProducts(map)
                .build();

        HikerState state = new HikerState(hiker);
        state.addPackage(pack, 1);
        state.setTargetForDay(LocalDate.of(2025, 8, 1), 150.0);

        System.out.println("HikerState → " + state);
    }

    @Test
    void getHikerWithPackagesTest() {
        Hiker hiker = Hiker.builder()
                .id(1L)
                .name("Оля")
                .weightCoefficient(1.0f)
                .build();

        FoodPackage fp = FoodPackage.builder()
                .id(5L)
                .name("Вечеря 2")
                .volumeCoefficient(1.0f)
                .additionalWeight(150)
                .build();

        PackageWithProducts pack = PackageWithProducts.builder()
                .foodPackage(fp)
                .dayProducts(Collections.emptyMap())
                .build();

        HikerWithPackages hw = HikerWithPackages.builder()
                .hiker(hiker)
                .packages(Set.of(pack))
                .build();

        System.out.println("HikerWithPackages → " + hw);
    }

    private ProductRef createTestProductRef(long id, String name, float weight) {
        ProductCategory category = new ProductCategory(id, "Основні страви");
        Product product = new Product(id, name, category, 300, 15, 10, 40, 100);
        // calorific=300 kcal, proteins=15g, fats=10g, carbs=40g per 100g
        return new ProductRef(product, weight);
    }
}
