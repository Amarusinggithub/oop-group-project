/**
 * Author: Kemece Hinds - 2501990
 * Date: 11/21/2026
 * Description: This class is the Results & standings menu. It records a fixture's result, which
 * completes the fixture and updates both teams' records and points, and shows the standings.
 */

package oop_project.menu;

import oop_project.entities.Fixture;
import oop_project.entities.FootballLeague;
import oop_project.entities.FootballTeam;
import oop_project.exceptions.LeagueException;

public class ResultsMenu {
    private final FootballLeague league;
    private final InputHelper input;

    public ResultsMenu(FootballLeague league, InputHelper input) {
        this.league = league;
        this.input = input;
    }

    public void run() {
        while (true) {
            int choice = input.menu("Results & standings", "Back", "Record result", "Display standings");
            if (choice == 0) {
                return;
            }
            try {
                switch (choice) {
                    case 1 -> recordResult();
                    case 2 -> System.out.print(league.standingsReport());
                    default -> System.out.println("Please choose an option from the menu.");
                }
            } catch (LeagueException | IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void recordResult() throws LeagueException {
        Fixture fixture = FixtureMenu.pickScheduled(league, input);
        if (fixture == null) {
            return;
        }
        // The range allows a negative entry so the league's own rule check reports it
        int homeGoals = input.readInt(fixture.getHomeTeam().getName() + " goals", -20, 50);
        int awayGoals = input.readInt(fixture.getAwayTeam().getName() + " goals", -20, 50);
        league.recordResult(fixture, homeGoals, awayGoals);
        FootballTeam winner = fixture.getWinner();
        System.out.println("Result recorded: " + fixture.getHomeTeam().getName() + " " + fixture.getResult().getScoreLine() + " "
                + fixture.getAwayTeam().getName() + " (attendance " + fixture.getResult().getAttendance() + "). "
                + (winner == null ? "A draw: 1 point each." : winner.getName() + " win and take 3 points."));
        System.out.println("Fixture " + fixture.getFixtureId() + " is now " + fixture.getStatus().getStatus() + ".");
        System.out.println();
        System.out.print(league.standingsReport());
    }
}
