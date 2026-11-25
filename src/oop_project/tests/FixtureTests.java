/**
 * Author: Amar Campbell - 2501977
 * Date: 11/21/2026
 * Description: Tests T-08 to T-13 from the SRS test plan: only registered teams can play, fixture
 * IDs, team and stadium clashes, and match officials who are already booked.
 */

package oop_project.tests;

import oop_project.entities.Fixture;
import oop_project.entities.FootballLeague;
import oop_project.entities.FootballTeam;
import oop_project.entities.MatchOfficial;
import oop_project.entities.Stadium;
import oop_project.enums.TeamStatus;
import oop_project.exceptions.LeagueException;
import oop_project.exceptions.ScheduleConflictException;
import oop_project.exceptions.TeamNotEligibleException;

import java.time.LocalDate;
import java.time.LocalTime;

public class FixtureTests {
    private static final LocalDate MATCH_DAY = LocalDate.of(2026, 12, 5);
    private static final LocalTime THREE_PM = LocalTime.of(15, 0);

    public static void run() {
        FootballLeague league = new FootballLeague("Test League", 2026);
        FootballTeam harbourView = new FootballTeam("T1", "Harbour View", "Kingston", 25);
        FootballTeam waterhouse = new FootballTeam("T2", "Waterhouse", "Kingston", 25);
        FootballTeam cavalier = new FootballTeam("T3", "Cavalier", "St. Andrew", 25);
        FootballTeam portmore = new FootballTeam("T4", "Portmore United", "St. Catherine", 25);
        FootballTeam arnett = new FootballTeam("T5", "Arnett Gardens", "Kingston", 25); // stays pending
        FootballTeam tivoli = new FootballTeam("T6", "Tivoli Gardens", "Kingston", 25); // suspended
        Stadium national = new Stadium("ST1", "National Stadium", "Kingston", 35000, 1500);
        Stadium spaulding = new Stadium("ST2", "Anthony Spaulding Sports Complex", "Kingston", 7000, 1000);
        MatchOfficial referee = new MatchOfficial("MO1", "Kemar Whyte", 38, "Male", "Referee", "FIFA", true);
        try {
            for (FootballTeam team : new FootballTeam[]{harbourView, waterhouse, cavalier, portmore, arnett, tivoli}) {
                league.registerTeam(team);
            }
            for (FootballTeam team : new FootballTeam[]{harbourView, waterhouse, cavalier, portmore}) {
                team.setStatus(TeamStatus.REGISTERED);
            }
            tivoli.setStatus(TeamStatus.SUSPENDED);
            league.registerStadium(national);
            league.registerStadium(spaulding);
            league.registerOfficial(referee);
        } catch (LeagueException e) {
            Check.equal("T-08", "Set up the fixture tests", "set up", e.getMessage());
            return;
        }

        // T-08 (invalid): a pending team, and a suspended team
        Check.throwsException("T-08", "Create a fixture with a pending team (invalid)", TeamNotEligibleException.class,
                () -> league.createFixture(arnett, harbourView, national, MATCH_DAY, THREE_PM));
        Check.throwsException("T-08", "Create a fixture with a suspended team (invalid)", TeamNotEligibleException.class,
                () -> league.createFixture(harbourView, tivoli, national, MATCH_DAY, THREE_PM));
        Check.equal("T-08", "No fixture was created", 0, league.getFixtures().size());

        // T-09 (normal): the first fixture gets ID FX001
        Fixture first = create(league, harbourView, waterhouse, national, THREE_PM);
        Check.equal("T-09", "Create a fixture between two registered teams (normal)", "FX001",
                first == null ? "not created" : first.getFixtureId());
        Check.equal("T-09", "Fixture is shown with teams, date and time",
                "Harbour View vs Waterhouse on 2026-12-05 at 15:00", String.valueOf(first));
        Check.equal("T-09", "Stadium records the teams of its current fixture", "[Harbour View, Waterhouse]",
                national.getAssignedTeams().toString());

        // T-10 (invalid): the same team at the same date and time, at another stadium
        Check.throwsException("T-10", "Same team, same date and time, another stadium (invalid)", ScheduleConflictException.class,
                () -> league.createFixture(harbourView, cavalier, spaulding, MATCH_DAY, THREE_PM));

        // T-11 (boundary): the same team one hour later is not a clash
        Fixture later = create(league, harbourView, cavalier, spaulding, LocalTime.of(16, 0));
        Check.equal("T-11", "Same team, same date, kick-off one hour later (boundary)", "FX002",
                later == null ? "not created" : later.getFixtureId());

        // T-12 (invalid): different teams, but the stadium is already booked
        Check.throwsException("T-12", "Different teams, same stadium, same date and time (invalid)", ScheduleConflictException.class,
                () -> league.createFixture(cavalier, portmore, national, MATCH_DAY, THREE_PM));

        // T-13 (invalid): an official already booked at the same date and time
        Fixture sameSlot = create(league, cavalier, portmore, spaulding, THREE_PM);
        Check.noException("T-13", "Assign the referee to FX001 (set-up)", () -> first.assignOfficial(referee));
        Check.throwsException("T-13", "Assign an official already booked at the same date and time (invalid)",
                ScheduleConflictException.class, () -> sameSlot.assignOfficial(referee));
        Check.equal("T-13", "The referee still has one fixture", 1, referee.getAssignedFixtures().size());
        Check.noException("T-13", "The same referee can take the 16:00 fixture (boundary)", () -> later.assignOfficial(referee));

        // A cancelled fixture frees its slot (SRS section 4)
        Check.noException("T-12", "After cancelling FX001, its stadium slot can be booked again (boundary)", () -> {
            league.cancelFixture(first);
            league.createFixture(waterhouse, harbourView, national, MATCH_DAY, LocalTime.of(18, 0));
            league.rescheduleFixture(league.findFixture("FX004"), national, MATCH_DAY, THREE_PM);
        });
        Check.throwsException("T-09", "A team cannot play itself (invalid)", IllegalArgumentException.class,
                () -> league.createFixture(cavalier, cavalier, national, MATCH_DAY, LocalTime.of(20, 0)));
    }

    private static Fixture create(FootballLeague league, FootballTeam home, FootballTeam away, Stadium stadium, LocalTime kickOff) {
        try {
            return league.createFixture(home, away, stadium, MATCH_DAY, kickOff);
        } catch (LeagueException e) {
            return null;
        }
    }
}
