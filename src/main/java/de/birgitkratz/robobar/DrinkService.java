package de.birgitkratz.robobar;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class DrinkService {

    private Drink[] drinks;

    @PostConstruct
    void init() {
        drinks = new Drink[] {
                new Drink(1, "Roba Cola", new BigDecimal("1.25"), false, 0),
                new Drink(2, "Robo Beer", new BigDecimal("2.00"), true, 0),
                new Drink(3, "Rob(w)ine", new BigDecimal("3.00"), true, 0)
        };
    }

    List<Drink> getDrinks() {
        return Arrays.asList(drinks);
    }

    List<Drink> increment(int id) {
        IntStream.range(0, drinks.length).forEach(idx ->{
            if (drinks[idx].id() == id) {
                final var drink = drinks[idx];
                drinks[idx]= new Drink(drink.id(), drink.name(), drink.price(), drink.alcoholic(), drink.amount()+1);
            }
        });
        return Arrays.asList(drinks);
    }

    List<Drink> decrement(int id) {
        IntStream.range(0, drinks.length).forEach(idx ->{
            if (drinks[idx].id() == id) {
                final var drink = drinks[idx];
                if (drink.amount() > 0) {
                    drinks[idx]= new Drink(drink.id(), drink.name(), drink.price(), drink.alcoholic(), drink.amount()-1);
                }
            }
        });
        return Arrays.asList(drinks);
    }

    BigDecimal getTotalPrice() {
        return Arrays.stream(drinks)
                .map(drink -> drink.price().multiply(BigDecimal.valueOf(drink.amount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    int getNumberOfDrinks() {
        return Arrays.stream(drinks)
                .mapToInt(Drink::amount)
                .sum();
    }

    boolean orderContainsAlcoholicDrinks() {
        return Arrays.stream(drinks)
                .anyMatch(drink -> drink.amount() > 0 && drink.alcoholic());
    }

    boolean isAllowedToBuyAlcohol(Integer age) {
        return age != null && age > 18;
    }
}
