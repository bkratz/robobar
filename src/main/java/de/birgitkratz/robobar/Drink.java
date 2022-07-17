package de.birgitkratz.robobar;


import java.math.BigDecimal;

record Drink(int id, String name, BigDecimal price, boolean alcoholic, int amount) {
}
