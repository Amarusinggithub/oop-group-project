/**
 * Author: Kemece Hinds - 2501990
 * Date: 10/29/2026
 * Description: This class represents a coach in the football team management system.
 * It extends the Person class and adds specific attributes related to a coach, such as
 * qualification, coaching role, and the team they are associated with.
 */

package oop_project.entities;

public class Coach extends Person {
    private  String qualification;
    private String coachingRole;
    private FootballTeam team;

    // team may be null; the coach then joins a team later through assignTeam or FootballTeam.addCoach
    public Coach(String id, String name, int age, String gender, String qualification, String coachingRole, FootballTeam team) {
        super(id, name, age, gender);
        setQualification(qualification);
        setCoachingRole(coachingRole);
        assignTeam(team);
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        if (qualification == null || qualification.trim().isEmpty()) {
            throw new IllegalArgumentException("Qualification is required.");
        }
        this.qualification = qualification.trim();
    }

    public String getCoachingRole() {
        return coachingRole;
    }

    public void setCoachingRole(String coachingRole) {
        if (coachingRole == null || coachingRole.trim().isEmpty()) {
            throw new IllegalArgumentException("Coaching role is required.");
        }
        this.coachingRole = coachingRole.trim();
    }

    public FootballTeam getTeam() {
        return team;
    }

    // A coach works for at most one team. Moving to a new team removes the coach from the old one.
    // Pass null to take the coach off their team.
    public void assignTeam(FootballTeam newTeam) {
        if (newTeam == team) {
            return;
        }
        if (team != null) {
            team.coachLeft(this);
        }
        team = newTeam;
        if (newTeam != null) {
            newTeam.coachJoined(this);
        }
    }

    @Override
    public String displayInfo() {
        String teamName = "None";
        if (team != null) {
            teamName = team.getName();
        }
        return super.displayInfo() + ", Qualification: " + qualification + ", Coaching Role: " + coachingRole + ", Team: " + teamName;
    }

    // The team is stored by registration number, or "-" when the coach has no team
    @Override
    public String toFileRecord(){
        String teamNo = "-";
        if (team != null) {
            teamNo = team.getRegistrationNo();
        }
        return super.toFileRecord() + "," + qualification + "," + coachingRole + "," + teamNo;
    }

    @Override
    public String getRole(){
        return "Coach";
    }
}
