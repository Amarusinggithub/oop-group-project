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
import oop_project.exceptions.LeagueException;
import oop_project.persistence.LeagueFileManager;
import oop_project.persistence.ProductFile;

import java.io.IOException;
import java.time.LocalDate;

public class ShopMenu {
    private FootballLeague league;
    private InputHelper input;
    private ProductFile productFile;

    public ShopMenu(FootballLeague league, InputHelper input, String dataFolder) {
        this.league = league;
        this.input = input;
        this.productFile = new LeagueFileManager(dataFolder).getProductFile();
    }

    public void run() {
        while (true) {
            String[] options = {"Add product", "Remove product", "Restock product", "New sale", "Display inventory", "Shop sales report"};
            int choice = input.menu("Stadium shop", "Back", options);
            if (choice == 0) {
                return;
            }
            try {
                switch (choice) {
                    case 1:
                        addProduct();
                        break;
                    case 2:
                        removeProduct();
                        break;
                    case 3:
                        restockProduct();
                        break;
                    case 4:
                        newSale();
                        break;
                    case 5:
                        displayInventory();
                        break;
                    case 6:
                        salesReport();
                        break;
                    default:
                        System.out.println("Please choose an option from the menu.");
                        break;
                }
            } catch (LeagueException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void addProduct() throws LeagueException {
        Stadium stadium = input.pickStadium(league.getStadiums());
        if (stadium == null) {
            return;
        }
        StadiumShop shop = stadium.getShop();
        String[] options = {"Merchandise (jersey, flag, scarf...)", "Food or beverage"};
        int kind = input.menu("Kind of product", "Back", options);
        if (kind == 0) {
            return;
        }
        String code = input.readText("Product code (e.g. M004, up to " + ProductFile.CODE_LENGTH + " characters)", ProductFile.CODE_LENGTH);
        if (shop.findProduct(code) != null) {
            System.out.println("Error: product " + code + " is already in this shop.");
            return;
        }
        String name = input.readText("Product name", ProductFile.NAME_LENGTH);
        double price = input.readMoney("Base price before GCT");
        int quantity = input.readInt("Quantity in stock", 0, 100000);
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
        System.out.println(product.getCategory() + " added to " + stadium.getName() + "'s shop: " + InputHelper.productLabel(product)
                + ". It is written to products.dat at the next save.");
    }

    private void removeProduct() {
        Stadium stadium = input.pickStadium(league.getStadiums());
        if (stadium == null) {
            return;
        }
        Product product = input.pickProduct(stadium.getShop().getInventory());
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
        Stadium stadium = input.pickStadium(league.getStadiums());
        if (stadium == null) {
            return;
        }
        Product product = input.pickProduct(stadium.getShop().getInventory());
        if (product == null) {
            return;
        }
        // The range allows zero or a negative entry so the product's own rule check reports it
        int quantity = input.readInt("Quantity to add", -1000, 100000);
        product.restock(quantity);
        System.out.println(product.getName() + " restocked: " + product.getQuantityInStock() + " in stock.");
        writeStock(stadium, product);
    }

    private void newSale() throws LeagueException {
        Stadium stadium = input.pickStadium(league.getStadiums());
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
        addItems(shop, sale);
        if (!sale.getItems().isEmpty()) {
            takePayment(stadium, sale);
        }
        // A sale that was not paid is dropped, so it never counts towards sales
        if (!sale.isPaid()) {
            shop.cancelTransaction(sale);
            System.out.println("Sale cancelled. No stock was taken.");
        }
    }

    // Adds products until the user picks 0
    private void addItems(StadiumShop shop, Transaction sale) {
        while (true) {
            System.out.println();
            System.out.printf("Sale %s for %s: %d item line(s), subtotal $%,.2f%n", sale.getTransactionId(),
                    sale.getCustomer().getName(), sale.getItems().size(), sale.getSubtotal());
            System.out.println("Pick a product to add, or 0 when the order is complete.");
            Product product = input.pickProduct(shop.getInventory());
            if (product == null) {
                return;
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

    // Asks for the payment until the sale is paid or the customer gives up
    private void takePayment(Stadium stadium, Transaction sale) {
        System.out.printf("%nSubtotal $%,.2f%nGCT (15%%) $%,.2f%nTotal $%,.2f%n", sale.getSubtotal(), sale.getTax(), sale.getTotal());
        while (true) {
            double amount = input.readMoney("Amount paid");
            try {
                double change = sale.pay(amount);
                System.out.printf("Payment accepted. Change: $%,.2f%n%n", change);
                System.out.print(sale.printReceipt());
                for (Product product : sale.getItems().keySet()) {
                    writeStock(stadium, product);
                }
                return;
            } catch (LeagueException e) {
                System.out.println("Error: " + e.getMessage());
                if (!input.readYesNo("Enter another amount?")) {
                    return;
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
        Stadium stadium = input.pickStadium(league.getStadiums());
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
            System.out.printf("%-8s %-30s %-11s $%,10.2f %13s %7d %s%n", product.getProductCode(), product.getName(),
                    product.getCategory(), product.getBasePrice(), InputHelper.priceWithTax(product), product.getQuantityInStock(), details(product));
        }
    }

    // Merchandise and food items have different details
    private static String details(Product product) {
        if (product instanceof Merchandise) {
            Merchandise merchandise = (Merchandise) product;
            return merchandise.getType() + ", " + merchandise.getSize() + ", " + merchandise.getTeamName();
        }
        if (product instanceof FoodItem) {
            FoodItem food = (FoodItem) product;
            if (food.getExpiryDate() == null) {
                return "no expiry";
            }
            if (food.isExpired()) {
                return "expires " + food.getExpiryDate() + " (EXPIRED)";
            }
            return "expires " + food.getExpiryDate();
        }
        return "";
    }

    private void salesReport() {
        Stadium stadium = input.pickStadium(league.getStadiums());
        if (stadium == null) {
            return;
        }
        System.out.print(stadium.getShop().salesReport());
        System.out.println("Units sold by product:");
        for (Product product : stadium.getShop().getInventory()) {
            int sold = 0;
            for (Transaction transaction : stadium.getShop().getTransactions()) {
                if (transaction.isPaid() && transaction.getItems().containsKey(product)) {
                    sold = sold + transaction.getItems().get(product);
                }
            }
            if (sold > 0) {
                System.out.printf("  %-30s %5d%n", product.getName(), sold);
            }
        }
    }
}
