/**
 * Author: Amar Campbell - 2501977
 * Date: 11/21/2026
 * Description: This class represents a football team in the league. It holds the team's
 * registration details and status, manages its squad of players and its coaching staff,
 * and keeps the team's match record and points.
 */

package oop_project.entities;

import oop_project.enums.TeamStatus;
import oop_project.exceptions.CapacityExceededException;
import oop_project.exceptions.InvalidDataException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FootballTeam {
    public static final int POINTS_FOR_WIN = 3;
    public static final int POINTS_FOR_DRAW = 1;

    private String registrationNo;
    private String name;
    private String parish;
    private int maxSquadSize;
    private TeamStatus status;
    private List<Player> players;
    private List<Coach> coaches;
    private int played;
    private int won;
    private int drawn;
    private int lost;
    private int goalsFor;
    private int goalsAgainst;

    // New teams start as PENDING until the commissioner approves them
    public FootballTeam(String registrationNo, String name, String parish, int maxSquadSize) {
        if (registrationNo == null || registrationNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration number is required.");
        }
        this.registrationNo = registrationNo.trim();
        this.players = new ArrayList<>();
        this.coaches = new ArrayList<>();
        setName(name);
        setParish(parish);
        setMaxSquadSize(maxSquadSize);
        this.status = TeamStatus.PENDING;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Team name is required.");
        }
        this.name = name.trim();
    }

    public String getParish() {
        return parish;
    }

    public void setParish(String parish) {
        if (parish == null || parish.trim().isEmpty()) {
            throw new IllegalArgumentException("Parish is required.");
        }
        this.parish = parish.trim();
    }

    public int getMaxSquadSize() {
        return maxSquadSize;
    }

    public void setMaxSquadSize(int maxSquadSize) {
        if (maxSquadSize < 1) {
            throw new IllegalArgumentException("Maximum squad size must be at least 1.");
        }
        if (maxSquadSize < players.size()) {
            throw new IllegalArgumentException("Maximum squad size cannot be below the current squad of " + players.size() + ".");
        }
        this.maxSquadSize = maxSquadSize;
    }

    public TeamStatus getStatus() {
        return status;
    }

    // The commissioner normally changes the status through approveTeam and suspendTeam
    public void setStatus(TeamStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Team status is required.");
        }
        this.status = status;
    }

    // SRS rule: only a registered team can be assigned to a fixture
    public boolean isEligible() {
        return status == TeamStatus.REGISTERED;
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public int getSquadSize() {
        return players.size();
    }

    public void addPlayer(Player player) throws CapacityExceededException, InvalidDataException {
        if (player == null) {
            throw new IllegalArgumentException("Player is required.");
        }
        // SRS rule: a team's squad cannot exceed its maximum squad size
        if (players.size() >= maxSquadSize) {
            throw new CapacityExceededException(name + "'s squad is full (" + maxSquadSize + " players).");
        }
        // SRS rule: players must satisfy the league's minimum-age rule
        if (!player.isEligible()) {
            throw new InvalidDataException(player.getName() + " is under the league's minimum age of " + Player.MIN_AGE + ".");
        }
        if (findPlayer(player.getId()) != null) {
            throw new InvalidDataException("A player with ID " + player.getId() + " is already in " + name + ".");
        }
        Player wearer = findPlayerByJersey(player.getJerseyNumber());
        if (wearer != null) {
            throw new InvalidDataException("Jersey number " + player.getJerseyNumber() + " is already worn by " + wearer.getName() + ".");
        }
        players.add(player);
    }

    // Returns the player who was removed
    public Player removePlayer(String personId) {
        Player player = findPlayer(personId);
        if (player == null) {
            throw new IllegalArgumentException("No player with ID " + personId + " is in " + name + ".");
        }
        players.remove(player);
        return player;
    }

    // Returns null when no player in the squad has that ID
    public Player findPlayer(String personId) {
        for (Player player : players) {
            if (player.getId().equalsIgnoreCase(personId.trim())) {
                return player;
            }
        }
        return null;
    }

    // Returns null when nobody in the squad wears that number
    public Player findPlayerByJersey(int jerseyNumber) {
        for (Player player : players) {
            if (player.getJerseyNumber() == jerseyNumber) {
                return player;
            }
        }
        return null;
    }

    // Changes a squad player's number, keeping jersey numbers unique within the team
    public void changeJerseyNumber(Player player, int jerseyNumber) throws InvalidDataException {
        if (!players.contains(player)) {
            throw new IllegalArgumentException(player.getName() + " is not in " + name + ".");
        }
        Player wearer = findPlayerByJersey(jerseyNumber);
        if (wearer != null && wearer != player) {
            throw new InvalidDataException("Jersey number " + jerseyNumber + " is already worn by " + wearer.getName() + ".");
        }
        player.setJerseyNumber(jerseyNumber);
    }

    public List<Coach> getCoaches() {
        return Collections.unmodifiableList(coaches);
    }

    // Moves the coach to this team (Coach.assignTeam keeps both sides of the link in step)
    public void addCoach(Coach coach) {
        if (coach == null) {
            throw new IllegalArgumentException("Coach is required.");
        }
        coach.assignTeam(this);
    }

    // Returns the coach who was removed; the coach is left with no team
    public Coach removeCoach(String personId) {
        Coach coach = findCoach(personId);
        if (coach == null) {
            throw new IllegalArgumentException("No coach with ID " + personId + " is in " + name + ".");
        }
        coach.assignTeam(null);
        return coach;
    }

    // Returns null when no coach on the staff has that ID
    public Coach findCoach(String personId) {
        for (Coach coach : coaches) {
            if (coach.getId().equalsIgnoreCase(personId.trim())) {
                return coach;
            }
        }
        return null;
    }

    // Called by Coach.assignTeam; use addCoach or Coach.assignTeam rather than calling these directly
    void coachJoined(Coach coach) {
        if (!coaches.contains(coach)) {
            coaches.add(coach);
        }
    }

    void coachLeft(Coach coach) {
        coaches.remove(coach);
    }

    public int getPlayed() {
        return played;
    }

    public int getWon() {
        return won;
    }

    public int getDrawn() {
        return drawn;
    }

    public int getLost() {
        return lost;
    }

    public int getGoalsFor() {
        return goalsFor;
    }

    public int getGoalsAgainst() {
        return goalsAgainst;
    }

    public int getGoalDifference() {
        return goalsFor - goalsAgainst;
    }

    // SRS rule: win = 3 points, draw = 1, loss = 0
    public int getPoints() {
        return won * POINTS_FOR_WIN + drawn * POINTS_FOR_DRAW;
    }

    // Called by Fixture.complete for both teams; use that rather than calling this directly
    void recordMatch(int scored, int conceded) {
        played++;
        goalsFor += scored;
        goalsAgainst += conceded;
        if (scored > conceded) {
            won++;
        } else if (scored == conceded) {
            drawn++;
        } else {
            lost++;
        }
    }

    public String displayInfo() {
        return "Registration No: " + registrationNo + ", Name: " + name + ", Parish: " + parish
                + ", Status: " + status.getStatus() + ", Squad: " + players.size() + "/" + maxSquadSize
                + ", Coaches: " + coaches.size()
                + String.format("%nRecord: P%d W%d D%d L%d, GF %d, GA %d, GD %+d, Points %d",
                played, won, drawn, lost, goalsFor, goalsAgainst, getGoalDifference(), getPoints());
    }

    public String toFileRecord() {
        return registrationNo + "," + name + "," + parish + "," + maxSquadSize + "," + status.name();
    }

    @Override
    public String toString() {
        return name;
    }
}
