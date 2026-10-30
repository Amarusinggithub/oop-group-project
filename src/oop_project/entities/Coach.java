/**
 * Author: Amar Campbell
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

    public Coach(String id, String name, int age, String gender, String qualification, String coachingRole, FootballTeam team) {
        super(id, name, age, gender);
        this.qualification = qualification;
        this.coachingRole = coachingRole;
        this.team = team;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getCoachingRole() {
        return coachingRole;
    }

    public void setCoachingRole(String coachingRole) {
        this.coachingRole = coachingRole;
    }

    public FootballTeam getTeam() {
        return team;
    }

    public void setTeam(FootballTeam team) {
        this.team = team;
    }

    @Override
    public String displayInfo() {
        return super.displayInfo() + ", Qualification: " + qualification + ", Coaching Role: " + coachingRole + ", Team: " + team;
    }

    @Override
    public String toFileRecord(){
        return super.toFileRecord() + "," + qualification + "," + coachingRole + "," + team;
    }

    @Override
    public String getRole(){
        return "Coach";
    }
}
