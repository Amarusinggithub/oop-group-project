/**
 * Author: Shenecia Williams - 2501202
 * Date: 11/21/2026
 * Description: This class is the Ticket sales menu. It sells tickets for scheduled fixtures in
 * the General, Grandstand or VIP category, with 15% GCT, shows the seats remaining, and lists the
 * tickets sold. The fixture refuses a sale once the stadium is full.
 */

package oop_project.menu;

import oop_project.entities.Fixture;
import oop_project.entities.FootballLeague;
import oop_project.entities.Spectator;
import oop_project.entities.Tickets;
import oop_project.entities.interfaces.Taxable;
import oop_project.enums.TicketCategory;
import oop_project.exceptions.LeagueException;

public class TicketMenu {
    private final FootballLeague league;
    private final InputHelper input;

    public TicketMenu(FootballLeague league, InputHelper input) {
        this.league = league;
        this.input = input;
    }

    public void run() {
        while (true) {
            int choice = input.menu("Ticket sales", "Back", "Sell ticket", "Show seats remaining", "Display tickets sold for a fixture");
            if (choice == 0) {
                return;
            }
            try {
                switch (choice) {
                    case 1 -> sellTicket();
                    case 2 -> showSeatsRemaining();
                    case 3 -> displayTickets();
                    default -> System.out.println("Please choose an option from the menu.");
                }
            } catch (LeagueException | IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // Finds a spectator by ID, or records a new one. Returns null if the user goes back.
    static Spectator chooseSpectator(FootballLeague league, InputHelper input) throws LeagueException {
        String id = input.readText("Spectator ID (e.g. S017; 0 to go back)", 10);
        if (id.equals("0")) {
            return null;
        }
        Spectator spectator = league.findSpectator(id);
        if (spectator != null) {
            System.out.println("Found spectator: " + spectator.getName() + " (" + spectator.getTickets().size() + " ticket(s), "
                    + spectator.getPurchases().size() + " purchase(s))");
            return spectator;
        }
        System.out.println("New spectator " + id + ":");
        String name = input.readText("Name");
        int age = input.readInt("Age", 0, 120);
        String gender = input.readText("Gender (e.g. Male or Female)");
        spectator = new Spectator(id, name, age, gender, null, null);
        league.registerSpectator(spectator);
        return spectator;
    }

    // The price of each category at this fixture's stadium, including GCT
    private static void printPrices(Fixture fixture) {
        System.out.printf("%s at %s: %,d of %,d seats remaining%n", fixture, fixture.getStadium().getName(),
                fixture.getSeatsRemaining(), fixture.getStadium().getSeatingCapacity());
        for (TicketCategory category : TicketCategory.values()) {
            double base = fixture.getStadium().getTicketPrice() * category.getPriceMultiplier();
            System.out.printf("  %-11s $%,10.2f + GCT = $%,10.2f%n", category.getCategory(), base, base * (1 + Taxable.GCT_RATE));
        }
    }

    private void sellTicket() throws LeagueException {
        Fixture fixture = FixtureMenu.pickScheduled(league, input);
        if (fixture == null) {
            return;
        }
        printPrices(fixture);
        if (fixture.getSeatsRemaining() == 0) {
            System.out.println("Note: this fixture is sold out, so the sale will be refused.");
        }
        Spectator spectator = chooseSpectator(league, input);
        if (spectator == null) {
            return;
        }
        TicketCategory category = input.pickEnum("Ticket category", TicketCategory.values(),
                c -> c.getCategory() + " (" + c.getPriceMultiplier() + " x base price)");
        if (category == null) {
            return;
        }
        Tickets ticket = fixture.sellTicket(spectator, category);
        System.out.printf("Ticket %s sold to %s: %s, $%,.2f incl. GCT. Seats remaining: %,d%n", ticket.getTicketNo(),
                spectator.getName(), category.getCategory(), ticket.getPriceWithTax(), fixture.getSeatsRemaining());
    }

    private void showSeatsRemaining() {
        Fixture fixture = FixtureMenu.pickScheduled(league, input);
        if (fixture != null) {
            printPrices(fixture);
        }
    }

    private void displayTickets() {
        Fixture fixture = input.pick("Fixtures", league.getFixturesByDate(), FixtureMenu::label);
        if (fixture == null) {
            return;
        }
        if (fixture.getTickets().isEmpty()) {
            System.out.println("No tickets have been sold for " + fixture.getFixtureId() + ".");
            return;
        }
        System.out.printf("%-14s %-24s %-11s %12s%n", "Ticket", "Holder", "Category", "Price + GCT");
        for (Tickets ticket : fixture.getTickets()) {
            System.out.printf("%-14s %-24s %-11s $%,11.2f%n", ticket.getTicketNo(), ticket.getHolder().getName(),
                    ticket.getCategory().getCategory(), ticket.getPriceWithTax());
        }
        System.out.printf("%d ticket(s), revenue $%,.2f incl. GCT%n", fixture.getTickets().size(), fixture.getTicketRevenue());
    }
}
