package de.birgitkratz.robobar;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RobobarController {

    private final DrinkService drinkService;

    public RobobarController(DrinkService drinkService) {
        this.drinkService = drinkService;
    }

    @GetMapping
    public String placeOrder(Model model) {
        drinkService.init();
        final var drinks = drinkService.getDrinks();
        model.addAttribute("drinks", drinks);
        model.addAttribute("totalPrice", drinkService.getTotalPrice());
        return "place-order";
    }

    @GetMapping(path = "increment/{id}")
    public String increment(@PathVariable int id,  Model model) {
        final var drinks = drinkService.increment(id);
        model.addAttribute("drinks", drinks);
        model.addAttribute("totalPrice", drinkService.getTotalPrice());
        return "place-order";
    }

    @GetMapping(path = "decrement/{id}")
    public String decrement(@PathVariable int id,  Model model) {
        final var drinks = drinkService.decrement(id);
        model.addAttribute("drinks", drinks);
        model.addAttribute("totalPrice", drinkService.getTotalPrice());
        return "place-order";
    }

    @GetMapping(path = "reviewOrder")
    public String reviewOder(Model model) {
        if (drinkService.getNumberOfDrinks() == 0) {
            return placeOrder(model);
        }
        if (drinkService.orderContainsAlcoholicDrinks()) {
            model.addAttribute("ageCheck", true);
        }
        model.addAttribute("numberOfDrinks", drinkService.getNumberOfDrinks());
        model.addAttribute("drinks", drinkService.getDrinks());

        return "review-order";
    }

    @GetMapping(path = "cancelOrder")
    public String cancelOrder(Model model) {
        return placeOrder(model);
    }

    @PostMapping(path = "submitOrder")
    public String submitOrder(Integer age, Model model) {
        if (!drinkService.isAllowedToBuyAlcohol(age)) {
            model.addAttribute("error", "Only adults can buy alcohol");
            model.addAttribute("age", age);
            return reviewOder(model);
        }
        model.addAttribute("numberOfDrinks", drinkService.getNumberOfDrinks());
        return "success-order";
    }
}
