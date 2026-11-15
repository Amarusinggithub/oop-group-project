/**
 * Author: Amar Campbell
 * Date: 11/1/2026
 * Description: This class represents the football league. It coordinates the teams, stadiums,
 * match officials and fixtures, records results, works out the standings and produces reports.
 */

package oop_project.entities;

import oop_project.exceptions.InvalidDataException;
import oop_project.exceptions.ResultNotRecordedException;
import oop_project.exceptions.ScheduleConflictException;
import oop_project.exceptions.TeamNotEligibleException;

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

    public FootballLeague(String name, int season) {
        setName(name);
        setSeason(season);
        this.teams = new ArrayList<>();
        this.stadiums = new ArrayList<>();
        this.officials = new ArrayList<>();
        this.fixtures = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
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

    // New teams start as PENDING until the commissioner approves them
    public void registerTeam(FootballTeam team) {
        for (FootballTeam existing : teams) {
            if (existing.getRegistrationNo().equalsIgnoreCase(team.getRegistrationNo())) {
                throw new IllegalArgumentException("A team with registration number " + team.getRegistrationNo() + " is already registered.");
            }
        }
        teams.add(team);
    }

    // Returns null when no team has that name
    public FootballTeam searchTeam(String name) {
        for (FootballTeam team : teams) {
            if (team.getName().equalsIgnoreCase(name.trim())) {
                return team;
            }
        }
        return null;
    }

    public void registerStadium(Stadium stadium) {
        for (Stadium existing : stadiums) {
            if (existing.getStadiumId().equalsIgnoreCase(stadium.getStadiumId())) {
                throw new IllegalArgumentException("A stadium with ID " + stadium.getStadiumId() + " is already registered.");
            }
        }
        stadiums.add(stadium);
    }

    public void registerOfficial(MatchOfficial official) {
        for (MatchOfficial existing : officials) {
            if (existing.getId().equals(official.getId())) {
                throw new IllegalArgumentException("An official with id " + official.getId() + " is already registered.");
            }
        }
        officials.add(official);
    }

    public Fixture createFixture(FootballTeam home, FootballTeam away, Stadium stadium, LocalDate date, LocalTime kickOff)
            throws TeamNotEligibleException, ScheduleConflictException {
        if (!teams.contains(home) || !teams.contains(away)) {
            throw new IllegalArgumentException("Both teams must be registered in " + name + ".");
        }
        if (!stadiums.contains(stadium)) {
            throw new IllegalArgumentException(stadium.getName() + " is not registered in " + name + ".");
        }
        String fixtureId = String.format("FX%03d", fixtures.size() + 1);
        Fixture fixture = new Fixture(fixtureId, home, away, stadium, date, kickOff);
        for (Fixture other : fixtures) {
            if (!other.clashesWith(fixture)) {
                continue;
            }
            // SRS rule: a team cannot participate in two fixtures scheduled for the same day and time
            for (FootballTeam team : new FootballTeam[]{home, away}) {
                if (other.getHomeTeam() == team || other.getAwayTeam() == team) {
                    throw new ScheduleConflictException(team.getName() + " already plays in " + other + ".");
                }
            }
            if (other.getStadium() == stadium) {
                throw new ScheduleConflictException(stadium.getName() + " is already hosting " + other + ".");
            }
        }
        stadium.assignTeams(home, away);
        fixtures.add(fixture);
        return fixture;
    }

    public void recordResult(Fixture fixture, int homeGoals, int awayGoals) throws InvalidDataException, ResultNotRecordedException {
        fixture.recordResult(homeGoals, awayGoals);
        fixture.complete();
    }

    // Ranked by points, then goal difference, then goals scored, then name
    public List<FootballTeam> getStandings() {
        List<FootballTeam> standings = new ArrayList<>(teams);
        standings.sort((a, b) -> {
            if (a.getPoints() != b.getPoints()) {
                return b.getPoints() - a.getPoints();
            }
            if (a.getGoalDifference() != b.getGoalDifference()) {
                return b.getGoalDifference() - a.getGoalDifference();
            }
            if (a.getGoalsFor() != b.getGoalsFor()) {
                return b.getGoalsFor() - a.getGoalsFor();
            }
            return a.getName().compareToIgnoreCase(b.getName());
        });
        return standings;
    }

    // Standings table, fixture list with results, and shop sales per stadium
    public String generateReports() {
        StringBuilder report = new StringBuilder();
        report.append(name).append(" - Season ").append(season).append("\n\n");

        report.append("LEAGUE STANDINGS\n");
        report.append(String.format("%-4s %-24s %3s %3s %3s %3s %4s %4s %4s %4s%n", "Pos", "Team", "P", "W", "D", "L", "GF", "GA", "GD", "Pts"));
        int position = 1;
        for (FootballTeam team : getStandings()) {
            report.append(String.format("%-4d %-24s %3d %3d %3d %3d %4d %4d %4d %4d%n", position++, team.getName(),
                    team.getPlayed(), team.getWon(), team.getDrawn(), team.getLost(),
                    team.getGoalsFor(), team.getGoalsAgainst(), team.getGoalDifference(), team.getPoints()));
        }

        report.append("\nFIXTURES\n");
        for (Fixture fixture : fixtures) {
            String outcome = fixture.getResult() == null ? fixture.getStatus().getStatus() : fixture.getResult().getScoreLine();
            report.append(String.format("%-6s %-60s %-12s %s%n", fixture.getFixtureId(), fixture, outcome,
                    "Tickets sold: " + fixture.getTickets().size()));
        }

        report.append("\nSTADIUM SHOP SALES\n");
        double totalSales = 0;
        for (Stadium stadium : stadiums) {
            double sales = stadium.getShop().getTotalSales();
            totalSales += sales;
            report.append(String.format("%-30s $%,12.2f%n", stadium.getName(), sales));
        }
        report.append(String.format("%-30s $%,12.2f%n", "Total", totalSales));
        return report.toString();
    }

    public void saveData() {
        throw new UnsupportedOperationException("Saving league data to files is not implemented yet.");
    }

    public void loadData() {
        throw new UnsupportedOperationException("Loading league data from files is not implemented yet.");
    }
}
