/**
 * Author: Amar Campbell - 2501977
 * Date: 11/21/2026
 * Description: This class reads and checks console input for every menu. Each prompt states the
 * format it expects, and anything that is not a valid number, date, time or answer is caught here
 * and asked again, so bad text never reaches the entity classes. Lists are shown numbered so the
 * user picks by number instead of retyping IDs, and 0 always goes back.
 */

package oop_project.menu;

import oop_project.entities.Coach;
import oop_project.entities.Fixture;
import oop_project.entities.FootballTeam;
import oop_project.entities.MatchOfficial;
import oop_project.entities.Player;
import oop_project.entities.Product;
import oop_project.entities.Stadium;
import oop_project.entities.interfaces.Taxable;
import oop_project.enums.FieldCondition;
import oop_project.enums.PlayerStatus;
import oop_project.enums.TicketCategory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InputHelper {
    private static final int MAX_TEXT = 60;

    private Scanner scanner;

    public InputHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    // ---------------------------------------------------------------- text

    // Reads one line. If there is no more input, the program stops cleanly.
    public String readLine(String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            System.out.println();
            System.out.println("Input ended. Goodbye.");
            System.exit(0);
        }
        return scanner.nextLine().trim();
    }

    // Text that is not blank and has no commas, because commas separate the fields in the data files
    public String readText(String prompt) {
        return readText(prompt, MAX_TEXT);
    }

    public String readText(String prompt, int maxLength) {
        while (true) {
            String text = readLine(prompt + ": ");
            if (isValidText(text, maxLength)) {
                return text;
            }
        }
    }

    // Leaving the answer blank keeps the current value
    public String readOptionalText(String prompt, String current) {
        while (true) {
            String text = readLine(prompt + " [" + current + ", Enter to keep]: ");
            if (text.isEmpty()) {
                return current;
            }
            if (isValidText(text, MAX_TEXT)) {
                return text;
            }
        }
    }

    private boolean isValidText(String text, int maxLength) {
        if (text.isEmpty()) {
            System.out.println("Please enter a value.");
            return false;
        }
        if (text.contains(",")) {
            System.out.println("Please leave out commas.");
            return false;
        }
        if (text.length() > maxLength) {
            System.out.println("Please use at most " + maxLength + " characters.");
            return false;
        }
        return true;
    }

    // ---------------------------------------------------------------- numbers

    public int readInt(String prompt, int min, int max) {
        while (true) {
            String text = readLine(prompt + " (" + min + " to " + max + "): ");
            try {
                int value = Integer.parseInt(text);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Please enter a whole number from " + min + " to " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number from " + min + " to " + max + ".");
            }
        }
    }

    // Leaving the answer blank keeps the current value
    public int readOptionalInt(String prompt, int current, int min, int max) {
        while (true) {
            String text = readLine(prompt + " (" + min + " to " + max + ") [" + current + ", Enter to keep]: ");
            if (text.isEmpty()) {
                return current;
            }
            try {
                int value = Integer.parseInt(text);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Please enter a whole number from " + min + " to " + max + ", or press Enter.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number from " + min + " to " + max + ", or press Enter.");
            }
        }
    }

    // An amount of money in dollars, for example 1500 or 1500.50
    public double readMoney(String prompt) {
        while (true) {
            String text = readLine(prompt + " ($, e.g. 1500.00): ");
            double amount = parseMoney(text);
            if (amount >= 0) {
                return amount;
            }
            System.out.println("Please enter an amount of at least $0.00.");
        }
    }

    // Leaving the answer blank keeps the current value
    public double readOptionalMoney(String prompt, double current) {
        while (true) {
            String text = readLine(String.format("%s ($) [%.2f, Enter to keep]: ", prompt, current));
            if (text.isEmpty()) {
                return current;
            }
            double amount = parseMoney(text);
            if (amount >= 0) {
                return amount;
            }
            System.out.println("Please enter an amount of at least $0.00, or press Enter.");
        }
    }

    // Turns text into an amount rounded to cents. Returns -1 when it is not a valid amount.
    private double parseMoney(String text) {
        try {
            double value = Double.parseDouble(text.replace("$", "").replace(",", ""));
            if (value >= 0 && value <= 1000000000) {
                return Math.round(value * 100) / 100.0;
            }
            return -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // ---------------------------------------------------------------- dates and times

    public LocalDate readDate(String prompt) {
        while (true) {
            String text = readLine(prompt + " (YYYY-MM-DD): ");
            try {
                return LocalDate.parse(text);
            } catch (DateTimeParseException e) {
                System.out.println("Please enter a real date as YYYY-MM-DD, for example 2026-12-05.");
            }
        }
    }

    // Returns null when the answer is left blank
    public LocalDate readOptionalDate(String prompt, String blankMeaning) {
        while (true) {
            String text = readLine(prompt + " (YYYY-MM-DD) [Enter for " + blankMeaning + "]: ");
            if (text.isEmpty()) {
                return null;
            }
            try {
                return LocalDate.parse(text);
            } catch (DateTimeParseException e) {
                System.out.println("Please enter a real date as YYYY-MM-DD, or press Enter.");
            }
        }
    }

    public LocalTime readTime(String prompt) {
        while (true) {
            String text = readLine(prompt + " (HH:MM, 24-hour): ");
            try {
                return LocalTime.parse(text);
            } catch (DateTimeParseException e) {
                System.out.println("Please enter a time as HH:MM in 24-hour format, for example 15:00.");
            }
        }
    }

    // Returns null when the answer is left blank
    public LocalTime readOptionalTime(String prompt, String blankMeaning) {
        while (true) {
            String text = readLine(prompt + " (HH:MM, 24-hour) [Enter for " + blankMeaning + "]: ");
            if (text.isEmpty()) {
                return null;
            }
            try {
                return LocalTime.parse(text);
            } catch (DateTimeParseException e) {
                System.out.println("Please enter a time as HH:MM in 24-hour format, or press Enter.");
            }
        }
    }

    // ---------------------------------------------------------------- yes or no

    public boolean readYesNo(String prompt) {
        while (true) {
            String text = readLine(prompt + " (Y/N): ");
            if (text.equalsIgnoreCase("Y")) {
                return true;
            }
            if (text.equalsIgnoreCase("N")) {
                return false;
            }
            System.out.println("Please answer Y or N.");
        }
    }

    // Asks "Are you sure?" before something that cannot be undone
    public boolean confirm(String action) {
        return readYesNo(action + " Are you sure?");
    }

    // ---------------------------------------------------------------- menus and numbered lists

    // Prints a menu and returns the option picked (0 is always Back or Exit)
    public int menu(String title, String backLabel, String[] options) {
        System.out.println();
        System.out.println("=== " + title + " ===");
        for (int i = 0; i < options.length; i++) {
            System.out.printf("%2d. %s%n", i + 1, options[i]);
        }
        System.out.printf("%2d. %s%n", 0, backLabel);
        return readInt("Choose an option", 0, options.length);
    }

    // Shows the labels numbered from 1. Returns the position picked in the list,
    // or -1 when the user goes back or the list is empty.
    private int pickPosition(String title, List<String> labels) {
        if (labels.isEmpty()) {
            System.out.println("There is nothing to choose from under " + title + " yet.");
            return -1;
        }
        System.out.println(title + ":");
        for (int i = 0; i < labels.size(); i++) {
            System.out.printf("  %2d. %s%n", i + 1, labels.get(i));
        }
        System.out.println("   0. Back");
        return readInt("Choose", 0, labels.size()) - 1;
    }

    // Polymorphism: works for a ticket or a product, because both implement Taxable
    static String priceWithTax(Taxable item) {
        return String.format("$%,.2f", item.getPriceWithTax());
    }

    // Each pick method below builds the labels, then returns the object picked, or null for Back

    public FootballTeam pickTeam(String title, List<FootballTeam> teams) {
        List<String> labels = new ArrayList<>();
        for (FootballTeam team : teams) {
            labels.add(team.getRegistrationNo() + "  " + team.getName() + " (" + team.getStatus().getStatus()
                    + ", squad " + team.getSquadSize() + "/" + team.getMaxSquadSize() + ")");
        }
        int position = pickPosition(title, labels);
        if (position == -1) {
            return null;
        }
        return teams.get(position);
    }

    public Player pickPlayer(FootballTeam team) {
        List<String> labels = new ArrayList<>();
        for (Player player : team.getPlayers()) {
            labels.add("#" + player.getJerseyNumber() + " " + player.getName() + " (" + player.getId() + ", " + player.getPosition() + ")");
        }
        int position = pickPosition(team.getName() + " players", labels);
        if (position == -1) {
            return null;
        }
        return team.getPlayers().get(position);
    }

    public Coach pickCoach(FootballTeam team) {
        List<String> labels = new ArrayList<>();
        for (Coach coach : team.getCoaches()) {
            labels.add(coach.getName() + " (" + coach.getId() + ", " + coach.getCoachingRole() + ")");
        }
        int position = pickPosition(team.getName() + " coaches", labels);
        if (position == -1) {
            return null;
        }
        return team.getCoaches().get(position);
    }

    public MatchOfficial pickOfficial(List<MatchOfficial> officials) {
        List<String> labels = new ArrayList<>();
        for (MatchOfficial official : officials) {
            String availability = "unavailable";
            if (official.isAvailable()) {
                availability = "available";
            }
            labels.add(official.getName() + " (" + official.getId() + ", " + official.getOfficiatingRole() + ", " + availability + ")");
        }
        int position = pickPosition("Officials", labels);
        if (position == -1) {
            return null;
        }
        return officials.get(position);
    }

    public Stadium pickStadium(List<Stadium> stadiums) {
        List<String> labels = new ArrayList<>();
        for (Stadium stadium : stadiums) {
            labels.add(stadium.getStadiumId() + "  " + stadium.getName() + " (field: " + stadium.getFieldCondition().getCondition() + ")");
        }
        int position = pickPosition("Stadiums", labels);
        if (position == -1) {
            return null;
        }
        return stadiums.get(position);
    }

    public Fixture pickFixture(String title, List<Fixture> fixtures) {
        List<String> labels = new ArrayList<>();
        for (Fixture fixture : fixtures) {
            labels.add(fixture.getFixtureId() + "  " + fixture + " at " + fixture.getStadium().getName()
                    + " [" + fixture.getStatus().getStatus() + "]");
        }
        int position = pickPosition(title, labels);
        if (position == -1) {
            return null;
        }
        return fixtures.get(position);
    }

    static String productLabel(Product product) {
        return String.format("%-8s %-30s %10s incl. GCT  (%d in stock)", product.getProductCode(), product.getName(),
                priceWithTax(product), product.getQuantityInStock());
    }

    public Product pickProduct(List<Product> products) {
        List<String> labels = new ArrayList<>();
        for (Product product : products) {
            labels.add(productLabel(product));
        }
        int position = pickPosition("Products", labels);
        if (position == -1) {
            return null;
        }
        return products.get(position);
    }

    public PlayerStatus pickPlayerStatus() {
        PlayerStatus[] values = PlayerStatus.values();
        List<String> labels = new ArrayList<>();
        for (PlayerStatus value : values) {
            labels.add(value.getStatus());
        }
        int position = pickPosition("Player status", labels);
        if (position == -1) {
            return null;
        }
        return values[position];
    }

    public FieldCondition pickFieldCondition() {
        FieldCondition[] values = FieldCondition.values();
        List<String> labels = new ArrayList<>();
        for (FieldCondition value : values) {
            labels.add(value.getCondition());
        }
        int position = pickPosition("Field condition", labels);
        if (position == -1) {
            return null;
        }
        return values[position];
    }

    public TicketCategory pickTicketCategory() {
        TicketCategory[] values = TicketCategory.values();
        List<String> labels = new ArrayList<>();
        for (TicketCategory value : values) {
            labels.add(value.getCategory() + " (" + value.getPriceMultiplier() + " x base price)");
        }
        int position = pickPosition("Ticket category", labels);
        if (position == -1) {
            return null;
        }
        return values[position];
    }
}
