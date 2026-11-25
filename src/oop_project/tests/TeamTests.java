/**
 * Author: Kemece Hinds - 2501990
 * Date: 11/21/2026
 * Description: Tests T-01 to T-07 from the SRS test plan: registering teams, the minimum-age rule,
 * the maximum squad size and unique jersey numbers.
 */

package oop_project.tests;

import oop_project.entities.FootballLeague;
import oop_project.entities.FootballTeam;
import oop_project.entities.Player;
import oop_project.enums.PlayerStatus;
import oop_project.enums.TeamStatus;
import oop_project.exceptions.CapacityExceededException;
import oop_project.exceptions.InvalidDataException;

public class TeamTests {

    public static void run() {
        FootballLeague league = new FootballLeague("Test League", 2026);
        FootballTeam harbourView = new FootballTeam("T1", "Harbour View", "Kingston", 25);

        // T-01 (normal): a new team is saved as Pending
        Check.noException("T-01", "Register team T1 \"Harbour View\", Kingston, max squad 25 (normal)",
                () -> league.registerTeam(harbourView));
        Check.equal("T-01", "New team's status", TeamStatus.PENDING.getStatus(), harbourView.getStatus().getStatus());
        Check.equal("T-01", "Search \"  harbour VIEW \" ignores case and spaces", "Harbour View",
                String.valueOf(league.searchTeam("  harbour VIEW ")));

        // T-02 (invalid): the registration number is already used, in any letter case
        Check.throwsException("T-02", "Register another team with registration no. \"t1\" (invalid)", InvalidDataException.class,
                () -> league.registerTeam(new FootballTeam("t1", "Waterhouse", "Kingston", 25)));
        Check.equal("T-02", "Teams registered after the refusal", 1, league.getTeams().size());

        // T-03 (boundary): exactly the minimum age is allowed
        Check.noException("T-03", "Add a 16-year-old player (boundary)",
                () -> harbourView.addPlayer(player("P001", "Kevaughn Brown", Player.MIN_AGE, 10)));
        Check.equal("T-03", "Squad size after adding", 1, harbourView.getSquadSize());

        // T-04 (invalid): one year under the minimum age
        Check.throwsException("T-04", "Add a 15-year-old player (invalid)", InvalidDataException.class,
                () -> harbourView.addPlayer(player("P002", "Junior Gordon", Player.MIN_AGE - 1, 11)));
        Check.equal("T-04", "Squad size is unchanged", 1, harbourView.getSquadSize());

        // T-05 (boundary): the player who fills the last place is accepted
        FootballTeam smallSquad = new FootballTeam("T9", "Small Squad FC", "St. Andrew", 2);
        Check.noException("T-05", "Add the player that fills the last squad place (boundary)", () -> {
            smallSquad.addPlayer(player("P010", "Andre Clarke", 20, 1));
            smallSquad.addPlayer(player("P011", "Romario Smith", 22, 2));
        });
        Check.equal("T-05", "Squad size equals the maximum", smallSquad.getMaxSquadSize(), smallSquad.getSquadSize());

        // T-06 (invalid): one more than the maximum
        Check.throwsException("T-06", "Add one more player to the full squad (invalid)", CapacityExceededException.class,
                () -> smallSquad.addPlayer(player("P012", "Dwayne Miller", 23, 3)));
        Check.equal("T-06", "Squad size is unchanged", 2, smallSquad.getSquadSize());

        // T-07 (invalid): the jersey number is already worn; the message names the wearer
        String message;
        try {
            harbourView.addPlayer(player("P003", "Shamar Thompson", 24, 10));
            message = "no exception";
        } catch (Exception e) {
            message = e.getClass().getSimpleName() + ": " + e.getMessage();
        }
        Check.equal("T-07", "Add a player with a jersey number already worn in the team (invalid)",
                "InvalidDataException: Jersey number 10 is already worn by Kevaughn Brown.", message);
    }

    private static Player player(String id, String name, int age, int jersey) {
        return new Player(id, name, age, "Male", "Midfielder", jersey, 0, PlayerStatus.ACTIVE);
    }
}
