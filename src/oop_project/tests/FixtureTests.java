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

import java.time.LocalDate;
import java.time.LocalTime;

public class FixtureTests {
    private static final LocalDate MATCH_DAY = LocalDate.of(2026, 12, 5);
    private static final LocalTime THREE_PM = LocalTime.of(15, 0);

    public static void run() throws LeagueException {
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
        FootballTeam[] allTeams = {harbourView, waterhouse, cavalier, portmore, arnett, tivoli};
        for (FootballTeam team : allTeams) {
            league.registerTeam(team);
        }
        harbourView.setStatus(TeamStatus.REGISTERED);
        waterhouse.setStatus(TeamStatus.REGISTERED);
        cavalier.setStatus(TeamStatus.REGISTERED);
        portmore.setStatus(TeamStatus.REGISTERED);
        tivoli.setStatus(TeamStatus.SUSPENDED);
        league.registerStadium(national);
        league.registerStadium(spaulding);
        league.registerOfficial(referee);

        // T-08 (invalid): a pending team, and a suspended team
        try {
            league.createFixture(arnett, harbourView, national, MATCH_DAY, THREE_PM);
            Check.notThrown("T-08", "Create a fixture with a pending team (invalid)", "TeamNotEligibleException");
        } catch (Exception e) {
            Check.exception("T-08", "Create a fixture with a pending team (invalid)", "TeamNotEligibleException", e);
        }
        try {
            league.createFixture(harbourView, tivoli, national, MATCH_DAY, THREE_PM);
            Check.notThrown("T-08", "Create a fixture with a suspended team (invalid)", "TeamNotEligibleException");
        } catch (Exception e) {
            Check.exception("T-08", "Create a fixture with a suspended team (invalid)", "TeamNotEligibleException", e);
        }
        Check.equal("T-08", "No fixture was created", 0, league.getFixtures().size());

        // T-09 (normal): the first fixture gets ID FX001
        Fixture first = league.createFixture(harbourView, waterhouse, national, MATCH_DAY, THREE_PM);
        Check.equal("T-09", "Create a fixture between two registered teams (normal)", "FX001",
                first.getFixtureId());
        Check.equal("T-09", "Fixture is shown with teams, date and time",
                "Harbour View vs Waterhouse on 2026-12-05 at 15:00", String.valueOf(first));
        Check.equal("T-09", "Stadium records the teams of its current fixture", "[Harbour View, Waterhouse]",
                national.getAssignedTeams().toString());

        // T-10 (invalid): the same team at the same date and time, at another stadium
        try {
            league.createFixture(harbourView, cavalier, spaulding, MATCH_DAY, THREE_PM);
            Check.notThrown("T-10", "Same team, same date and time, another stadium (invalid)", "ScheduleConflictException");
        } catch (Exception e) {
            Check.exception("T-10", "Same team, same date and time, another stadium (invalid)", "ScheduleConflictException", e);
        }

        // T-11 (boundary): the same team one hour later is not a clash
        Fixture later = league.createFixture(harbourView, cavalier, spaulding, MATCH_DAY, LocalTime.of(16, 0));
        Check.equal("T-11", "Same team, same date, kick-off one hour later (boundary)", "FX002",
                later.getFixtureId());

        // T-12 (invalid): different teams, but the stadium is already booked
        try {
            league.createFixture(cavalier, portmore, national, MATCH_DAY, THREE_PM);
            Check.notThrown("T-12", "Different teams, same stadium, same date and time (invalid)", "ScheduleConflictException");
        } catch (Exception e) {
            Check.exception("T-12", "Different teams, same stadium, same date and time (invalid)", "ScheduleConflictException", e);
        }

        // T-13 (invalid): an official already booked at the same date and time
        Fixture sameSlot = league.createFixture(cavalier, portmore, spaulding, MATCH_DAY, THREE_PM);
        try {
            first.assignOfficial(referee);
            Check.completed("T-13", "Assign the referee to FX001 (set-up)");
        } catch (Exception e) {
            Check.unexpected("T-13", "Assign the referee to FX001 (set-up)", e);
        }
        try {
            sameSlot.assignOfficial(referee);
            Check.notThrown("T-13", "Assign an official already booked at the same date and time (invalid)", "ScheduleConflictException");
        } catch (Exception e) {
            Check.exception("T-13", "Assign an official already booked at the same date and time (invalid)", "ScheduleConflictException", e);
        }
        Check.equal("T-13", "The referee still has one fixture", 1, referee.getAssignedFixtures().size());
        try {
            later.assignOfficial(referee);
            Check.completed("T-13", "The same referee can take the 16:00 fixture (boundary)");
        } catch (Exception e) {
            Check.unexpected("T-13", "The same referee can take the 16:00 fixture (boundary)", e);
        }

        // A cancelled fixture frees its slot (SRS section 4)
        try {
            league.cancelFixture(first);
            league.createFixture(waterhouse, harbourView, national, MATCH_DAY, LocalTime.of(18, 0));
            league.rescheduleFixture(league.findFixture("FX004"), national, MATCH_DAY, THREE_PM);
            Check.completed("T-12", "After cancelling FX001, its stadium slot can be booked again (boundary)");
        } catch (Exception e) {
            Check.unexpected("T-12", "After cancelling FX001, its stadium slot can be booked again (boundary)", e);
        }
        try {
            league.createFixture(cavalier, cavalier, national, MATCH_DAY, LocalTime.of(20, 0));
            Check.notThrown("T-09", "A team cannot play itself (invalid)", "IllegalArgumentException");
        } catch (Exception e) {
            Check.exception("T-09", "A team cannot play itself (invalid)", "IllegalArgumentException", e);
        }
    }
}
