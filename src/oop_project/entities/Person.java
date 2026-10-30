/**
 * Author: Amar Campbell
 * Date: 10/29/2026
 * Description: This class represents a person in the football team management system.
 * It is an abstract class that provides a base for other classes like Coach and Player.
 */

package oop_project.entities;

public abstract class Person {
    protected String Id;
    protected String name;
    protected int age;
    protected String gender;

    public Person(String id, String name, int age, String gender) {
        Id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public String displayInfo() {
        return "Id: " + Id + ", Name: " + name + ", Age: " + age + ", Gender: " + gender;
    }

    public String toFileRecord(){
        return Id + "," + name + "," + age + "," + gender;
    }

    public abstract String getRole();
}
