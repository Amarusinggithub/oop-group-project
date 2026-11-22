/**
 * Author: Amar Campbell - 2501977
 * Date: 11/21/2026
 * Description: This class reads and checks console input for every menu. Each prompt states the
 * format it expects, and anything that is not a valid number, date, time or answer is caught here
 * and asked again, so bad text never reaches the entity classes. Lists are shown numbered so the
 * user picks by number instead of retyping IDs, and 0 always goes back.
 */

package oop_project.menu;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

public class InputHelper {
    private final Scanner scanner;

    public InputHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    // Reads one line. If input ends (for example a piped script runs out), the program stops cleanly.
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
        return readText(prompt, Integer.MAX_VALUE);
    }

    public String readText(String prompt, int maxLength) {
        while (true) {
            String text = readLine(prompt + ": ");
            String problem = checkText(text, maxLength);
            if (problem == null) {
                return text;
            }
            System.out.println(problem);
        }
    }

    // Leaving the answer blank keeps the current value
    public String readOptionalText(String prompt, String current) {
        return readOptionalText(prompt, current, Integer.MAX_VALUE);
    }

    public String readOptionalText(String prompt, String current, int maxLength) {
        while (true) {
            String text = readLine(prompt + " [" + current + ", Enter to keep]: ");
            if (text.isEmpty()) {
                return current;
            }
            String problem = checkText(text, maxLength);
            if (problem == null) {
                return text;
            }
            System.out.println(problem);
        }
    }

    private static String checkText(String text, int maxLength) {
        if (text.isEmpty()) {
            return "Please enter a value.";
        }
        if (text.contains(",")) {
            return "Please leave out commas.";
        }
        if (text.length() > maxLength) {
            return "Please use at most " + maxLength + " characters.";
        }
        return null;
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            String text = readLine(prompt + " (" + min + " to " + max + "): ");
            try {
                int value = Integer.parseInt(text);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // falls through to the message below
            }
            System.out.println("Please enter a whole number from " + min + " to " + max + ".");
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
            } catch (NumberFormatException ignored) {
                // falls through to the message below
            }
            System.out.println("Please enter a whole number from " + min + " to " + max + ", or press Enter.");
        }
    }

    // An amount of money in dollars, for example 1500 or 1500.50
    public double readMoney(String prompt, double min) {
        while (true) {
            String text = readLine(prompt + " ($, e.g. 1500.00): ").replace("$", "").replace(",", "");
            try {
                double value = Double.parseDouble(text);
                if (value >= min && !Double.isNaN(value) && !Double.isInfinite(value)) {
                    return Math.round(value * 100) / 100.0;
                }
            } catch (NumberFormatException ignored) {
                // falls through to the message below
            }
            System.out.printf("Please enter an amount of at least $%,.2f.%n", min);
        }
    }

    public double readOptionalMoney(String prompt, double current, double min) {
        while (true) {
            String text = readLine(String.format("%s ($) [%.2f, Enter to keep]: ", prompt, current)).replace("$", "").replace(",", "");
            if (text.isEmpty()) {
                return current;
            }
            try {
                double value = Double.parseDouble(text);
                if (value >= min && !Double.isNaN(value) && !Double.isInfinite(value)) {
                    return Math.round(value * 100) / 100.0;
                }
            } catch (NumberFormatException ignored) {
                // falls through to the message below
            }
            System.out.printf("Please enter an amount of at least $%,.2f, or press Enter.%n", min);
        }
    }

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
                return parseTime(text);
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
                return parseTime(text);
            } catch (DateTimeParseException e) {
                System.out.println("Please enter a time as HH:MM in 24-hour format, or press Enter.");
            }
        }
    }

    // Accepts 9:30 as well as 09:30
    private static LocalTime parseTime(String text) {
        String padded = text.length() == 4 && text.charAt(1) == ':' ? "0" + text : text;
        return LocalTime.parse(padded);
    }

    public boolean readYesNo(String prompt) {
        while (true) {
            String text = readLine(prompt + " (Y/N): ");
            if (text.equalsIgnoreCase("Y") || text.equalsIgnoreCase("yes")) {
                return true;
            }
            if (text.equalsIgnoreCase("N") || text.equalsIgnoreCase("no")) {
                return false;
            }
            System.out.println("Please answer Y or N.");
        }
    }

    // Asks "Are you sure?" before something that cannot be undone
    public boolean confirm(String action) {
        return readYesNo(action + " Are you sure?");
    }

    // Shows the items numbered from 1 and returns the one picked, or null for 0 (back) or an empty list
    public <T> T pick(String title, List<T> items, Function<T, String> label) {
        if (items.isEmpty()) {
            System.out.println("There are no " + title.toLowerCase() + " to choose from yet.");
            return null;
        }
        System.out.println(title + ":");
        for (int i = 0; i < items.size(); i++) {
            System.out.printf("  %2d. %s%n", i + 1, label.apply(items.get(i)));
        }
        System.out.println("   0. Back");
        int choice = readInt("Choose", 0, items.size());
        return choice == 0 ? null : items.get(choice - 1);
    }

    // Picks a value from an enum, for example a ticket category; returns null for 0 (back)
    public <E extends Enum<E>> E pickEnum(String title, E[] values, Function<E, String> label) {
        return pick(title, List.of(values), label);
    }

    // Prints a menu and returns the option picked (0 is always Back or Exit)
    public int menu(String title, String backLabel, String... options) {
        System.out.println();
        System.out.println("=== " + title + " ===");
        for (int i = 0; i < options.length; i++) {
            System.out.printf("%2d. %s%n", i + 1, options[i]);
        }
        System.out.printf("%2d. %s%n", 0, backLabel);
        return readInt("Choose an option", 0, options.length);
    }
}
