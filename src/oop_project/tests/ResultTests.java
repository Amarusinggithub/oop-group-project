/**
 * Author: Kemece Hinds - 2501990
 * Date: 11/21/2026
 * Description: Tests T-17 to T-20 and T-28 from the SRS test plan: completing a fixture needs a
 * valid result, scores cannot be negative, the points system (win 3, draw 1, loss 0), and the
 * order of the standings.
 */

package oop_project.tests;

import oop_project.entities.Fixture;
import oop_project.entities.FootballLeague;
import oop_project.entities.FootballTeam;
import oop_project.entities.Stadium;
import oop_project.enums.FixtureStatus;
import oop_project.enums.TeamStatus;
import oop_project.exceptions.LeagueException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ResultTests {

    public static void run() throws LeagueException {
        FootballLeague league = new FootballLeague("Test League", 2026);
        FootballTeam harbourView = new FootballTeam("T1", "Harbour View", "Kingston", 25);
        FootballTeam waterhouse = new FootballTeam("T2", "Waterhouse", "Kingston", 25);
        FootballTeam cavalier = new FootballTeam("T3", "Cavalier", "St. Andrew", 25);
        FootballTeam portmore = new FootballTeam("T4", "Portmore United", "St. Catherine", 25);
        Stadium stadium = new Stadium("ST1", "National Stadium", "Kingston", 35000, 1500);
        Stadium stadium2 = new Stadium("ST2", "Anthony Spaulding Sports Complex", "Kingston", 7000, 1000);
        FootballTeam[] allTeams = {harbourView, waterhouse, cavalier, portmore};
        for (FootballTeam team : allTeams) {
            team.setStatus(TeamStatus.REGISTERED);
            league.registerTeam(team);
        }
        league.registerStadium(stadium);
        league.registerStadium(stadium2);
        Fixture first = league.createFixture(harbourView, waterhouse, stadium, LocalDate.of(2026, 12, 5), LocalTime.of(15, 0));
        Fixture second = league.createFixture(cavalier, portmore, stadium2, LocalDate.of(2026, 12, 5), LocalTime.of(15, 0));

        // T-17 (invalid): completing a fixture before a result is recorded
        try {
            first.complete();
            Check.notThrown("T-17", "Complete a fixture with no result (invalid)", "ResultNotRecordedException");
        } catch (Exception e) {
            Check.exception("T-17", "Complete a fixture with no result (invalid)", "ResultNotRecordedException", e);
        }
        Check.equal("T-17", "Fixture is still scheduled", FixtureStatus.SCHEDULED, first.getStatus());

        // T-18 (invalid): a negative score
        try {
            league.recordResult(first, -1, 0);
            Check.notThrown("T-18", "Record a score of -1 to 0 (invalid)", "InvalidDataException");
        } catch (Exception e) {
            Check.exception("T-18", "Record a score of -1 to 0 (invalid)", "InvalidDataException", e);
        }
        String resultText = "no result";
        if (first.getResult() != null) {
            resultText = "has a result";
        }
        Check.equal("T-18", "Fixture is still scheduled and has no result", "SCHEDULED, no result",
                first.getStatus() + ", " + resultText);

        // T-19 (normal): a home win
        try {
            league.recordResult(first, 2, 1);
            Check.completed("T-19", "Record 2-1 for the home team (normal)");
        } catch (Exception e) {
            Check.unexpected("T-19", "Record 2-1 for the home team (normal)", e);
        }
        Check.equal("T-19", "Home team points", 3, harbourView.getPoints());
        Check.equal("T-19", "Away team points", 0, waterhouse.getPoints());
        Check.equal("T-19", "Fixture status", FixtureStatus.COMPLETED, first.getStatus());
        Check.equal("T-19", "Winner", "Harbour View", String.valueOf(first.getWinner()));
        try {
            league.recordResult(first, 0, 0);
            Check.notThrown("T-19", "A completed fixture cannot be given another result", "IllegalStateException");
        } catch (Exception e) {
            Check.exception("T-19", "A completed fixture cannot be given another result", "IllegalStateException", e);
        }

        // T-20 (normal): a draw
        try {
            league.recordResult(second, 1, 1);
            Check.completed("T-20", "Record 1-1 (normal)");
        } catch (Exception e) {
            Check.unexpected("T-20", "Record 1-1 (normal)", e);
        }
        Check.equal("T-20", "Each team's points", "1 and 1", cavalier.getPoints() + " and " + portmore.getPoints());

        // T-28 (normal): ordered by points, then goal difference, then goals scored, then name.
        // Cavalier and Portmore United are level on all three, so they are ordered by name.
        List<String> order = new ArrayList<>();
        for (FootballTeam team : league.getStandings()) {
            order.add(team.getName() + " " + team.getPoints());
        }
        Check.equal("T-28", "Standings after T-19 and T-20 (normal)",
                "[Harbour View 3, Cavalier 1, Portmore United 1, Waterhouse 0]", order.toString());
        Check.equal("T-28", "Goal difference of the leader and the last team", "+1 and -1",
                String.format("%+d and %+d", harbourView.getGoalDifference(), waterhouse.getGoalDifference()));
    }
}
