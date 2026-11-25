/**
 * Author: Kemece Hinds - 2501990
 * Date: 11/21/2026
 * Description: This class is the Players & coaches menu. It adds, updates and removes a team's
 * players and coaches, and records goals for a player. The team enforces the squad size, the
 * minimum age and unique jersey numbers, and any broken rule is reported here as a message.
 */

package oop_project.menu;

import oop_project.entities.Coach;
import oop_project.entities.FootballLeague;
import oop_project.entities.FootballTeam;
import oop_project.entities.Player;
import oop_project.enums.PlayerStatus;
import oop_project.exceptions.LeagueException;

public class PersonnelMenu {
    private FootballLeague league;
    private InputHelper input;

    public PersonnelMenu(FootballLeague league, InputHelper input) {
        this.league = league;
        this.input = input;
    }

    public void run() {
        while (true) {
            String[] options = {"Add player", "Update player", "Remove player", "Record goals for a player",
                    "Add coach", "Update coach", "Remove coach", "Display a team's squad and staff"};
            int choice = input.menu("Players & coaches", "Back", options);
            if (choice == 0) {
                return;
            }
            try {
                switch (choice) {
                    case 1:
                        addPlayer();
                        break;
                    case 2:
                        updatePlayer();
                        break;
                    case 3:
                        removePlayer();
                        break;
                    case 4:
                        recordGoals();
                        break;
                    case 5:
                        addCoach();
                        break;
                    case 6:
                        updateCoach();
                        break;
                    case 7:
                        removeCoach();
                        break;
                    case 8:
                        displaySquad();
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

    private FootballTeam pickTeam() {
        return input.pickTeam("Teams", league.getTeams());
    }

    // A person ID must not already belong to a player or coach anywhere in the league
    private boolean idInUse(String id) {
        if (league.findPlayer(id) != null || league.findCoach(id) != null) {
            System.out.println("Error: ID " + id + " is already used by another player or coach.");
            return true;
        }
        return false;
    }

    private void addPlayer() throws LeagueException {
        FootballTeam team = pickTeam();
        if (team == null) {
            return;
        }
        String id = input.readText("Player ID (e.g. P031)", 10);
        if (idInUse(id)) {
            return;
        }
        String name = input.readText("Name");
        int age = input.readInt("Age", 0, 99);
        String gender = input.readText("Gender (e.g. Male or Female)");
        String position = input.readText("Position (e.g. Goalkeeper, Defender, Midfielder, Forward)");
        int jersey = input.readInt("Jersey number", 1, Player.MAX_JERSEY_NUMBER);
        PlayerStatus status = input.pickPlayerStatus();
        if (status == null) {
            status = PlayerStatus.ACTIVE;
        }
        Player player = new Player(id, name, age, gender, position, jersey, 0, status);
        team.addPlayer(player);
        System.out.println(player.getName() + " added to " + team.getName() + " wearing #" + jersey
                + ". Squad: " + team.getSquadSize() + "/" + team.getMaxSquadSize());
    }

    private void updatePlayer() throws LeagueException {
        FootballTeam team = pickTeam();
        if (team == null) {
            return;
        }
        Player player = input.pickPlayer(team);
        if (player == null) {
            return;
        }
        player.setPosition(input.readOptionalText("Position", player.getPosition()));
        int jersey = input.readOptionalInt("Jersey number", player.getJerseyNumber(), 1, Player.MAX_JERSEY_NUMBER);
        team.changeJerseyNumber(player, jersey);
        System.out.println("Current status: " + player.getStatus().getStatus() + " (0 keeps it)");
        PlayerStatus status = input.pickPlayerStatus();
        if (status != null) {
            player.setStatus(status);
        }
        System.out.println("Player updated: " + player.displayInfo());
    }

    private void removePlayer() {
        FootballTeam team = pickTeam();
        if (team == null) {
            return;
        }
        Player player = input.pickPlayer(team);
        if (player == null) {
            return;
        }
        if (!input.confirm("Remove " + player.getName() + " from " + team.getName() + "?")) {
            System.out.println("Nothing was changed.");
            return;
        }
        team.removePlayer(player.getId());
        System.out.println(player.getName() + " removed. Squad: " + team.getSquadSize() + "/" + team.getMaxSquadSize());
    }

    private void recordGoals() throws LeagueException {
        FootballTeam team = pickTeam();
        if (team == null) {
            return;
        }
        Player player = input.pickPlayer(team);
        if (player == null) {
            return;
        }
        // The range allows a negative entry so the league's own rule check reports it
        int goals = input.readInt("Goals scored", -20, 20);
        player.addGoals(goals);
        System.out.println(player.getName() + " now has " + player.getGaols() + " goal(s).");
    }

    private void addCoach() {
        FootballTeam team = pickTeam();
        if (team == null) {
            return;
        }
        String id = input.readText("Coach ID (e.g. C010)", 10);
        if (idInUse(id)) {
            return;
        }
        String name = input.readText("Name");
        int age = input.readInt("Age", 18, 99);
        String gender = input.readText("Gender (e.g. Male or Female)");
        String qualification = input.readText("Qualification (e.g. CONCACAF A Licence)");
        String role = input.readText("Coaching role (e.g. Head Coach)");
        Coach coach = new Coach(id, name, age, gender, qualification, role, null);
        team.addCoach(coach);
        System.out.println(coach.getName() + " added to " + team.getName() + " as " + coach.getCoachingRole() + ".");
    }

    private void updateCoach() {
        FootballTeam team = pickTeam();
        if (team == null) {
            return;
        }
        Coach coach = input.pickCoach(team);
        if (coach == null) {
            return;
        }
        coach.setQualification(input.readOptionalText("Qualification", coach.getQualification()));
        coach.setCoachingRole(input.readOptionalText("Coaching role", coach.getCoachingRole()));
        if (input.readYesNo("Move " + coach.getName() + " to another team?")) {
            FootballTeam newTeam = pickTeam();
            if (newTeam != null) {
                coach.assignTeam(newTeam);
            }
        }
        System.out.println("Coach updated: " + coach.displayInfo());
    }

    private void removeCoach() {
        FootballTeam team = pickTeam();
        if (team == null) {
            return;
        }
        Coach coach = input.pickCoach(team);
        if (coach == null) {
            return;
        }
        if (!input.confirm("Remove " + coach.getName() + " from " + team.getName() + "?")) {
            System.out.println("Nothing was changed.");
            return;
        }
        team.removeCoach(coach.getId());
        System.out.println(coach.getName() + " removed from " + team.getName() + ".");
    }

    private void displaySquad() {
        FootballTeam team = pickTeam();
        if (team != null) {
            TeamMenu.printTeam(team);
        }
    }
}
