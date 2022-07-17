package de.birgitkratz.robobar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

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

        final var drinks = (List<Drink>) result.getModelAndView().getModel().get("drinks");
        assertThat(drinks.get(0).amount()).isEqualTo(0);
        assertThat(drinks.get(1).amount()).isEqualTo(1);
        assertThat(drinks.get(2).amount()).isEqualTo(0);
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

        final var drinks = (List<Drink>) result.getModelAndView().getModel().get("drinks");
        assertThat(drinks.get(0).amount()).isEqualTo(1);
        assertThat(drinks.get(1).amount()).isEqualTo(1);
        assertThat(drinks.get(2).amount()).isEqualTo(1);
    }

    @Test
    @DisplayName("should not go below 0 on decrement")
    void shouldNotGotBelowZeroOnDecrement() throws Exception {
        final var result = mockMvc.perform(get("/decrement/1"))
                .andExpect(status().isOk())
                .andReturn();

        final var drinks = (List<Drink>) result.getModelAndView().getModel().get("drinks");
        assertThat(drinks.get(0).amount()).isEqualTo(0);
    }

    @Test
    @DisplayName("should return to placeOrder page when no drinks in order")
    void shouldReturnToPlaceOrderPageWhenNoDrinksInOrder() throws Exception {
        final var result = mockMvc.perform(get("/reviewOrder"))
                .andExpect(status().isOk())
                .andReturn();

        final var model = result.getModelAndView().getModel();
        final var drinks = (List<Drink>) model.get("drinks");
        assertThat(drinks.get(0).amount()).isEqualTo(0);
        assertThat(drinks.get(1).amount()).isEqualTo(0);
        assertThat(drinks.get(2).amount()).isEqualTo(0);

        assertThat(model.get("totalPrice")).isEqualTo(new BigDecimal("0.00"));
    }

    @Test
    @DisplayName("should allow order, when only non-alcoholic drinks")
    void shouldAllowOrderWhenOnlyNonAlcoholicDrinks() throws Exception {
        drinkService.increment(1);
        drinkService.increment(1);

        final var result = mockMvc.perform(post("/submitOrder"))
                .andExpect(status().isOk())
                .andReturn();

        final var numberOfDrinks = (int)result.getModelAndView().getModel().get("numberOfDrinks");
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

        final var ageCheck = (boolean) result.getModelAndView().getModel().get("ageCheck");
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

        final var numberOfDrinks = (int)result.getModelAndView().getModel().get("numberOfDrinks");
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

        final var model = result.getModelAndView().getModel();
        final var drinks = (List<Drink>) model.get("drinks");
        assertThat(drinks.get(0).amount()).isEqualTo(0);
        assertThat(drinks.get(1).amount()).isEqualTo(0);
        assertThat(drinks.get(2).amount()).isEqualTo(0);

        assertThat(model.get("totalPrice")).isEqualTo(new BigDecimal("0.00"));
    }

}