import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by piek on 05/12/2017.
 */
public class OldBaileyPerson {

    private String givenname;
    private String surname;
    private String age;
    private String gender;
    private String role;

    public OldBaileyPerson() {
        this.givenname = "";
        this.surname = "";
        this.age = "";
        this.gender = "";
        this.role = "unknownRole";
    }

    public void addToModel (Model namedModel,Resource subject) throws UnsupportedEncodingException {
        if (!gender.isEmpty()) {
            Property metaProperty = namedModel.createProperty(ResourcesUri.nwr, "gender");
            Statement meta = namedModel.createStatement(subject, metaProperty, gender);
            namedModel.add(meta);
        }
        if (!age.isEmpty()) {
            Property metaProperty = namedModel.createProperty(ResourcesUri.nwr, "age");
            Statement meta = namedModel.createStatement(subject, metaProperty, age);
            namedModel.add(meta);
        }
        if (!role.isEmpty()) {
            Property metaProperty = namedModel.createProperty(ResourcesUri.nwr, "role");
            Statement meta = namedModel.createStatement(subject, metaProperty, role);
            namedModel.add(meta);
        }
        if (!givenname.isEmpty()) {
            Property metaProperty = namedModel.createProperty(ResourcesUri.nwr, "givenname");
            Statement meta = namedModel.createStatement(subject, metaProperty, givenname);
            namedModel.add(meta);
        }
        if (!surname.isEmpty()) {
            Property metaProperty = namedModel.createProperty(ResourcesUri.nwr, "surname");
            Statement meta = namedModel.createStatement(subject, metaProperty, surname);
            namedModel.add(meta);
        }

    }

    public String getUri (String caseId) throws UnsupportedEncodingException {
        String uri = ResourcesUri.oldbaily+caseId + "/person/"+URLEncoder.encode(surname +"_"+givenname, "UTF-8").toLowerCase();
        return uri;
    }

    public void setValue (String type, String value) {
        if (type.equals("age")) age=value;
        if (type.equals("gender")) gender=value;
        if (type.equals("surname")) surname =value;
        if (type.equals("given")) givenname=value;
    }

    public String getGivenname() {
        return givenname;
    }

    public void setGivenname(String givenname) {
        this.givenname = givenname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
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
