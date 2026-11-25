/**
 * Author: Kemece Hinds - 2501990
 * Date: 11/21/2026
 * Description: This class is the Teams menu. It registers, updates, searches for and displays
 * football teams, and lets the league commissioner approve or suspend a team.
 */

package oop_project.menu;

import oop_project.entities.Coach;
import oop_project.entities.FootballLeague;
import oop_project.entities.FootballTeam;
import oop_project.entities.LeagueCommissioner;
import oop_project.entities.Player;
import oop_project.exceptions.LeagueException;

public class TeamMenu {
    public static final int MAX_SQUAD_LIMIT = 50;

    private FootballLeague league;
    private InputHelper input;

    public TeamMenu(FootballLeague league, InputHelper input) {
        this.league = league;
        this.input = input;
    }

    public void run() {
        while (true) {
            String[] options = {"Register team", "Update team", "Search for a team", "Display all teams", "Approve or suspend a team"};
            int choice = input.menu("Teams", "Back", options);
            if (choice == 0) {
                return;
            }
            try {
                switch (choice) {
                    case 1:
                        registerTeam();
                        break;
                    case 2:
                        updateTeam();
                        break;
                    case 3:
                        searchTeam();
                        break;
                    case 4:
                        displayTeams();
                        break;
                    case 5:
                        approveOrSuspend();
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

    private void registerTeam() throws LeagueException {
        String registrationNo = input.readText("Registration number (e.g. T7)", 10);
        String name = input.readText("Team name");
        String parish = input.readText("Parish");
        int maxSquad = input.readInt("Maximum squad size", 1, MAX_SQUAD_LIMIT);
        FootballTeam team = new FootballTeam(registrationNo, name, parish, maxSquad);
        league.registerTeam(team);
        System.out.println("Team " + team.getRegistrationNo() + " " + team.getName() + " registered with status "
                + team.getStatus().getStatus() + ". The commissioner must approve it before it can play.");
    }

    private void updateTeam() {
        FootballTeam team = input.pickTeam("Teams", league.getTeams());
        if (team == null) {
            return;
        }
        team.setName(input.readOptionalText("Team name", team.getName()));
        team.setParish(input.readOptionalText("Parish", team.getParish()));
        team.setMaxSquadSize(input.readOptionalInt("Maximum squad size", team.getMaxSquadSize(),
                Math.max(1, team.getSquadSize()), MAX_SQUAD_LIMIT));
        System.out.println("Team updated: " + team.getRegistrationNo() + " " + team.getName() + ", " + team.getParish()
                + ", squad " + team.getSquadSize() + "/" + team.getMaxSquadSize());
    }

    private void searchTeam() {
        String text = input.readText("Team name or registration number");
        FootballTeam team = league.searchTeam(text);
        if (team == null) {
            System.out.println("No match found.");
            return;
        }
        printTeam(team);
    }

    // FR-01.4: details, status, squad, coaches and match record
    static void printTeam(FootballTeam team) {
        System.out.println(team.displayInfo());
        System.out.println("Squad:");
        if (team.getPlayers().isEmpty()) {
            System.out.println("  (no players yet)");
        }
        for (Player player : team.getPlayers()) {
            System.out.printf("  #%-3d %-8s %-24s %-12s %3d yrs  %-10s goals %d%n", player.getJerseyNumber(), player.getId(),
                    player.getName(), player.getPosition(), player.getAge(), player.getStatus().getStatus(), player.getGaols());
        }
        System.out.println("Coaching staff:");
        if (team.getCoaches().isEmpty()) {
            System.out.println("  (no coaches yet)");
        }
        for (Coach coach : team.getCoaches()) {
            System.out.printf("  %-8s %-24s %-22s %s%n", coach.getId(), coach.getName(), coach.getCoachingRole(), coach.getQualification());
        }
    }

    private void displayTeams() {
        if (league.getTeams().isEmpty()) {
            System.out.println("No teams are registered yet.");
            return;
        }
        System.out.printf("%-8s %-24s %-16s %-11s %7s %3s %4s%n", "Reg No", "Name", "Parish", "Status", "Squad", "P", "Pts");
        for (FootballTeam team : league.getTeams()) {
            System.out.printf("%-8s %-24s %-16s %-11s %3d/%-3d %3d %4d%n", team.getRegistrationNo(), team.getName(), team.getParish(),
                    team.getStatus().getStatus(), team.getSquadSize(), team.getMaxSquadSize(), team.getPlayed(), team.getPoints());
        }
    }

    private void approveOrSuspend() {
        LeagueCommissioner commissioner = league.getCommissioner();
        if (commissioner == null) {
            System.out.println("There is no league commissioner on record. Add one under Match officials > Set league commissioner.");
            return;
        }
        FootballTeam team = input.pickTeam("Teams", league.getTeams());
        if (team == null) {
            return;
        }
        String[] options = {"Approve (Registered)", "Suspend"};
        int choice = input.menu("Commissioner " + commissioner.getName() + ": " + team.getName(), "Back", options);
        if (choice == 1) {
            commissioner.approveTeam(team);
        } else if (choice == 2) {
            commissioner.suspendTeam(team);
        } else {
            return;
        }
        System.out.println(team.getName() + " is now " + team.getStatus().getStatus() + ".");
    }
}
