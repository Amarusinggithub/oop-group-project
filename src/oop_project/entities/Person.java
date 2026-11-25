/**
 * Author: Kemece Hinds - 2501990
 * Date: 10/29/2026
 * Description: This class represents a person in the football team management system.
 * It is an abstract class that provides a base for other classes like Coach and Player.
 */

package oop_project.entities;

public abstract class Person {
    public static final int MAX_AGE = 120;

    protected String Id;
    protected String name;
    protected int age;
    protected String gender;

    public Person(String id, String name, int age, String gender) {
        setId(id);
        setName(name);
        setAge(age);
        setGender(gender);
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID is required.");
        }
        Id = id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required.");
        }
        this.name = name.trim();
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        if (age < 0 || age > MAX_AGE) {
            throw new IllegalArgumentException("Age must be between 0 and " + MAX_AGE + ".");
        }
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        if (gender == null || gender.trim().isEmpty()) {
            throw new IllegalArgumentException("Gender is required.");
        }
        this.gender = gender.trim();
    }


    public String displayInfo() {
        return "Id: " + Id + ", Name: " + name + ", Age: " + age + ", Gender: " + gender;
    }

    public String toFileRecord(){
        return Id + "," + name + "," + age + "," + gender;
    }

    public abstract String getRole();

    @Override
    public String toString() {
        return name;
    }
}
