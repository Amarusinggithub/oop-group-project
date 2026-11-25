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
    private FootballLeague league;
    private InputHelper input;

    public FixtureMenu(FootballLeague league, InputHelper input) {
        this.league = league;
        this.input = input;
    }

    public void run() {
        while (true) {
            String[] options = {"Create fixture", "Reschedule fixture", "Cancel fixture", "Display fixtures"};
            int choice = input.menu("Fixtures", "Back", options);
            if (choice == 0) {
                return;
            }
            try {
                switch (choice) {
                    case 1:
                        createFixture();
                        break;
                    case 2:
                        rescheduleFixture();
                        break;
                    case 3:
                        cancelFixture();
                        break;
                    case 4:
                        displayFixtures();
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

    // Lets the user pick one of the scheduled fixtures; returns null for Back
    static Fixture pickScheduled(FootballLeague league, InputHelper input) {
        return input.pickFixture("Scheduled fixtures", league.getFixtures(FixtureStatus.SCHEDULED));
    }

    private void createFixture() throws LeagueException {
        // Every team is listed, with its status, so the user sees why a pending or suspended team is refused
        FootballTeam home = input.pickTeam("Home team", league.getTeams());
        if (home == null) {
            return;
        }
        FootballTeam away = input.pickTeam("Away team", league.getTeams());
        if (away == null) {
            return;
        }
        if (home == away) {
            System.out.println("Error: a team cannot play itself. Pick two different teams.");
            return;
        }
        Stadium stadium = input.pickStadium(league.getStadiums());
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
        System.out.println("Current: " + fixture.displayInfo());
        LocalDate date = input.readOptionalDate("New date", "no change");
        LocalTime kickOff = input.readOptionalTime("New kick-off time", "no change");
        System.out.println("Pick a new stadium, or 0 to keep " + fixture.getStadium().getName() + ".");
        Stadium stadium = input.pickStadium(league.getStadiums());
        // Anything left blank keeps its current value
        if (stadium == null) {
            stadium = fixture.getStadium();
        }
        if (date == null) {
            date = fixture.getDate();
        }
        if (kickOff == null) {
            kickOff = fixture.getKickOff();
        }
        league.rescheduleFixture(fixture, stadium, date, kickOff);
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
