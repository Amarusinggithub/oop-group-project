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
import java.util.Comparator;
import java.util.List;

public class ReportMenu {
    private final FootballLeague league;
    private final InputHelper input;

    public ReportMenu(FootballLeague league, InputHelper input) {
        this.league = league;
        this.input = input;
    }

    public void run() {
        while (true) {
            int choice = input.menu("Reports & search", "Back",
                    "Report: league standings", "Report: fixtures and results", "Report: shop sales by stadium",
                    "Report: top scorers", "Report: attendance and ticket revenue", "Report: squad list for a team",
                    "Search: find a team", "Search: find a player", "Search: find fixtures by date or team",
                    "Search: find a shop product");
            if (choice == 0) {
                return;
            }
            try {
                System.out.println();
                switch (choice) {
                    case 1 -> System.out.print(league.standingsReport());
                    case 2 -> System.out.print(league.fixturesReport());
                    case 3 -> System.out.print(league.shopSalesReport());
                    case 4 -> System.out.print(topScorersReport(league));
                    case 5 -> System.out.print(attendanceReport(league));
                    case 6 -> squadReport();
                    case 7 -> findTeam();
                    case 8 -> findPlayer();
                    case 9 -> findFixtures();
                    case 10 -> findProduct();
                    default -> System.out.println("Please choose an option from the menu.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // RPT-04: players with at least one goal, highest first
    public static String topScorersReport(FootballLeague league) {
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
        scorers.sort(Comparator.comparingInt(Player::getGaols).reversed().thenComparing(Player::getName));
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
    public static String attendanceReport(FootballLeague league) {
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
    public static String squadReport(FootballTeam team) {
        if (team.getPlayers().isEmpty()) {
            return "There is nothing to report yet: " + team.getName() + " has no players.\n";
        }
        List<Player> players = new ArrayList<>(team.getPlayers());
        players.sort(Comparator.comparingInt(Player::getJerseyNumber));
        StringBuilder report = new StringBuilder("SQUAD LIST - " + team.getName() + " (" + team.getSquadSize() + "/" + team.getMaxSquadSize() + ")\n");
        report.append(String.format("%-4s %-8s %-24s %-12s %4s %-10s %s%n", "No.", "ID", "Player", "Position", "Age", "Status", "Eligible"));
        for (Player player : players) {
            report.append(String.format("%-4d %-8s %-24s %-12s %4d %-10s %s%n", player.getJerseyNumber(), player.getId(), player.getName(),
                    player.getPosition(), player.getAge(), player.getStatus().getStatus(), player.isEligible() ? "Yes" : "No"));
        }
        return report.toString();
    }

    private void squadReport() {
        FootballTeam team = input.pick("Teams", league.getTeams(), FootballTeam::getName);
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
        int choice = input.menu("Find fixtures", "Back", "By date", "By team");
        List<Fixture> fixtures;
        if (choice == 1) {
            LocalDate date = input.readDate("Date");
            fixtures = league.findFixtures(date);
        } else if (choice == 2) {
            FootballTeam team = input.pick("Teams", league.getTeams(), FootballTeam::getName);
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
                System.out.println(stadium.getName() + ": " + ShopMenu.productLabel(product) + ", " + product.getCategory());
            }
        }
        if (!found) {
            System.out.println("No match found.");
        }
    }
}
