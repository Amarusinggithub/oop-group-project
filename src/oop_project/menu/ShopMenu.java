/**
 * Author: Shenecia Williams - 2501202
 * Date: 11/21/2026
 * Description: This class is the Stadium shop menu. It adds, removes and restocks a shop's
 * products, runs a sale (items, subtotal, 15% GCT, total, payment, change and receipt), and shows
 * the inventory and sales. After each paid sale the new stock levels are written straight into
 * products.dat, the random-access product file.
 */

package oop_project.menu;

import oop_project.entities.FoodItem;
import oop_project.entities.FootballLeague;
import oop_project.entities.Merchandise;
import oop_project.entities.Product;
import oop_project.entities.Spectator;
import oop_project.entities.Stadium;
import oop_project.entities.StadiumShop;
import oop_project.entities.Transaction;
import oop_project.exceptions.InsufficientPaymentException;
import oop_project.exceptions.LeagueException;
import oop_project.persistence.LeagueFileManager;
import oop_project.persistence.ProductFile;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class ShopMenu {
    private final FootballLeague league;
    private final InputHelper input;
    private final ProductFile productFile;

    public ShopMenu(FootballLeague league, InputHelper input, Path dataFolder) {
        this.league = league;
        this.input = input;
        this.productFile = new LeagueFileManager(dataFolder).getProductFile();
    }

    public void run() {
        while (true) {
            int choice = input.menu("Stadium shop", "Back",
                    "Add product", "Remove product", "Restock product", "New sale", "Display inventory", "Shop sales report");
            if (choice == 0) {
                return;
            }
            try {
                switch (choice) {
                    case 1 -> addProduct();
                    case 2 -> removeProduct();
                    case 3 -> restockProduct();
                    case 4 -> newSale();
                    case 5 -> displayInventory();
                    case 6 -> salesReport();
                    default -> System.out.println("Please choose an option from the menu.");
                }
            } catch (LeagueException | IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private Stadium pickStadium() {
        return input.pick("Stadium shops", league.getStadiums(),
                s -> s.getName() + " (" + s.getShop().getInventory().size() + " products)");
    }

    private Product pickProduct(StadiumShop shop) {
        return input.pick("Products", shop.getInventory(), ShopMenu::productLabel);
    }

    static String productLabel(Product product) {
        return String.format("%-8s %-30s $%,9.2f incl. GCT  (%d in stock)", product.getProductCode(), product.getName(),
                product.getPriceWithTax(), product.getQuantityInStock());
    }

    private void addProduct() throws LeagueException {
        Stadium stadium = pickStadium();
        if (stadium == null) {
            return;
        }
        StadiumShop shop = stadium.getShop();
        int kind = input.menu("Kind of product", "Back", "Merchandise (jersey, flag, scarf...)", "Food or beverage");
        if (kind == 0) {
            return;
        }
        String code = input.readText("Product code (e.g. M004, up to " + ProductFile.CODE_LENGTH + " characters)", ProductFile.CODE_LENGTH);
        if (shop.findProduct(code) != null) {
            System.out.println("Error: product " + code + " is already in this shop.");
            return;
        }
        String name = input.readText("Product name", ProductFile.NAME_LENGTH);
        double price = input.readMoney("Base price before GCT", 0);
        int quantity = input.readInt("Quantity in stock", 0, 100_000);
        Product product;
        if (kind == 1) {
            String type = input.readText("Merchandise type (e.g. Jersey, Flag, Scarf)", ProductFile.TYPE_LENGTH);
            String size = input.readText("Size (e.g. S, M, L, XL or One Size)", ProductFile.SIZE_LENGTH);
            String team = input.readText("Team name (or League)", ProductFile.TEAM_LENGTH);
            product = new Merchandise(code, name, price, quantity, type, size, team);
        } else {
            boolean beverage = input.readYesNo("Is it a beverage?");
            LocalDate expiry = input.readOptionalDate("Expiry date", "none");
            product = new FoodItem(code, name, price, quantity, beverage, expiry);
        }
        shop.addProduct(product);
        System.out.println(product.getCategory() + " added to " + stadium.getName() + "'s shop: " + productLabel(product)
                + ". It is written to products.dat at the next save.");
    }

    private void removeProduct() {
        Stadium stadium = pickStadium();
        if (stadium == null) {
            return;
        }
        Product product = pickProduct(stadium.getShop());
        if (product == null) {
            return;
        }
        if (!input.confirm("Remove " + product.getName() + " from " + stadium.getName() + "'s shop?")) {
            System.out.println("Nothing was changed.");
            return;
        }
        stadium.getShop().removeProduct(product.getProductCode());
        System.out.println(product.getName() + " removed from the inventory.");
    }

    private void restockProduct() throws LeagueException {
        Stadium stadium = pickStadium();
        if (stadium == null) {
            return;
        }
        Product product = pickProduct(stadium.getShop());
        if (product == null) {
            return;
        }
        // The range allows zero or a negative entry so the product's own rule check reports it
        int quantity = input.readInt("Quantity to add", -1000, 100_000);
        product.restock(quantity);
        System.out.println(product.getName() + " restocked: " + product.getQuantityInStock() + " in stock.");
        writeStock(stadium, product);
    }

    private void newSale() throws LeagueException {
        Stadium stadium = pickStadium();
        if (stadium == null) {
            return;
        }
        StadiumShop shop = stadium.getShop();
        if (shop.getInventory().isEmpty()) {
            System.out.println(stadium.getName() + "'s shop has no products yet.");
            return;
        }
        Spectator customer = TicketMenu.chooseSpectator(league, input);
        if (customer == null) {
            return;
        }
        Transaction sale = shop.startTransaction(customer);
        try {
            if (addItems(shop, sale) && takePayment(stadium, sale)) {
                return;
            }
            shop.cancelTransaction(sale);
            System.out.println("Sale cancelled. No stock was taken.");
        } catch (LeagueException | RuntimeException e) {
            // A sale that fails part-way is dropped, so it never counts towards sales
            if (!sale.isPaid()) {
                shop.cancelTransaction(sale);
            }
            throw e;
        }
    }

    // Returns false when the sale is abandoned or has no items
    private boolean addItems(StadiumShop shop, Transaction sale) {
        while (true) {
            System.out.println();
            System.out.printf("Sale %s for %s: %d item line(s), subtotal $%,.2f%n", sale.getTransactionId(),
                    sale.getCustomer().getName(), sale.getItems().size(), sale.getSubtotal());
            System.out.println("Pick a product to add, or 0 when the order is complete.");
            Product product = pickProduct(shop);
            if (product == null) {
                if (sale.getItems().isEmpty()) {
                    return false;
                }
                return true;
            }
            // The range allows zero or a negative entry so the sale's own rule check reports it
            int quantity = input.readInt("Quantity", -100, 1000);
            try {
                sale.addItem(product, quantity);
                System.out.println("Added " + quantity + " x " + product.getName() + ".");
            } catch (LeagueException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // Returns true when the sale is paid; false when the customer gives up
    private boolean takePayment(Stadium stadium, Transaction sale) throws LeagueException {
        System.out.printf("%nSubtotal $%,.2f%nGCT (15%%) $%,.2f%nTotal $%,.2f%n", sale.getSubtotal(), sale.getTax(), sale.getTotal());
        while (true) {
            double amount = input.readMoney("Amount paid", 0);
            try {
                double change = sale.pay(amount);
                System.out.printf("Payment accepted. Change: $%,.2f%n%n", change);
                System.out.print(sale.printReceipt());
                for (Product product : sale.getItems().keySet()) {
                    writeStock(stadium, product);
                }
                return true;
            } catch (InsufficientPaymentException e) {
                System.out.println("Error: " + e.getMessage());
                if (!input.readYesNo("Enter another amount?")) {
                    return false;
                }
            }
        }
    }

    // Updates the product's stock in place in products.dat (random access)
    private void writeStock(Stadium stadium, Product product) {
        try {
            if (productFile.updateStock(stadium.getStadiumId(), product)) {
                System.out.println("products.dat updated: " + product.getName() + " now " + product.getQuantityInStock() + " in stock.");
            } else {
                System.out.println("Note: " + product.getName() + " is not in products.dat yet; it will be written at the next save.");
            }
        } catch (IOException e) {
            System.out.println("Could not update products.dat (" + e.getMessage() + "); the stock will be written at the next save.");
        }
    }

    private void displayInventory() {
        Stadium stadium = pickStadium();
        if (stadium == null) {
            return;
        }
        StadiumShop shop = stadium.getShop();
        if (shop.getInventory().isEmpty()) {
            System.out.println(stadium.getName() + "'s shop has no products yet.");
            return;
        }
        System.out.println(stadium.getName() + " shop (" + shop.getShopId() + ")");
        System.out.printf("%-8s %-30s %-11s %11s %13s %7s %s%n", "Code", "Name", "Category", "Base price", "Price + GCT", "Stock", "Details");
        for (Product product : shop.getInventory()) {
            System.out.printf("%-8s %-30s %-11s $%,10.2f $%,12.2f %7d %s%n", product.getProductCode(), product.getName(),
                    product.getCategory(), product.getBasePrice(), product.getPriceWithTax(), product.getQuantityInStock(), details(product));
        }
    }

    // Polymorphism: each kind of product has its own details
    private static String details(Product product) {
        if (product instanceof Merchandise merchandise) {
            return merchandise.getType() + ", " + merchandise.getSize() + ", " + merchandise.getTeamName();
        }
        if (product instanceof FoodItem food) {
            String expiry = food.getExpiryDate() == null ? "no expiry" : "expires " + food.getExpiryDate();
            return expiry + (food.isExpired() ? " (EXPIRED)" : "");
        }
        return "";
    }

    private void salesReport() {
        Stadium stadium = pickStadium();
        if (stadium == null) {
            return;
        }
        System.out.print(stadium.getShop().salesReport());
        Map<Product, Integer> sold = new LinkedHashMap<>();
        for (Transaction transaction : stadium.getShop().getTransactions()) {
            if (transaction.isPaid()) {
                for (Map.Entry<Product, Integer> item : transaction.getItems().entrySet()) {
                    sold.merge(item.getKey(), item.getValue(), Integer::sum);
                }
            }
        }
        if (!sold.isEmpty()) {
            System.out.println("Units sold by product:");
            for (Map.Entry<Product, Integer> item : sold.entrySet()) {
                System.out.printf("  %-30s %5d%n", item.getKey().getName(), item.getValue());
            }
        }
    }
}
