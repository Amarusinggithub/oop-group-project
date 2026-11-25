/**
 * Author: Shenecia Williams - 2501202
 * Date: 11/14/2026
 * Description: This class represents a food or beverage item sold by the stadium shop.
 * It extends the Product class.
 */

package oop_project.entities;

import java.time.LocalDate;

public class FoodItem extends Product {
    private boolean beverage;
    private LocalDate expiryDate;

    public FoodItem(String productCode, String name, double basePrice, int quantityInStock, boolean beverage, LocalDate expiryDate) {
        super(productCode, name, basePrice, quantityInStock);
        this.beverage = beverage;
        this.expiryDate = expiryDate;
    }

    public boolean isBeverage() {
        return beverage;
    }

    public void setBeverage(boolean beverage) {
        this.beverage = beverage;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }

    @Override
    public String getCategory() {
        if (beverage) {
            return "Beverage";
        }
        return "Food";
    }
}
