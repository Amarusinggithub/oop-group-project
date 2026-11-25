/**
 * Author: Amar Campbell - 2501977
 * Date: 11/21/2026
 * Description: This is the program's entry point. It loads the league's data files from the data
 * folder (or the folder given as the first argument), reports any lines it had to skip, and opens
 * the main menu.
 */

package oop_project;

import oop_project.entities.FootballLeague;
import oop_project.menu.InputHelper;
import oop_project.menu.MainMenu;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static final String LEAGUE_NAME = "Kingston Premier Football League";
    public static final int SEASON = 2026;

    public static void main(String[] args) {
        Path dataFolder = Path.of(args.length > 0 ? args[0] : "data");
        FootballLeague league = new FootballLeague(LEAGUE_NAME, SEASON);

        System.out.println("KPFL League and Match-Day Management System");
        if (!Files.isDirectory(dataFolder)) {
            System.out.println("No data folder at " + dataFolder.toAbsolutePath() + " yet: starting with an empty league.");
        } else {
            try {
                List<String> warnings = league.loadData(dataFolder);
                System.out.printf("Loaded %d teams, %d stadiums, %d officials and %d fixtures from %s.%n",
                        league.getTeams().size(), league.getStadiums().size(), league.getOfficials().size(),
                        league.getFixtures().size(), dataFolder.toAbsolutePath());
                if (!warnings.isEmpty()) {
                    System.out.println(warnings.size() + " line(s) could not be read and were skipped:");
                    for (String warning : warnings) {
                        System.out.println("  " + warning);
                    }
                }
            } catch (IOException e) {
                System.out.println("Could not read the data files (" + e.getMessage() + "): starting with an empty league.");
                league = new FootballLeague(LEAGUE_NAME, SEASON);
            }
        }

        new MainMenu(league, new InputHelper(new Scanner(System.in)), dataFolder).run();
    }
}
