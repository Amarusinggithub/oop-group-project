/**
 * Author: Shenecia Williams - 2501202
 * Date: 11/14/2026
 * Description: This class represents one purchase made by a customer in the stadium shop.
 * It stores the transaction ID, the customer, the products bought with their quantities,
 * the amount paid and the date and time. It can calculate the subtotal, tax and total,
 * take a payment and print a receipt.
 */

package oop_project.entities;

import oop_project.entities.interfaces.Taxable;
import oop_project.exceptions.InsufficientPaymentException;
import oop_project.exceptions.InvalidDataException;
import oop_project.exceptions.OutOfStockException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Transaction {
    private String transactionId;
    private Spectator customer;
    private Map<Product, Integer> items;
    private double amountPaid;
    private LocalDateTime dateTime;
    private boolean paid;

    // StadiumShop.startTransaction creates transactions and gives them their IDs
    public Transaction(String transactionId, Spectator customer) {
        if (transactionId == null || transactionId.isBlank()) {
            throw new IllegalArgumentException("Transaction ID is required.");
        }
        if (customer == null) {
            throw new IllegalArgumentException("A transaction must have a customer.");
        }
        this.transactionId = transactionId;
        this.customer = customer;
        this.items = new LinkedHashMap<>();
        this.amountPaid = 0;
        this.dateTime = LocalDateTime.now();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Spectator getCustomer() {
        return customer;
    }

    public Map<Product, Integer> getItems() {
        return Collections.unmodifiableMap(items);
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public boolean isPaid() {
        return paid;
    }

    // Adding a product that is already in the transaction increases its quantity
    public void addItem(Product product, int quantity) {
        requireUnpaid();
        if (product == null) {
            throw new IllegalArgumentException("Product is required.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        items.merge(product, quantity, Integer::sum);
    }

    public double getSubtotal() {
        double subtotal = 0;
        for (Map.Entry<Product, Integer> entry : items.entrySet()) {
            subtotal += entry.getKey().getBasePrice() * entry.getValue();
        }
        return roundToCents(subtotal);
    }

    public double getTax() {
        return roundToCents(getSubtotal() * Taxable.GCT_RATE);
    }

    public double getTotal() {
        return roundToCents(getSubtotal() + getTax());
    }

    // Returns the change. Stock is checked for every item before any is reduced,
    // so a failed payment changes nothing.
    public double pay(double amount) throws InvalidDataException, InsufficientPaymentException, OutOfStockException {
        requireUnpaid();
        if (items.isEmpty()) {
            throw new InvalidDataException("Add at least one item before paying.");
        }
        // SRS rule: the payment must cover the total, including GCT
        if (amount < getTotal()) {
            throw new InsufficientPaymentException(String.format(
                    "Payment of $%.2f is less than the total of $%.2f.", amount, getTotal()));
        }
        // SRS rule: a product cannot be sold beyond the quantity in stock
        for (Map.Entry<Product, Integer> entry : items.entrySet()) {
            Product product = entry.getKey();
            if (entry.getValue() > product.getQuantityInStock()) {
                throw new OutOfStockException("Only " + product.getQuantityInStock() + " of "
                        + product.getName() + " left in stock.");
            }
        }
        for (Map.Entry<Product, Integer> entry : items.entrySet()) {
            entry.getKey().reduceStock(entry.getValue());
        }
        amountPaid = roundToCents(amount);
        paid = true;
        dateTime = LocalDateTime.now();
        customer.addPurchase(this);
        return roundToCents(amountPaid - getTotal());
    }

    public String printReceipt() {
        StringBuilder receipt = new StringBuilder();
        receipt.append("Receipt ").append(transactionId).append("\n");
        receipt.append("Customer: ").append(customer.getName()).append("\n");
        receipt.append("Date: ").append(dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append("\n");
        for (Map.Entry<Product, Integer> entry : items.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            receipt.append(String.format("%-24s %3d x $%,8.2f  $%,10.2f%n", product.getName(), quantity,
                    product.getBasePrice(), product.getBasePrice() * quantity));
        }
        receipt.append(String.format("%-40s $%,10.2f%n", "Subtotal", getSubtotal()));
        receipt.append(String.format("%-40s $%,10.2f%n", "GCT (15%)", getTax()));
        receipt.append(String.format("%-40s $%,10.2f%n", "Total", getTotal()));
        if (isPaid()) {
            receipt.append(String.format("%-40s $%,10.2f%n", "Paid", amountPaid));
            receipt.append(String.format("%-40s $%,10.2f%n", "Change", amountPaid - getTotal()));
        } else {
            receipt.append("Not paid yet\n");
        }
        return receipt.toString();
    }

    private void requireUnpaid() {
        if (isPaid()) {
            throw new IllegalStateException("Transaction " + transactionId + " has already been paid.");
        }
    }

    private static double roundToCents(double amount) {
        return Math.round(amount * 100) / 100.0;
    }

    @Override
    public String toString() {
        return transactionId;
    }
}
