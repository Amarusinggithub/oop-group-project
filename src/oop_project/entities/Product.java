/**
 * Author: Shenecia Williams - 2501202
 * Date: 11/14/2026
 * Description: This abstract class represents an item sold by the stadium shop. Its price
 * includes 15% GCT. Subclasses such as Merchandise and FoodItem say which category they belong to.
 */

package oop_project.entities;

import oop_project.entities.interfaces.Taxable;
import oop_project.exceptions.InvalidDataException;
import oop_project.exceptions.OutOfStockException;

public abstract class Product implements Taxable {
    protected String productCode;
    protected String name;
    protected double basePrice;
    protected int quantityInStock;

    public Product(String productCode, String name, double basePrice, int quantityInStock) {
        if (productCode == null || productCode.isBlank()) {
            throw new IllegalArgumentException("Product code is required.");
        }
        if (quantityInStock < 0) {
            throw new IllegalArgumentException("Quantity in stock cannot be negative.");
        }
        this.productCode = productCode;
        this.quantityInStock = quantityInStock;
        setName(name);
        setBasePrice(basePrice);
    }

    public String getProductCode() {
        return productCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name is required.");
        }
        this.name = name;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        if (basePrice < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        this.basePrice = basePrice;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    @Override
    public double getPriceWithTax() {
        return basePrice * (1 + GCT_RATE);
    }

    // Called when a shop transaction is paid
    public void reduceStock(int quantity) throws OutOfStockException {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        if (quantity > quantityInStock) {
            throw new OutOfStockException("Only " + quantityInStock + " of " + name + " left in stock.");
        }
        quantityInStock -= quantity;
    }

    public void restock(int quantity) throws InvalidDataException {
        // SRS rule: quantities cannot be negative
        if (quantity <= 0) {
            throw new InvalidDataException("Restock quantity must be greater than zero.");
        }
        quantityInStock += quantity;
    }

    public abstract String getCategory();

    @Override
    public String toString() {
        return name;
    }
}
