package de.birgitkratz.robobar;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DrinkServiceTest {

    DrinkService drinkService = new DrinkService();

    @Test
    void testIsAllowedToBuyAlcohol() {
        assertThat(drinkService.isAllowedToBuyAlcohol(24)).isTrue();
        assertThat(drinkService.isAllowedToBuyAlcohol(12)).isFalse();
        assertThat(drinkService.isAllowedToBuyAlcohol(null)).isFalse();
    }
}