/**
 * Author: Amar Campbell - 2501977
 * Date: 11/21/2026
 * Description: This class is the main console menu. It opens the sub-menu for each of the nine
 * league areas, saves the data (option 10), and on exit offers to save before closing.
 */

package oop_project.menu;

import oop_project.entities.FootballLeague;

import java.io.IOException;
import java.nio.file.Path;

public class MainMenu {
    private final FootballLeague league;
    private final InputHelper input;
    private final Path dataFolder;

    public MainMenu(FootballLeague league, InputHelper input, Path dataFolder) {
        this.league = league;
        this.input = input;
        this.dataFolder = dataFolder;
    }

    public void run() {
        while (true) {
            int choice = input.menu(league.getName() + " - Season " + league.getSeason(), "Exit",
                    "Teams", "Players & coaches", "Stadiums", "Fixtures", "Match officials",
                    "Results & standings", "Ticket sales", "Stadium shop", "Reports & search", "Save data");
            try {
                switch (choice) {
                    case 1 -> new TeamMenu(league, input).run();
                    case 2 -> new PersonnelMenu(league, input).run();
                    case 3 -> new StadiumMenu(league, input).run();
                    case 4 -> new FixtureMenu(league, input).run();
                    case 5 -> new OfficialMenu(league, input).run();
                    case 6 -> new ResultsMenu(league, input).run();
                    case 7 -> new TicketMenu(league, input).run();
                    case 8 -> new ShopMenu(league, input, dataFolder).run();
                    case 9 -> new ReportMenu(league, input).run();
                    case 10 -> save();
                    case 0 -> {
                        if (confirmExit()) {
                            System.out.println("Goodbye.");
                            return;
                        }
                    }
                    default -> System.out.println("Please choose an option from the menu.");
                }
            } catch (RuntimeException e) {
                // Each sub-menu handles its own errors; this is a last safety net so the program never crashes
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
    }

    // Returns true when it is safe to exit
    private boolean confirmExit() {
        if (input.readYesNo("Save your changes before exiting?")) {
            return save();
        }
        return input.confirm("Exit without saving?");
    }

    // Returns true when the save succeeded
    private boolean save() {
        try {
            int records = league.saveData(dataFolder);
            System.out.println("Saved " + records + " records to " + dataFolder.toAbsolutePath() + ".");
            return true;
        } catch (IOException | RuntimeException e) {
            System.out.println("Could not save the data: " + e.getMessage() + ". Your previous saved data is unchanged.");
            return false;
        }
    }
}
