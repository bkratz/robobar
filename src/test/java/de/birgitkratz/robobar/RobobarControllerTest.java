package de.birgitkratz.robobar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RobobarControllerTest {

    @Autowired
    RobobarController robobarController;

    @Autowired
    DrinkService drinkService;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void init() {
        drinkService.init();
    }

    @Test
    @DisplayName("should display three drink choices")
    void shouldDisplayThreeDrinkChoices() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("drinks", hasSize(3)));
    }

    @Test
    @DisplayName("should increment the drink amount on increment")
    void shouldIncrementTheDrinkAmountOnIncrement() throws Exception {
        final var result = mockMvc.perform(get("/increment/2"))
                .andExpect(status().isOk())
                .andReturn();

        final var drinks = Optional.ofNullable(result.getModelAndView())
                .map(ModelAndView::getModelMap)
                .map(mm -> (List<Drink>)mm.get("drinks"))
                .orElse(null);

        assertThat(drinks)
                .hasSize(3)
                .extracting("amount")
                .containsExactly(0, 1, 0);
    }

    @Test
    @DisplayName("should decrement the drink amount on decrement")
    void shouldDecrementTheDrinkAmountOnDecrement() throws Exception {
        drinkService.increment(1);
        drinkService.increment(1);
        drinkService.increment(2);
        drinkService.increment(3);

        final var result = mockMvc.perform(get("/decrement/1"))
                .andExpect(status().isOk())
                .andReturn();

        final var drinks = Optional.ofNullable(result.getModelAndView())
                .map(ModelAndView::getModelMap)
                .map(mm -> (List<Drink>)mm.get("drinks"))
                .orElse(null);

        assertThat(drinks)
                .isNotEmpty()
                .extracting(Drink::amount)
                .allMatch(amount -> amount == 1);
    }

    @Test
    @DisplayName("should not go below 0 on decrement")
    void shouldNotGotBelowZeroOnDecrement() throws Exception {
        final var result = mockMvc.perform(get("/decrement/1"))
                .andExpect(status().isOk())
                .andReturn();

        final var drinks = Optional.ofNullable(result.getModelAndView())
                .map(ModelAndView::getModelMap)
                .map(mm -> (List<Drink>)mm.get("drinks"))
                .orElse(null);

        assertThat(drinks)
                .filteredOn(drink -> drink.id() == 1)
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("amount", 0);
    }

    @Test
    @DisplayName("should return to placeOrder page when no drinks in order")
    void shouldReturnToPlaceOrderPageWhenNoDrinksInOrder() throws Exception {
        final var result = mockMvc.perform(get("/reviewOrder"))
                .andExpect(status().isOk())
                .andReturn();

        final var modelMap = Optional.ofNullable(result.getModelAndView())
                .map(ModelAndView::getModelMap);
        final var drinks = modelMap
                .map(mm -> (List<Drink>)mm.get("drinks"))
                .orElse(null);
        final var totalPrice = modelMap
                .map(mm -> (BigDecimal)mm.get("totalPrice"))
                .orElse(null);

        assertThat(drinks)
                .isNotEmpty()
                .extracting(Drink::amount)
                .allMatch(amount -> amount == 0);
        assertThat(totalPrice).isEqualTo("0.00");
    }

    @Test
    @DisplayName("should allow order, when only non-alcoholic drinks")
    void shouldAllowOrderWhenOnlyNonAlcoholicDrinks() throws Exception {
        drinkService.increment(1);
        drinkService.increment(1);

        final var result = mockMvc.perform(post("/submitOrder"))
                .andExpect(status().isOk())
                .andReturn();

        final var numberOfDrinks = Optional.ofNullable(result.getModelAndView())
                .map(ModelAndView::getModelMap)
                .map(modelMap -> (Integer)modelMap.get("numberOfDrinks"))
                .orElse(null);

        assertThat(numberOfDrinks).isEqualTo(2);
    }

    @Test
    @DisplayName("should show age check, when alcoholic drinks")
    void shouldShowAgeCheckWhenAlcoholicDrinks() throws Exception {
        drinkService.increment(1);
        drinkService.increment(1);
        drinkService.increment(2);

        final var result = mockMvc.perform(get("/reviewOrder"))
                .andExpect(status().isOk())
                .andReturn();

        final var ageCheck = Optional.ofNullable(result.getModelAndView())
                .map(ModelAndView::getModelMap)
                .map(modelMap -> (Boolean)modelMap.get("ageCheck"))
                .orElse(null);

        assertThat(ageCheck).isTrue();
    }

    @Test
    @DisplayName("should allow, when age 24")
    void shouldAllowOrderWhenAge24() throws Exception {
        drinkService.increment(1);
        drinkService.increment(1);
        drinkService.increment(2);

        final var result = mockMvc.perform(post("/submitOrder").param("age", "24"))
                .andExpect(status().isOk())
                .andReturn();

        final var numberOfDrinks = Optional.ofNullable(result.getModelAndView())
                .map(ModelAndView::getModelMap)
                .map(modelMap -> (Integer)modelMap.get("numberOfDrinks"))
                .orElse(null);

        assertThat(numberOfDrinks).isEqualTo(3);
    }

    @Test
    @DisplayName("should return to placeOrder on cancel order")
    void shouldReturnToPlaceOrderPageOnCancelOrder() throws Exception {
        drinkService.increment(1);
        drinkService.increment(1);
        drinkService.increment(2);

        final var result = mockMvc.perform(get("/cancelOrder"))
                .andExpect(status().isOk())
                .andReturn();

        final var modelMap = Optional.ofNullable(result.getModelAndView())
                .map(ModelAndView::getModelMap);
        final var drinks = modelMap
                .map(mm -> (List<Drink>)mm.get("drinks"))
                .orElse(null);
        final var totalPrice = modelMap
                .map(mm -> (BigDecimal)mm.get("totalPrice"))
                .orElse(null);

        assertThat(drinks)
                .isNotEmpty()
                .extracting(Drink::amount)
                .allMatch(amount -> amount == 0);
        assertThat(totalPrice).isEqualTo("0.00");
    }

}