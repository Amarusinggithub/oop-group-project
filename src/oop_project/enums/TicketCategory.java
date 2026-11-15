/**
 * Author: Amar Campbell
 * Date: 11/1/2026
 * Description: This enum represents the category of a match ticket. Each category
 * multiplies the stadium's base ticket price.
 */

package oop_project.enums;

public enum TicketCategory {
    GENERAL("General", 1.0),
    GRANDSTAND("Grandstand", 1.5),
    VIP("VIP", 2.5);

    private final String category;
    private final double priceMultiplier;

    TicketCategory(String category, double priceMultiplier) {
        this.category = category;
        this.priceMultiplier = priceMultiplier;
    }

    public String getCategory() {
        return category;
    }

    public double getPriceMultiplier() {
        return priceMultiplier;
    }

}
