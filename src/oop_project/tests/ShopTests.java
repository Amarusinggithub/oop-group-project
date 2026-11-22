/**
 * Author: Shenecia Williams - 2501202
 * Date: 11/21/2026
 * Description: Tests T-21 to T-25 from the SRS test plan: sale quantities must be positive and in
 * stock, a payment must cover the total, and the subtotal, 15% GCT, total and change are right to
 * the cent. Stock only falls when a payment succeeds.
 */

package oop_project.tests;

import oop_project.entities.FoodItem;
import oop_project.entities.Merchandise;
import oop_project.entities.Product;
import oop_project.entities.Spectator;
import oop_project.entities.Stadium;
import oop_project.entities.StadiumShop;
import oop_project.entities.Transaction;
import oop_project.exceptions.InsufficientPaymentException;
import oop_project.exceptions.InvalidDataException;
import oop_project.exceptions.LeagueException;
import oop_project.exceptions.OutOfStockException;

import java.time.LocalDate;

public class ShopTests {

    public static void run() {
        Stadium stadium = new Stadium("ST1", "National Stadium", "Kingston", 35000, 1500);
        StadiumShop shop = stadium.getShop();
        Product jersey = new Merchandise("M001", "Harbour View Home Jersey", 4500, 10, "Jersey", "L", "Harbour View");
        Product patty = new FoodItem("F001", "Beef Patty", 350, 20, false, LocalDate.of(2026, 12, 31));
        Product ting = new FoodItem("F002", "Ting", 250, 1, true, null);
        shop.addProduct(jersey);
        shop.addProduct(patty);
        shop.addProduct(ting);
        Spectator fan = new Spectator("S01", "Tashana Reid", 29, "Female", null, null);

        // T-21 (invalid): a quantity of zero
        Transaction first = shop.startTransaction(fan);
        Check.throwsException("T-21", "Add a product to a sale with quantity 0 (invalid)", InvalidDataException.class,
                () -> first.addItem(jersey, 0));
        Check.throwsException("T-21", "Add a product to a sale with quantity -1 (invalid)", InvalidDataException.class,
                () -> first.addItem(jersey, -1));

        // T-22 (invalid): more than is in stock
        Check.throwsException("T-22", "Ask for 2 of a product with 1 in stock (invalid)", OutOfStockException.class,
                () -> first.addItem(ting, 2));
        Check.equal("T-22", "The sale has no items", 0, first.getItems().size());

        // Two sales each take the last Ting; only the one paid first gets it, and the other changes nothing
        Transaction quick = shop.startTransaction(fan);
        Check.noException("T-22", "Two open sales each add the last Ting", () -> {
            first.addItem(ting, 1);
            quick.addItem(ting, 1);
            quick.pay(500);
        });
        Check.throwsException("T-22", "Paying for the Ting after it sold out (invalid)", OutOfStockException.class, () -> first.pay(500));
        Check.equal("T-22", "Ting stock after the refused payment", 0, ting.getQuantityInStock());
        shop.cancelTransaction(first);

        // T-23 (invalid): paying less than the total changes nothing
        Transaction sale = shop.startTransaction(fan);
        try {
            sale.addItem(jersey, 2);
            sale.addItem(patty, 3);
        } catch (LeagueException e) {
            Check.equal("T-23", "Add 2 jerseys and 3 patties", "added", e.getMessage());
            return;
        }
        Check.throwsException("T-23", "Pay $11,000.00 for a total of $11,557.50 (invalid)", InsufficientPaymentException.class,
                () -> sale.pay(11_000));
        Check.equal("T-23", "Stock unchanged (jerseys, patties) and sale unpaid", "10, 20, unpaid",
                jersey.getQuantityInStock() + ", " + patty.getQuantityInStock() + (sale.isPaid() ? ", paid" : ", unpaid"));

        // T-24 (boundary): paying exactly the total
        double exactChange = -1;
        try {
            exactChange = sale.pay(11_557.50);
        } catch (LeagueException e) {
            Check.equal("T-24", "Pay exactly $11,557.50", "paid", e.getMessage());
        }
        Check.money("T-24", "Pay exactly $11,557.50 (boundary): change", 0.00, exactChange);
        Check.equal("T-24", "Sale completes", true, sale.isPaid());

        // T-25 (normal): 2 jerseys at $4,500 and 3 patties at $350, paying $12,000
        Transaction normal = shop.startTransaction(fan);
        double change = -1;
        try {
            normal.addItem(jersey, 2);
            normal.addItem(patty, 3);
            change = normal.pay(12_000);
        } catch (LeagueException e) {
            Check.equal("T-25", "Complete the sale", "paid", e.getMessage());
        }
        Check.money("T-25", "Subtotal (normal)", 10_050.00, normal.getSubtotal());
        Check.money("T-25", "GCT at 15%", 1_507.50, normal.getTax());
        Check.money("T-25", "Total", 11_557.50, normal.getTotal());
        Check.money("T-25", "Change from $12,000.00", 442.50, change);
        Check.equal("T-25", "Stock reduced (jerseys, patties) after both paid sales", "6, 14",
                jersey.getQuantityInStock() + ", " + patty.getQuantityInStock());
        Check.equal("T-25", "The spectator's purchases", 3, fan.getPurchases().size());
        // The paid sales are the Ting ($250 + GCT = $287.50) and the two $11,557.50 sales
        Check.money("T-25", "Shop sales count only paid sales", 287.50 + 2 * 11_557.50, shop.getTotalSales());
        Check.equal("T-25", "Receipt shows the change", true, normal.printReceipt().contains("442.50"));
    }
}
