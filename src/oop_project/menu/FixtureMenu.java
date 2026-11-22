/**
 * Author: Amar Campbell - 2501977
 * Date: 11/21/2026
 * Description: This class is the Fixtures menu. It creates fixtures, reschedules and cancels
 * scheduled ones, and lists every fixture. The league checks team eligibility and clashes, and
 * any rule that is broken is reported here as a message.
 */

package oop_project.menu;

import oop_project.entities.Fixture;
import oop_project.entities.FootballLeague;
import oop_project.entities.FootballTeam;
import oop_project.entities.Stadium;
import oop_project.enums.FixtureStatus;
import oop_project.exceptions.LeagueException;

import java.time.LocalDate;
import java.time.LocalTime;

public class FixtureMenu {
    private final FootballLeague league;
    private final InputHelper input;

    public FixtureMenu(FootballLeague league, InputHelper input) {
        this.league = league;
        this.input = input;
    }

    public void run() {
        while (true) {
            int choice = input.menu("Fixtures", "Back",
                    "Create fixture", "Reschedule fixture", "Cancel fixture", "Display fixtures");
            if (choice == 0) {
                return;
            }
            try {
                switch (choice) {
                    case 1 -> createFixture();
                    case 2 -> rescheduleFixture();
                    case 3 -> cancelFixture();
                    case 4 -> displayFixtures();
                    default -> System.out.println("Please choose an option from the menu.");
                }
            } catch (LeagueException | IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // How a fixture is shown in every numbered list
    static String label(Fixture fixture) {
        return fixture.getFixtureId() + "  " + fixture + " at " + fixture.getStadium().getName()
                + " [" + fixture.getStatus().getStatus() + "]";
    }

    static String teamLabel(FootballTeam team) {
        return team.getRegistrationNo() + "  " + team.getName() + " (" + team.getStatus().getStatus() + ")";
    }

    // Lets the user pick one of the scheduled fixtures; returns null for Back
    static Fixture pickScheduled(FootballLeague league, InputHelper input) {
        return input.pick("Scheduled fixtures", league.getFixtures(FixtureStatus.SCHEDULED), FixtureMenu::label);
    }

    private void createFixture() throws LeagueException {
        // Every team is listed, with its status, so the user sees why a pending or suspended team is refused
        FootballTeam home = input.pick("Home team", league.getTeams(), FixtureMenu::teamLabel);
        if (home == null) {
            return;
        }
        FootballTeam away = input.pick("Away team", league.getTeams(), FixtureMenu::teamLabel);
        if (away == null) {
            return;
        }
        if (home == away) {
            System.out.println("Error: a team cannot play itself. Pick two different teams.");
            return;
        }
        Stadium stadium = input.pick("Stadium", league.getStadiums(), s -> s.getName() + " (" + s.getFieldCondition().getCondition() + ")");
        if (stadium == null) {
            return;
        }
        LocalDate date = input.readDate("Date");
        LocalTime kickOff = input.readTime("Kick-off time");
        Fixture fixture = league.createFixture(home, away, stadium, date, kickOff);
        System.out.println("Fixture " + fixture.getFixtureId() + " created: " + fixture + " at " + stadium.getName());
        if (!stadium.getFieldCondition().isPlayable()) {
            System.out.println("Note: " + stadium.getName() + "'s field is unplayable; update it before match day.");
        }
    }

    private void rescheduleFixture() throws LeagueException {
        Fixture fixture = pickScheduled(league, input);
        if (fixture == null) {
            return;
        }
        System.out.println("Current: " + label(fixture));
        LocalDate date = input.readOptionalDate("New date", "no change");
        LocalTime kickOff = input.readOptionalTime("New kick-off time", "no change");
        System.out.println("Pick a new stadium, or 0 to keep " + fixture.getStadium().getName() + ".");
        Stadium stadium = input.pick("Stadium", league.getStadiums(), Stadium::getName);
        league.rescheduleFixture(fixture,
                stadium == null ? fixture.getStadium() : stadium,
                date == null ? fixture.getDate() : date,
                kickOff == null ? fixture.getKickOff() : kickOff);
        System.out.println("Fixture " + fixture.getFixtureId() + " rescheduled: " + fixture + " at " + fixture.getStadium().getName());
    }

    private void cancelFixture() {
        Fixture fixture = pickScheduled(league, input);
        if (fixture == null) {
            return;
        }
        if (!fixture.getTickets().isEmpty()) {
            System.out.println("Note: " + fixture.getTickets().size() + " ticket(s) have been sold for this fixture.");
        }
        if (!input.confirm("Cancel " + fixture.getFixtureId() + " (" + fixture + ")?")) {
            System.out.println("Nothing was changed.");
            return;
        }
        league.cancelFixture(fixture);
        System.out.println("Fixture " + fixture.getFixtureId() + " cancelled. Its date and time are free again.");
    }

    private void displayFixtures() {
        System.out.print(league.fixturesReport());
    }
}
