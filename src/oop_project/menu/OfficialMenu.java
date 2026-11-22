/**
 * Author: Kemece Hinds - 2501990
 * Date: 11/21/2026
 * Description: This class is the Match officials menu. It registers officials, changes their
 * availability, assigns them to fixtures without clashes, inspects whether a fixture is ready,
 * and records the league commissioner who approves teams and assigns officials.
 */

package oop_project.menu;

import oop_project.entities.Fixture;
import oop_project.entities.FootballLeague;
import oop_project.entities.LeagueCommissioner;
import oop_project.entities.MatchOfficial;
import oop_project.exceptions.LeagueException;

public class OfficialMenu {
    private final FootballLeague league;
    private final InputHelper input;

    public OfficialMenu(FootballLeague league, InputHelper input) {
        this.league = league;
        this.input = input;
    }

    public void run() {
        while (true) {
            int choice = input.menu("Match officials", "Back",
                    "Register official", "Assign official to fixture", "Inspect fixture readiness",
                    "Display officials", "Change an official's availability", "Set league commissioner");
            if (choice == 0) {
                return;
            }
            try {
                switch (choice) {
                    case 1 -> registerOfficial();
                    case 2 -> assignOfficial();
                    case 3 -> inspectFixture();
                    case 4 -> displayOfficials();
                    case 5 -> changeAvailability();
                    case 6 -> setCommissioner();
                    default -> System.out.println("Please choose an option from the menu.");
                }
            } catch (LeagueException | IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static String label(MatchOfficial official) {
        return official.getName() + " (" + official.getId() + ", " + official.getOfficiatingRole() + ", "
                + (official.isAvailable() ? "available" : "unavailable") + ")";
    }

    private void registerOfficial() throws LeagueException {
        String id = input.readText("Official ID (e.g. MO07)", 10);
        String name = input.readText("Name");
        int age = input.readInt("Age", 18, 99);
        String gender = input.readText("Gender (e.g. Male or Female)");
        String role = input.readText("Officiating role (e.g. Referee, Assistant Referee, Fourth Official)");
        String rank = input.readText("Rank (e.g. FIFA, National, Regional)");
        boolean available = input.readYesNo("Available for fixtures?");
        MatchOfficial official = new MatchOfficial(id, name, age, gender, role, rank, available);
        league.registerOfficial(official);
        System.out.println(official.getRole() + " registered: " + official.displayInfo());
    }

    private void assignOfficial() throws LeagueException {
        Fixture fixture = FixtureMenu.pickScheduled(league, input);
        if (fixture == null) {
            return;
        }
        MatchOfficial official = input.pick("Officials", league.getOfficials(), OfficialMenu::label);
        if (official == null) {
            return;
        }
        LeagueCommissioner commissioner = league.getCommissioner();
        if (commissioner != null) {
            commissioner.assignOfficial(official, fixture);
        } else {
            fixture.assignOfficial(official);
        }
        System.out.println(official.getName() + " assigned to " + fixture.getFixtureId() + " (" + fixture + "). Officials on this fixture: "
                + fixture.getOfficials().size());
    }

    private void inspectFixture() {
        LeagueCommissioner commissioner = league.getCommissioner();
        if (commissioner == null) {
            System.out.println("There is no league commissioner on record. Choose Set league commissioner first.");
            return;
        }
        Fixture fixture = input.pick("Fixtures", league.getFixturesByDate(), FixtureMenu::label);
        if (fixture == null) {
            return;
        }
        System.out.print(commissioner.inspectionReport(fixture));
        if (!fixture.getOfficials().isEmpty()) {
            System.out.println("Officials:");
            for (MatchOfficial official : fixture.getOfficials()) {
                System.out.println("  " + official.getName() + " - " + official.getOfficiatingRole() + " (" + official.getRank() + ")");
            }
        }
    }

    private void displayOfficials() {
        if (league.getOfficials().isEmpty()) {
            System.out.println("No match officials are registered yet.");
            return;
        }
        System.out.printf("%-6s %-24s %-20s %-10s %-9s %s%n", "ID", "Name", "Role", "Rank", "Available", "Fixtures");
        for (MatchOfficial official : league.getOfficials()) {
            System.out.printf("%-6s %-24s %-20s %-10s %-9s %d%n", official.getId(), official.getName(), official.getOfficiatingRole(),
                    official.getRank(), official.isAvailable() ? "Yes" : "No", official.getAssignedFixtures().size());
        }
    }

    private void changeAvailability() {
        MatchOfficial official = input.pick("Officials", league.getOfficials(), OfficialMenu::label);
        if (official == null) {
            return;
        }
        official.setAvailable(input.readYesNo("Is " + official.getName() + " available for new fixtures?"));
        System.out.println(official.getName() + " is now " + (official.isAvailable() ? "available." : "unavailable."));
    }

    private void setCommissioner() {
        LeagueCommissioner current = league.getCommissioner();
        if (current != null) {
            System.out.println("Current commissioner: " + current.displayInfo());
            if (!input.confirm("Replace " + current.getName() + "?")) {
                System.out.println("Nothing was changed.");
                return;
            }
        }
        String id = input.readText("Commissioner ID (e.g. LC01)", 10);
        String name = input.readText("Name");
        int age = input.readInt("Age", 18, 99);
        String gender = input.readText("Gender (e.g. Male or Female)");
        String title = input.readText("Office title (e.g. League Commissioner)");
        LeagueCommissioner commissioner = new LeagueCommissioner(id, name, age, gender, title);
        league.setCommissioner(commissioner);
        System.out.println(commissioner.getName() + " is now the " + commissioner.getRole() + ".");
    }
}
