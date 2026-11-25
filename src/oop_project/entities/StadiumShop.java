/**
 * Author: Shenecia Williams - 2501202
 * Date: 11/14/2026
 * Description: This class represents a stadium's shop. It manages the product inventory,
 * starts sales transactions and reports on sales.
 */

package oop_project.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StadiumShop {
    private String shopId;
    private List<Product> inventory;
    private List<Transaction> transactions;

    public StadiumShop(String shopId) {
        if (shopId == null || shopId.trim().isEmpty()) {
            throw new IllegalArgumentException("Shop ID is required.");
        }
        this.shopId = shopId;
        this.inventory = new ArrayList<>();
        this.transactions = new ArrayList<>();
    }

    public String getShopId() {
        return shopId;
    }

    public List<Product> getInventory() {
        return Collections.unmodifiableList(inventory);
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public void addProduct(Product product) {
        if (findProduct(product.getProductCode()) != null) {
            throw new IllegalArgumentException("A product with code " + product.getProductCode() + " is already in the inventory.");
        }
        inventory.add(product);
    }

    public void removeProduct(String productCode) {
        Product product = findProduct(productCode);
        if (product == null) {
            throw new IllegalArgumentException("No product with code " + productCode + " is in the inventory.");
        }
        inventory.remove(product);
    }

    // Returns null when no product has that code
    public Product findProduct(String productCode) {
        for (Product product : inventory) {
            if (product.getProductCode().equalsIgnoreCase(productCode)) {
                return product;
            }
        }
        return null;
    }

    public Transaction startTransaction(Spectator customer) {
        // Find the next number that is not used yet, so an ID never repeats
        int number = transactions.size() + 1;
        String transactionId = shopId + "-TX" + String.format("%04d", number);
        while (findTransaction(transactionId) != null) {
            number++;
            transactionId = shopId + "-TX" + String.format("%04d", number);
        }
        Transaction transaction = new Transaction(transactionId, customer);
        transactions.add(transaction);
        return transaction;
    }

    // Drops a sale that was started but never paid, for example when the customer walks away
    public void cancelTransaction(Transaction transaction) {
        if (transaction.isPaid()) {
            throw new IllegalStateException("Transaction " + transaction.getTransactionId() + " has been paid and cannot be cancelled.");
        }
        transactions.remove(transaction);
    }

    // Rebuilds a paid sale from saved data without changing stock.
    // Used by LeagueFileManager when loading; menus use startTransaction instead.
    public Transaction restoreTransaction(String transactionId, Spectator customer, Map<Product, Integer> items,
                                          double amountPaid, LocalDateTime dateTime) {
        if (findTransaction(transactionId) != null) {
            throw new IllegalArgumentException("Transaction " + transactionId + " is already recorded.");
        }
        Transaction transaction = new Transaction(transactionId, customer);
        transaction.restorePaid(items, amountPaid, dateTime);
        transactions.add(transaction);
        return transaction;
    }

    // Returns null when no transaction has that ID
    public Transaction findTransaction(String transactionId) {
        for (Transaction transaction : transactions) {
            if (transaction.getTransactionId().equalsIgnoreCase(transactionId)) {
                return transaction;
            }
        }
        return null;
    }

    // Only paid transactions count as sales
    public double getTotalSales() {
        double total = 0;
        for (Transaction transaction : transactions) {
            if (transaction.isPaid()) {
                total += transaction.getTotal();
            }
        }
        return total;
    }

    public String salesReport() {
        StringBuilder report = new StringBuilder();
        report.append("Sales report for shop ").append(shopId).append("\n");
        int paidCount = 0;
        for (Transaction transaction : transactions) {
            if (transaction.isPaid()) {
                paidCount++;
                report.append(String.format("%-20s %-24s $%,10.2f%n", transaction.getTransactionId(),
                        transaction.getCustomer().getName(), transaction.getTotal()));
            }
        }
        report.append(String.format("%d paid transaction(s), total sales $%,.2f (incl. GCT)%n", paidCount, getTotalSales()));
        return report.toString();
    }
}
