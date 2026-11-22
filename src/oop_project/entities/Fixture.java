/**
 * Author: Amar Campbell - 2501977
 * Date: 11/21/2026
 * Description: This class represents a fixture: a match between a home team and an away team
 * at a stadium on a date and kick-off time. A fixture moves from SCHEDULED to COMPLETED or
 * CANCELLED. While it is scheduled it can be rescheduled, given match officials and sell
 * tickets; it can only be completed once a valid result has been recorded.
 */

package oop_project.entities;

import oop_project.enums.FixtureStatus;
import oop_project.enums.TicketCategory;
import oop_project.exceptions.CapacityExceededException;
import oop_project.exceptions.InvalidDataException;
import oop_project.exceptions.ResultNotRecordedException;
import oop_project.exceptions.ScheduleConflictException;
import oop_project.exceptions.TeamNotEligibleException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Fixture {
    private String fixtureId;
    private FootballTeam homeTeam;
    private FootballTeam awayTeam;
    private Stadium stadium;
    private LocalDate date;
    private LocalTime kickOff;
    private FixtureStatus status;
    private List<MatchOfficial> officials;
    private List<Tickets> tickets;
    private MatchResult result;

    // FootballLeague.createFixture creates fixtures, gives them their IDs and checks for clashes
    public Fixture(String fixtureId, FootballTeam homeTeam, FootballTeam awayTeam, Stadium stadium,
                   LocalDate date, LocalTime kickOff) throws TeamNotEligibleException {
        this(fixtureId, homeTeam, awayTeam, stadium, date, kickOff, true);
    }

    private Fixture(String fixtureId, FootballTeam homeTeam, FootballTeam awayTeam, Stadium stadium,
                    LocalDate date, LocalTime kickOff, boolean checkEligibility) throws TeamNotEligibleException {
        if (fixtureId == null || fixtureId.isBlank()) {
            throw new IllegalArgumentException("Fixture ID is required.");
        }
        if (homeTeam == null || awayTeam == null) {
            throw new IllegalArgumentException("A fixture needs a home team and an away team.");
        }
        if (homeTeam == awayTeam) {
            throw new IllegalArgumentException("A team cannot play itself.");
        }
        if (stadium == null) {
            throw new IllegalArgumentException("A fixture needs a stadium.");
        }
        if (date == null || kickOff == null) {
            throw new IllegalArgumentException("A fixture needs a date and a kick-off time.");
        }
        if (checkEligibility) {
            // SRS rule: an unregistered or suspended team cannot be assigned to a fixture
            for (FootballTeam team : new FootballTeam[]{homeTeam, awayTeam}) {
                if (!team.isEligible()) {
                    throw new TeamNotEligibleException(team.getName() + " is " + team.getStatus().getStatus().toLowerCase()
                            + " and cannot be assigned to a fixture.");
                }
            }
        }
        this.fixtureId = fixtureId;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.stadium = stadium;
        this.date = date;
        this.kickOff = kickOff;
        this.status = FixtureStatus.SCHEDULED;
        this.officials = new ArrayList<>();
        this.tickets = new ArrayList<>();
    }

    // Rebuilds a fixture from saved data. The rules were checked when the fixture was first made,
    // so the teams' current status is not checked again. A completed fixture's result is added to
    // both teams' records, so the standings are rebuilt from the saved results.
    // Used by LeagueFileManager when loading; menus use FootballLeague.createFixture instead.
    public static Fixture restore(String fixtureId, FootballTeam homeTeam, FootballTeam awayTeam, Stadium stadium,
                                  LocalDate date, LocalTime kickOff, FixtureStatus status,
                                  List<MatchOfficial> officials, MatchResult result) {
        Fixture fixture;
        try {
            fixture = new Fixture(fixtureId, homeTeam, awayTeam, stadium, date, kickOff, false);
        } catch (TeamNotEligibleException e) {
            throw new IllegalStateException(e); // cannot happen: eligibility is not checked here
        }
        if (status == FixtureStatus.COMPLETED && result == null) {
            throw new IllegalArgumentException("Completed fixture " + fixtureId + " has no result.");
        }
        for (MatchOfficial official : officials) {
            fixture.officials.add(official);
            official.restoreAssignment(fixture);
        }
        fixture.result = result;
        if (status == FixtureStatus.COMPLETED) {
            fixture.updateTeamRecords();
        }
        fixture.status = status;
        return fixture;
    }

    public String getFixtureId() {
        return fixtureId;
    }

    public FootballTeam getHomeTeam() {
        return homeTeam;
    }

    public FootballTeam getAwayTeam() {
        return awayTeam;
    }

    public Stadium getStadium() {
        return stadium;
    }

    // Use FootballLeague.rescheduleFixture, which checks for clashes before changing the stadium, date or time
    public void setStadium(Stadium stadium) {
        requireScheduled();
        if (stadium == null) {
            throw new IllegalArgumentException("A fixture needs a stadium.");
        }
        this.stadium = stadium;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        requireScheduled();
        if (date == null) {
            throw new IllegalArgumentException("A fixture needs a date.");
        }
        this.date = date;
    }

    public LocalTime getKickOff() {
        return kickOff;
    }

    public void setKickOff(LocalTime kickOff) {
        requireScheduled();
        if (kickOff == null) {
            throw new IllegalArgumentException("A fixture needs a kick-off time.");
        }
        this.kickOff = kickOff;
    }

    public FixtureStatus getStatus() {
        return status;
    }

    public List<MatchOfficial> getOfficials() {
        return Collections.unmodifiableList(officials);
    }

    public List<Tickets> getTickets() {
        return Collections.unmodifiableList(tickets);
    }

    public MatchResult getResult() {
        return result;
    }

    // Two fixtures clash when both still count (neither is cancelled) and they share a date and kick-off time
    public boolean clashesWith(Fixture other) {
        return other != null && other != this
                && status != FixtureStatus.CANCELLED && other.status != FixtureStatus.CANCELLED
                && date.equals(other.date) && kickOff.equals(other.kickOff);
    }

    public boolean involves(FootballTeam team) {
        return homeTeam == team || awayTeam == team;
    }

    public void assignOfficial(MatchOfficial official) throws ScheduleConflictException {
        requireScheduled();
        if (official == null) {
            throw new IllegalArgumentException("Official is required.");
        }
        if (officials.contains(official)) {
            throw new ScheduleConflictException(official.getName() + " is already assigned to " + this + ".");
        }
        official.assignTo(this);
        officials.add(official);
    }

    public Tickets sellTicket(Spectator spectator, TicketCategory category) throws CapacityExceededException {
        requireScheduled();
        if (spectator == null) {
            throw new IllegalArgumentException("A ticket must have a holder.");
        }
        if (category == null) {
            throw new IllegalArgumentException("Ticket category is required.");
        }
        // SRS rule: ticket sales cannot exceed the stadium's seating capacity
        if (getSeatsRemaining() <= 0) {
            throw new CapacityExceededException(stadium.getName() + " is sold out for " + this + ".");
        }
        String ticketNo = fixtureId + "-T" + String.format("%04d", tickets.size() + 1);
        double basePrice = stadium.getTicketPrice() * category.getPriceMultiplier();
        Tickets ticket = new Tickets(ticketNo, this, spectator, category, basePrice);
        tickets.add(ticket);
        spectator.addTicket(ticket);
        return ticket;
    }

    // Adds a ticket from saved data. Used by LeagueFileManager when loading; menus use sellTicket instead.
    public Tickets restoreTicket(String ticketNo, Spectator holder, TicketCategory category, double basePrice) {
        Tickets ticket = new Tickets(ticketNo, this, holder, category, basePrice);
        tickets.add(ticket);
        holder.addTicket(ticket);
        return ticket;
    }

    public int getSeatsRemaining() {
        return Math.max(0, stadium.getSeatingCapacity() - tickets.size());
    }

    // Ticket revenue including GCT
    public double getTicketRevenue() {
        double revenue = 0;
        for (Tickets ticket : tickets) {
            revenue += ticket.getPriceWithTax();
        }
        return Math.round(revenue * 100) / 100.0;
    }

    // Attendance is the number of tickets sold
    public void recordResult(int homeGoals, int awayGoals) throws InvalidDataException {
        requireScheduled();
        result = new MatchResult(homeGoals, awayGoals, tickets.size());
    }

    // Returns null when there is no result yet or the match was drawn
    public FootballTeam getWinner() {
        if (result == null || result.isDraw()) {
            return null;
        }
        return result.isHomeWin() ? homeTeam : awayTeam;
    }

    public void complete() throws ResultNotRecordedException {
        requireScheduled();
        // SRS rule: a fixture cannot be completed until a valid result has been recorded
        if (result == null) {
            throw new ResultNotRecordedException("Record a result for " + this + " before completing it.");
        }
        updateTeamRecords();
        status = FixtureStatus.COMPLETED;
    }

    public void cancel() {
        requireScheduled();
        status = FixtureStatus.CANCELLED;
    }

    private void updateTeamRecords() {
        homeTeam.recordMatch(result.getHomeGoals(), result.getAwayGoals());
        awayTeam.recordMatch(result.getAwayGoals(), result.getHomeGoals());
    }

    private void requireScheduled() {
        if (status != FixtureStatus.SCHEDULED) {
            throw new IllegalStateException("Fixture " + fixtureId + " is " + status.getStatus().toLowerCase()
                    + " and can no longer be changed.");
        }
    }

    public String displayInfo() {
        String outcome = result == null ? status.getStatus() : status.getStatus() + " " + result.getScoreLine();
        return fixtureId + ": " + this + " at " + stadium.getName() + " - " + outcome
                + ", Officials: " + officials.size() + ", Tickets sold: " + tickets.size();
    }

    @Override
    public String toString() {
        return homeTeam.getName() + " vs " + awayTeam.getName() + " on " + date + " at " + kickOff;
    }
}
