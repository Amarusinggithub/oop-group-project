/**
 * Author: Amar Campbell
 * Date: 11/1/2026
 * Description: This class represents the football league. It coordinates the teams, stadiums,
 * match officials, spectators and fixtures, records results, works out the standings, searches
 * its records, produces reports, and saves and loads its data through LeagueFileManager.
 */

package oop_project.entities;

import oop_project.enums.FixtureStatus;
import oop_project.exceptions.CapacityExceededException;
import oop_project.exceptions.InvalidDataException;
import oop_project.exceptions.ResultNotRecordedException;
import oop_project.exceptions.ScheduleConflictException;
import oop_project.exceptions.TeamNotEligibleException;
import oop_project.persistence.LeagueFileManager;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FootballLeague {
    private String name;
    private int season;
    private LeagueCommissioner commissioner;
    private List<FootballTeam> teams;
    private List<Stadium> stadiums;
    private List<MatchOfficial> officials;
    private List<Fixture> fixtures;
    private List<Spectator> spectators;

    public FootballLeague(String name, int season) {
        setName(name);
        setSeason(season);
        this.teams = new ArrayList<>();
        this.stadiums = new ArrayList<>();
        this.officials = new ArrayList<>();
        this.fixtures = new ArrayList<>();
        this.spectators = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("League name is required.");
        }
        this.name = name;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        if (season < 1) {
            throw new IllegalArgumentException("Season must be a positive year.");
        }
        this.season = season;
    }

    public LeagueCommissioner getCommissioner() {
        return commissioner;
    }

    public void setCommissioner(LeagueCommissioner commissioner) {
        this.commissioner = commissioner;
    }

    public List<FootballTeam> getTeams() {
        return Collections.unmodifiableList(teams);
    }

    public List<Stadium> getStadiums() {
        return Collections.unmodifiableList(stadiums);
    }

    public List<MatchOfficial> getOfficials() {
        return Collections.unmodifiableList(officials);
    }

    public List<Fixture> getFixtures() {
        return Collections.unmodifiableList(fixtures);
    }

    public List<Spectator> getSpectators() {
        return Collections.unmodifiableList(spectators);
    }

    // New teams start as PENDING until the commissioner approves them
    public void registerTeam(FootballTeam team) throws InvalidDataException {
        if (findTeam(team.getRegistrationNo()) != null) {
            throw new InvalidDataException("A team with registration number " + team.getRegistrationNo() + " is already registered.");
        }
        teams.add(team);
    }

    // Finds a team by registration number; returns null when there is none
    public FootballTeam findTeam(String registrationNo) {
        for (FootballTeam team : teams) {
            if (team.getRegistrationNo().equalsIgnoreCase(registrationNo.trim())) {
                return team;
            }
        }
        return null;
    }

    // Finds a team by name or registration number, ignoring case and surrounding spaces; returns null when there is none
    public FootballTeam searchTeam(String nameOrRegistrationNo) {
        String wanted = nameOrRegistrationNo.trim();
        for (FootballTeam team : teams) {
            if (team.getName().equalsIgnoreCase(wanted) || team.getRegistrationNo().equalsIgnoreCase(wanted)) {
                return team;
            }
        }
        return null;
    }

    public void registerStadium(Stadium stadium) throws InvalidDataException {
        if (findStadium(stadium.getStadiumId()) != null) {
            throw new InvalidDataException("A stadium with ID " + stadium.getStadiumId() + " is already registered.");
        }
        stadiums.add(stadium);
    }

    // Returns null when no stadium has that ID
    public Stadium findStadium(String stadiumId) {
        for (Stadium stadium : stadiums) {
            if (stadium.getStadiumId().equalsIgnoreCase(stadiumId.trim())) {
                return stadium;
            }
        }
        return null;
    }

    public void registerOfficial(MatchOfficial official) throws InvalidDataException {
        if (findOfficial(official.getId()) != null) {
            throw new InvalidDataException("An official with ID " + official.getId() + " is already registered.");
        }
        officials.add(official);
    }

    // Returns null when no official has that ID
    public MatchOfficial findOfficial(String personId) {
        for (MatchOfficial official : officials) {
            if (official.getId().equalsIgnoreCase(personId.trim())) {
                return official;
            }
        }
        return null;
    }

    public void registerSpectator(Spectator spectator) throws InvalidDataException {
        if (findSpectator(spectator.getId()) != null) {
            throw new InvalidDataException("A spectator with ID " + spectator.getId() + " is already on record.");
        }
        spectators.add(spectator);
    }

    // Returns null when no spectator has that ID
    public Spectator findSpectator(String personId) {
        for (Spectator spectator : spectators) {
            if (spectator.getId().equalsIgnoreCase(personId.trim())) {
                return spectator;
            }
        }
        return null;
    }

    // Finds a player in any team by ID; returns null when there is none
    public Player findPlayer(String personId) {
        for (FootballTeam team : teams) {
            Player player = team.findPlayer(personId);
            if (player != null) {
                return player;
            }
        }
        return null;
    }

    // Returns the team whose squad holds the player, or null
    public FootballTeam findTeamOf(Player player) {
        for (FootballTeam team : teams) {
            if (team.getPlayers().contains(player)) {
                return team;
            }
        }
        return null;
    }

    // Players whose ID matches, or whose name contains the text, ignoring case
    public List<Player> searchPlayers(String text) {
        String wanted = text.trim().toLowerCase();
        List<Player> matches = new ArrayList<>();
        for (FootballTeam team : teams) {
            for (Player player : team.getPlayers()) {
                if (player.getId().equalsIgnoreCase(wanted) || player.getName().toLowerCase().contains(wanted)) {
                    matches.add(player);
                }
            }
        }
        return matches;
    }

    // Finds a coach in any team by ID; returns null when there is none
    public Coach findCoach(String personId) {
        for (FootballTeam team : teams) {
            Coach coach = team.findCoach(personId);
            if (coach != null) {
                return coach;
            }
        }
        return null;
    }

    // Returns null when no fixture has that ID
    public Fixture findFixture(String fixtureId) {
        for (Fixture fixture : fixtures) {
            if (fixture.getFixtureId().equalsIgnoreCase(fixtureId.trim())) {
                return fixture;
            }
        }
        return null;
    }

    public List<Fixture> findFixtures(LocalDate date) {
        List<Fixture> matches = new ArrayList<>();
        for (Fixture fixture : fixtures) {
            if (fixture.getDate().equals(date)) {
                matches.add(fixture);
            }
        }
        return matches;
    }

    public List<Fixture> findFixtures(FootballTeam team) {
        List<Fixture> matches = new ArrayList<>();
        for (Fixture fixture : fixtures) {
            if (fixture.involves(team)) {
                matches.add(fixture);
            }
        }
        return matches;
    }

    public List<Fixture> getFixtures(FixtureStatus status) {
        List<Fixture> matches = new ArrayList<>();
        for (Fixture fixture : fixtures) {
            if (fixture.getStatus() == status) {
                matches.add(fixture);
            }
        }
        return matches;
    }

    public Fixture createFixture(FootballTeam home, FootballTeam away, Stadium stadium, LocalDate date, LocalTime kickOff)
            throws TeamNotEligibleException, ScheduleConflictException {
        if (!teams.contains(home) || !teams.contains(away)) {
            throw new IllegalArgumentException("Both teams must be registered in " + name + ".");
        }
        if (!stadiums.contains(stadium)) {
            throw new IllegalArgumentException(stadium.getName() + " is not registered in " + name + ".");
        }
        checkEligible(home);
        checkEligible(away);
        String fixtureId = String.format("FX%03d", fixtures.size() + 1);
        Fixture fixture = new Fixture(fixtureId, home, away, stadium, date, kickOff);
        checkForClashes(fixture, home, away, stadium, date, kickOff);
        stadium.assignTeams(home, away);
        fixtures.add(fixture);
        return fixture;
    }

    // SRS rule: an unregistered or suspended team cannot be assigned to a fixture
    private void checkEligible(FootballTeam team) throws TeamNotEligibleException {
        if (!team.isEligible()) {
            throw new TeamNotEligibleException(team.getName() + " is " + team.getStatus().getStatus().toLowerCase()
                    + " and cannot be assigned to a fixture.");
        }
    }

    // Adds a fixture read from the data file (see Fixture.restoreState). Used by LeagueFileManager when loading.
    public void restoreFixture(Fixture fixture) {
        if (findFixture(fixture.getFixtureId()) != null) {
            throw new IllegalArgumentException("Fixture " + fixture.getFixtureId() + " is already recorded.");
        }
        fixtures.add(fixture);
    }

    // Moves a scheduled fixture to a new stadium, date or kick-off time, checking the same rules as createFixture
    public void rescheduleFixture(Fixture fixture, Stadium stadium, LocalDate date, LocalTime kickOff)
            throws ScheduleConflictException, CapacityExceededException {
        if (fixture.getStatus() != FixtureStatus.SCHEDULED) {
            throw new IllegalStateException("Fixture " + fixture.getFixtureId() + " is " + fixture.getStatus().getStatus().toLowerCase()
                    + " and can no longer be changed.");
        }
        if (!stadiums.contains(stadium)) {
            throw new IllegalArgumentException(stadium.getName() + " is not registered in " + name + ".");
        }
        checkForClashes(fixture, fixture.getHomeTeam(), fixture.getAwayTeam(), stadium, date, kickOff);
        // SRS rule: a match official cannot be assigned to conflicting fixtures
        for (MatchOfficial official : fixture.getOfficials()) {
            for (Fixture other : official.getAssignedFixtures()) {
                if (other != fixture && isBookedAt(other, date, kickOff)) {
                    throw new ScheduleConflictException(official.getName() + " is already officiating " + other + ".");
                }
            }
        }
        // SRS rule: ticket sales cannot exceed the stadium's seating capacity
        if (fixture.getTickets().size() > stadium.getSeatingCapacity()) {
            throw new CapacityExceededException(fixture.getTickets().size() + " tickets are already sold, but "
                    + stadium.getName() + " only seats " + stadium.getSeatingCapacity() + ".");
        }
        fixture.setStadium(stadium);
        fixture.setDate(date);
        fixture.setKickOff(kickOff);
        stadium.assignTeams(fixture.getHomeTeam(), fixture.getAwayTeam());
    }

    public void cancelFixture(Fixture fixture) {
        fixture.cancel();
    }

    // Throws when either team, or the stadium, already has another fixture at that date and time
    private void checkForClashes(Fixture fixture, FootballTeam home, FootballTeam away, Stadium stadium,
                                 LocalDate date, LocalTime kickOff) throws ScheduleConflictException {
        for (Fixture other : fixtures) {
            if (other == fixture || !isBookedAt(other, date, kickOff)) {
                continue;
            }
            // SRS rule: a team cannot participate in two fixtures scheduled for the same day and time
            if (other.involves(home)) {
                throw new ScheduleConflictException(home.getName() + " already plays in " + other + ".");
            }
            if (other.involves(away)) {
                throw new ScheduleConflictException(away.getName() + " already plays in " + other + ".");
            }
            // SRS rule: a stadium cannot host two fixtures at the same date and time
            if (other.getStadium() == stadium) {
                throw new ScheduleConflictException(stadium.getName() + " is already hosting " + other + ".");
            }
        }
    }

    // A cancelled fixture no longer books its slot
    private static boolean isBookedAt(Fixture fixture, LocalDate date, LocalTime kickOff) {
        return fixture.getStatus() != FixtureStatus.CANCELLED
                && fixture.getDate().equals(date) && fixture.getKickOff().equals(kickOff);
    }

    // Records the score (attendance is the tickets sold) and completes the fixture
    public void recordResult(Fixture fixture, int homeGoals, int awayGoals) throws InvalidDataException, ResultNotRecordedException {
        fixture.recordResult(homeGoals, awayGoals);
        fixture.complete();
    }

    // Ranked by points, then goal difference, then goals scored, then name.
    // Uses a bubble sort: neighbours in the wrong order are swapped until the list is in order.
    public List<FootballTeam> getStandings() {
        List<FootballTeam> standings = new ArrayList<>(teams);
        for (int pass = 0; pass < standings.size() - 1; pass++) {
            for (int i = 0; i < standings.size() - 1 - pass; i++) {
                FootballTeam first = standings.get(i);
                FootballTeam second = standings.get(i + 1);
                if (ranksAbove(second, first)) {
                    standings.set(i, second);
                    standings.set(i + 1, first);
                }
            }
        }
        return standings;
    }

    // True when team a should be above team b in the league table
    private boolean ranksAbove(FootballTeam a, FootballTeam b) {
        if (a.getPoints() != b.getPoints()) {
            return a.getPoints() > b.getPoints();
        }
        if (a.getGoalDifference() != b.getGoalDifference()) {
            return a.getGoalDifference() > b.getGoalDifference();
        }
        if (a.getGoalsFor() != b.getGoalsFor()) {
            return a.getGoalsFor() > b.getGoalsFor();
        }
        return a.getName().compareToIgnoreCase(b.getName()) < 0;
    }

    // Fixtures in date and kick-off order (bubble sort)
    public List<Fixture> getFixturesByDate() {
        List<Fixture> sorted = new ArrayList<>(fixtures);
        for (int pass = 0; pass < sorted.size() - 1; pass++) {
            for (int i = 0; i < sorted.size() - 1 - pass; i++) {
                Fixture first = sorted.get(i);
                Fixture second = sorted.get(i + 1);
                if (isEarlier(second, first)) {
                    sorted.set(i, second);
                    sorted.set(i + 1, first);
                }
            }
        }
        return sorted;
    }

    // True when fixture a kicks off before fixture b
    private boolean isEarlier(Fixture a, Fixture b) {
        if (!a.getDate().equals(b.getDate())) {
            return a.getDate().isBefore(b.getDate());
        }
        return a.getKickOff().isBefore(b.getKickOff());
    }

    // RPT-01: the league table
    public String standingsReport() {
        if (teams.isEmpty()) {
            return "There is nothing to report yet: no teams are registered.\n";
        }
        StringBuilder report = new StringBuilder();
        report.append("LEAGUE STANDINGS - ").append(name).append(", Season ").append(season).append("\n");
        report.append(String.format("%-4s %-24s %3s %3s %3s %3s %4s %4s %4s %4s%n", "Pos", "Team", "P", "W", "D", "L", "GF", "GA", "GD", "Pts"));
        int position = 1;
        for (FootballTeam team : getStandings()) {
            report.append(String.format("%-4d %-24s %3d %3d %3d %3d %4d %4d %+4d %4d%n", position++, team.getName(),
                    team.getPlayed(), team.getWon(), team.getDrawn(), team.getLost(),
                    team.getGoalsFor(), team.getGoalsAgainst(), team.getGoalDifference(), team.getPoints()));
        }
        return report.toString();
    }

    // RPT-02: every fixture with its result or status
    public String fixturesReport() {
        if (fixtures.isEmpty()) {
            return "There is nothing to report yet: no fixtures have been created.\n";
        }
        StringBuilder report = new StringBuilder();
        report.append("FIXTURES AND RESULTS\n");
        report.append(String.format("%-6s %-10s %-5s %-20s %-20s %-32s %-12s %7s%n",
                "ID", "Date", "Time", "Home", "Away", "Stadium", "Result", "Tickets"));
        for (Fixture fixture : getFixturesByDate()) {
            String outcome = fixture.getStatus().getStatus();
            if (fixture.getStatus() == FixtureStatus.COMPLETED) {
                outcome = fixture.getResult().getScoreLine();
            }
            report.append(String.format("%-6s %-10s %-5s %-20s %-20s %-32s %-12s %7d%n", fixture.getFixtureId(),
                    fixture.getDate(), fixture.getKickOff(), fixture.getHomeTeam().getName(), fixture.getAwayTeam().getName(),
                    fixture.getStadium().getName(), outcome, fixture.getTickets().size()));
        }
        return report.toString();
    }

    // RPT-03: paid shop sales for each stadium, with a league total
    public String shopSalesReport() {
        if (stadiums.isEmpty()) {
            return "There is nothing to report yet: no stadiums are registered.\n";
        }
        StringBuilder report = new StringBuilder();
        report.append("STADIUM SHOP SALES (incl. GCT)\n");
        report.append(String.format("%-34s %6s %14s%n", "Stadium", "Sales", "Total"));
        double totalSales = 0;
        int totalCount = 0;
        for (Stadium stadium : stadiums) {
            int count = 0;
            for (Transaction transaction : stadium.getShop().getTransactions()) {
                if (transaction.isPaid()) {
                    count++;
                }
            }
            double sales = stadium.getShop().getTotalSales();
            totalSales += sales;
            totalCount += count;
            report.append(String.format("%-34s %6d $%,13.2f%n", stadium.getName(), count, sales));
        }
        report.append(String.format("%-34s %6d $%,13.2f%n", "League total", totalCount, totalSales));
        return report.toString();
    }

    // The three league-wide reports together
    public String generateReports() {
        return standingsReport() + "\n" + fixturesReport() + "\n" + shopSalesReport();
    }

    // Writes every data file into the folder and returns the number of records saved
    public int saveData(String folder) throws IOException {
        return new LeagueFileManager(folder).save(this);
    }

    // Loads the data files from the folder into this league, which must be empty.
    // Returns a message for each line that could not be read; those lines are skipped.
    public List<String> loadData(String folder) throws IOException {
        if (!teams.isEmpty() || !stadiums.isEmpty() || !officials.isEmpty() || !fixtures.isEmpty()) {
            throw new IllegalStateException("Data can only be loaded into an empty league.");
        }
        return new LeagueFileManager(folder).load(this);
    }
}
