/**
 * Created by piek on 05/12/2017.
 */
public class OldBaileyPerson {

    private String givenname;
    private String surename;
    private String age;
    private String gender;
    private String role;

    public OldBaileyPerson() {
        this.givenname = "";
        this.surename = "";
        this.age = "";
        this.gender = "";
        this.role = "";
    }

    public String getGivenname() {
        return givenname;
    }

    public void setGivenname(String givenname) {
        this.givenname = givenname;
    }

    public String getSurename() {
        return surename;
    }

    public void setSurename(String surename) {
        this.surename = surename;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
