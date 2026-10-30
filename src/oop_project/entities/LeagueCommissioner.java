/**
 * Author: Amar Campbell
 * Date: 10/29/2026
 * Description: This class represents a League Commissioner in the sports league management system.
 * It extends the Person class and adds specific attributes related to a League Commissioner, such as office title.
 */

package oop_project.entities;

public class LeagueCommissioner extends Person {
    private String officeTitle;

    public LeagueCommissioner(String id, String name, int age, String gender, String officeTitle) {
        super(id, name, age, gender);
        this.officeTitle = officeTitle;
    }

    public String getOfficeTitle() {
        return officeTitle;
    }

    public void setOfficeTitle(String officeTitle) {
        this.officeTitle = officeTitle;
    }


    @Override
    public String displayInfo() {
        return super.displayInfo() + ", Office Title: " + officeTitle;
    }

    @Override
    public String toFileRecord(){
        return super.toFileRecord() + "," + officeTitle;
    }

    @Override
    public String getRole() {
        return "League Commissioner";
    }
}
