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
    private FootballLeague league;
    private InputHelper input;

    public OfficialMenu(FootballLeague league, InputHelper input) {
        this.league = league;
        this.input = input;
    }

    public void run() {
        while (true) {
            String[] options = {"Register official", "Assign official to fixture", "Inspect fixture readiness",
                    "Display officials", "Change an official's availability", "Set league commissioner"};
            int choice = input.menu("Match officials", "Back", options);
            if (choice == 0) {
                return;
            }
            try {
                switch (choice) {
                    case 1:
                        registerOfficial();
                        break;
                    case 2:
                        assignOfficial();
                        break;
                    case 3:
                        inspectFixture();
                        break;
                    case 4:
                        displayOfficials();
                        break;
                    case 5:
                        changeAvailability();
                        break;
                    case 6:
                        setCommissioner();
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
        MatchOfficial official = input.pickOfficial(league.getOfficials());
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
        Fixture fixture = input.pickFixture("Fixtures", league.getFixturesByDate());
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
            String available = "No";
            if (official.isAvailable()) {
                available = "Yes";
            }
            System.out.printf("%-6s %-24s %-20s %-10s %-9s %d%n", official.getId(), official.getName(), official.getOfficiatingRole(),
                    official.getRank(), available, official.getAssignedFixtures().size());
        }
    }

    private void changeAvailability() {
        MatchOfficial official = input.pickOfficial(league.getOfficials());
        if (official == null) {
            return;
        }
        official.setAvailable(input.readYesNo("Is " + official.getName() + " available for new fixtures?"));
        if (official.isAvailable()) {
            System.out.println(official.getName() + " is now available.");
        } else {
            System.out.println(official.getName() + " is now unavailable.");
        }
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
