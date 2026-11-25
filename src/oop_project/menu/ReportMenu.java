/**
 * Author: Shenecia Williams - 2501202
 * Date: 11/21/2026
 * Description: This class is the Reports & search menu. It prints six management reports
 * (standings, fixtures and results, shop sales, top scorers, attendance and ticket revenue, and a
 * team's squad list) and four searches (team, player, fixtures, and shop product).
 */

package oop_project.menu;

import oop_project.entities.Fixture;
import oop_project.entities.FootballLeague;
import oop_project.entities.FootballTeam;
import oop_project.entities.Player;
import oop_project.entities.Product;
import oop_project.entities.Stadium;
import oop_project.enums.FixtureStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportMenu {
    private FootballLeague league;
    private InputHelper input;

    public ReportMenu(FootballLeague league, InputHelper input) {
        this.league = league;
        this.input = input;
    }

    public void run() {
        while (true) {
            String[] options = {"Report: league standings", "Report: fixtures and results", "Report: shop sales by stadium",
                    "Report: top scorers", "Report: attendance and ticket revenue", "Report: squad list for a team",
                    "Search: find a team", "Search: find a player", "Search: find fixtures by date or team",
                    "Search: find a shop product"};
            int choice = input.menu("Reports & search", "Back", options);
            if (choice == 0) {
                return;
            }
            try {
                System.out.println();
                switch (choice) {
                    case 1:
                        System.out.print(league.standingsReport());
                        break;
                    case 2:
                        System.out.print(league.fixturesReport());
                        break;
                    case 3:
                        System.out.print(league.shopSalesReport());
                        break;
                    case 4:
                        System.out.print(topScorersReport());
                        break;
                    case 5:
                        System.out.print(attendanceReport());
                        break;
                    case 6:
                        showSquad();
                        break;
                    case 7:
                        findTeam();
                        break;
                    case 8:
                        findPlayer();
                        break;
                    case 9:
                        findFixtures();
                        break;
                    case 10:
                        findProduct();
                        break;
                    default:
                        System.out.println("Please choose an option from the menu.");
                        break;
                }
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // RPT-04: players with at least one goal, highest first
    private String topScorersReport() {
        List<Player> scorers = new ArrayList<>();
        for (FootballTeam team : league.getTeams()) {
            for (Player player : team.getPlayers()) {
                if (player.getGaols() > 0) {
                    scorers.add(player);
                }
            }
        }
        if (scorers.isEmpty()) {
            return "There is nothing to report yet: no goals have been recorded for any player.\n";
        }
        // Bubble sort: most goals first
        for (int pass = 0; pass < scorers.size() - 1; pass++) {
            for (int i = 0; i < scorers.size() - 1 - pass; i++) {
                Player first = scorers.get(i);
                Player second = scorers.get(i + 1);
                if (second.getGaols() > first.getGaols()) {
                    scorers.set(i, second);
                    scorers.set(i + 1, first);
                }
            }
        }
        StringBuilder report = new StringBuilder("TOP SCORERS\n");
        report.append(String.format("%-4s %-24s %-22s %-12s %5s%n", "Pos", "Player", "Team", "Position", "Goals"));
        int position = 1;
        for (Player player : scorers) {
            report.append(String.format("%-4d %-24s %-22s %-12s %5d%n", position++, player.getName(),
                    league.findTeamOf(player).getName(), player.getPosition(), player.getGaols()));
        }
        return report.toString();
    }

    // RPT-05: tickets sold against capacity, and ticket revenue including GCT, for every fixture that was not cancelled
    private String attendanceReport() {
        List<Fixture> fixtures = new ArrayList<>();
        for (Fixture fixture : league.getFixturesByDate()) {
            if (fixture.getStatus() != FixtureStatus.CANCELLED) {
                fixtures.add(fixture);
            }
        }
        if (fixtures.isEmpty()) {
            return "There is nothing to report yet: no fixtures have been created.\n";
        }
        StringBuilder report = new StringBuilder("ATTENDANCE AND TICKET REVENUE (incl. GCT)\n");
        report.append(String.format("%-6s %-10s %-36s %-32s %9s %7s %7s %14s%n",
                "ID", "Date", "Fixture", "Stadium", "Capacity", "Sold", "% Cap", "Revenue"));
        int totalSold = 0;
        double totalRevenue = 0;
        for (Fixture fixture : fixtures) {
            int capacity = fixture.getStadium().getSeatingCapacity();
            int sold = fixture.getTickets().size();
            double revenue = fixture.getTicketRevenue();
            totalSold += sold;
            totalRevenue += revenue;
            report.append(String.format("%-6s %-10s %-36s %-32s %,9d %,7d %6.1f%% $%,13.2f%n", fixture.getFixtureId(), fixture.getDate(),
                    fixture.getHomeTeam().getName() + " vs " + fixture.getAwayTeam().getName(), fixture.getStadium().getName(),
                    capacity, sold, 100.0 * sold / capacity, revenue));
        }
        report.append(String.format("%-6s %-10s %-36s %-32s %9s %,7d %7s $%,13.2f%n", "Total", "", "", "", "", totalSold, "", totalRevenue));
        return report.toString();
    }

    // RPT-06: a team's players by jersey number, with whether each meets the minimum age
    private String squadReport(FootballTeam team) {
        if (team.getPlayers().isEmpty()) {
            return "There is nothing to report yet: " + team.getName() + " has no players.\n";
        }
        List<Player> players = new ArrayList<>(team.getPlayers());
        // Bubble sort: lowest jersey number first
        for (int pass = 0; pass < players.size() - 1; pass++) {
            for (int i = 0; i < players.size() - 1 - pass; i++) {
                Player first = players.get(i);
                Player second = players.get(i + 1);
                if (second.getJerseyNumber() < first.getJerseyNumber()) {
                    players.set(i, second);
                    players.set(i + 1, first);
                }
            }
        }
        StringBuilder report = new StringBuilder("SQUAD LIST - " + team.getName() + " (" + team.getSquadSize() + "/" + team.getMaxSquadSize() + ")\n");
        report.append(String.format("%-4s %-8s %-24s %-12s %4s %-10s %s%n", "No.", "ID", "Player", "Position", "Age", "Status", "Eligible"));
        for (Player player : players) {
            String eligible = "No";
            if (player.isEligible()) {
                eligible = "Yes";
            }
            report.append(String.format("%-4d %-8s %-24s %-12s %4d %-10s %s%n", player.getJerseyNumber(), player.getId(), player.getName(),
                    player.getPosition(), player.getAge(), player.getStatus().getStatus(), eligible));
        }
        return report.toString();
    }

    private void showSquad() {
        FootballTeam team = input.pickTeam("Teams", league.getTeams());
        if (team != null) {
            System.out.print(squadReport(team));
        }
    }

    // SR-01
    private void findTeam() {
        FootballTeam team = league.searchTeam(input.readText("Team name or registration number"));
        if (team == null) {
            System.out.println("No match found.");
            return;
        }
        TeamMenu.printTeam(team);
    }

    // SR-02
    private void findPlayer() {
        List<Player> players = league.searchPlayers(input.readText("Player ID or part of the name"));
        if (players.isEmpty()) {
            System.out.println("No match found.");
            return;
        }
        for (Player player : players) {
            System.out.println(player.displayInfo() + ", Team: " + league.findTeamOf(player).getName());
        }
    }

    // SR-03
    private void findFixtures() {
        String[] options = {"By date", "By team"};
        int choice = input.menu("Find fixtures", "Back", options);
        List<Fixture> fixtures;
        if (choice == 1) {
            LocalDate date = input.readDate("Date");
            fixtures = league.findFixtures(date);
        } else if (choice == 2) {
            FootballTeam team = input.pickTeam("Teams", league.getTeams());
            if (team == null) {
                return;
            }
            fixtures = league.findFixtures(team);
        } else {
            return;
        }
        if (fixtures.isEmpty()) {
            System.out.println("No match found.");
            return;
        }
        for (Fixture fixture : fixtures) {
            System.out.println(fixture.displayInfo());
        }
    }

    // SR-04: searches every stadium's shop
    private void findProduct() {
        String code = input.readText("Product code");
        boolean found = false;
        for (Stadium stadium : league.getStadiums()) {
            Product product = stadium.getShop().findProduct(code);
            if (product != null) {
                found = true;
                System.out.println(stadium.getName() + ": " + InputHelper.productLabel(product) + ", " + product.getCategory());
            }
        }
        if (!found) {
            System.out.println("No match found.");
        }
    }
}
