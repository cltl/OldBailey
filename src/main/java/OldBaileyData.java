import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

import java.util.ArrayList;

/**
 * Created by piek on 05/12/2017.
 */
public class OldBaileyData {

    /*
    ordinary
    <persName id="OA16980309n24-1">
                      <interp inst="OA16980309n24-1" type="gender" value="male"/>
                      <interp inst="OA16980309n24-1" type="surname" value="MARSLIN"/>
                      <interp inst="OA16980309n24-1" type="given" value="WILLIAM"/>WIlliam Marslin</persName>,
    <persName id="OA16980309n24-3">
                      <interp inst="OA16980309n24-3" type="gender" value="male"/>
                      <interp inst="OA16980309n24-3" type="surname" value="Marslin"/>
                      <interp inst="OA16980309n24-3" type="given" value="Peter"/>alias, Peter</persName>
                      , Condemned for robbing
    <persName id="OA16980309n24-2">
                      <interp inst="OA16980309n24-2" type="gender" value="female"/>
                      <interp inst="OA16980309n24-2" type="surname" value="JOLLY"/>
                      <interp inst="OA16980309n24-2" type="given" value="MARY"/>Mary Jolly</persName>

    <placeName id="OA16980309-geo-1">
                      <interp inst="OA16980309-geo-1" type="type" value="parish"/>
                      <join result="persNamePlace" targOrder="Y"
                            targets="OA16980309n24-1 n24-3 OA16980309-geo-1"/>Aldgate Parish</placeName>
                            . He was
                            <rs id="OA16980309-occupation-1" type="occupation">Aprentice and Journey Man to a Glass-maker</rs>
                   <join result="persNameOccupation" targOrder="Y"
                         targets="OA16980309n24-1 n24-3 OA16980309-occupation-1"/>,

     <persName id="OA16980309n37-1">
                       <interp inst="OA16980309n37-1" type="gender" value="male"/>
                       <interp inst="OA16980309n37-1" type="surname" value="Smith"/>
                       <interp inst="OA16980309n37-1" type="given" value="Sam"/>Sam. Smith</persName>,
                       <rs id="OA16980309-occupation-4" type="occupation">Ordinary</rs>
                    <join result="persNameOccupation" targOrder="Y"
                          targets="OA16980309n37-1 OA16980309-occupation-4"/>.</p>
     */
    /*
    sessions
    <rs id="t19130107-2-punishment-1" type="punishmentDescription">
                         <interp inst="t19130107-2-punishment-1" type="punishmentCategory" value="imprison"/>
                         <interp inst="t19130107-2-punishment-1" type="punishmentSubcategory" value="hardLabour"/>
                         <join result="defendantPunishment" targOrder="Y"
                               targets="def1-2-19130107 t19130107-2-punishment-1"/>Fifteen months' hard labour</rs>; Cornish <rs id="t19130107-2-punishment-2" type="punishmentDescription">
                         <interp inst="t19130107-2-punishment-2" type="punishmentCategory" value="imprison"/>
                         <interp inst="t19130107-2-punishment-2" type="punishmentSubcategory" value="hardLabour"/>
                         <join result="defendantPunishment" targOrder="Y"
                               targets="def2-2-19130107 t19130107-2-punishment-2"/>Twenty months' hard labour</rs>.
     */
    private String caseid;
    private String offenceCategory;
    private String offenceSubcategory;
    private String verdictCategory;
    private String verdictSubcategory;
    private String punishmentCategory;
    private ArrayList<OldBaileyPerson> persons;
    private ArrayList<OldBaileyPlace> places;
    private ArrayList<OldBaileyJoin> joins;


    public OldBaileyData() {
        this.caseid = "";
        this.offenceCategory = "";
        this.offenceSubcategory = "";
        this.verdictCategory = "";
        this.verdictSubcategory = "";
        this.punishmentCategory = "";
        this.places = new ArrayList<OldBaileyPlace>();
        this.persons = new ArrayList<OldBaileyPerson>();
        this.joins = new ArrayList<OldBaileyJoin>();
    }

    public void setValue (String type, String value) {
        if (type.equals("offenceCategory")) offenceCategory=value;
        if (type.equals("offenceSubcategory")) offenceSubcategory=value;
        if (type.equals("verdictCategory")) verdictCategory=value;
        if (type.equals("verdictSubcategory")) verdictSubcategory=value;
        if (type.equals("punishmentCategory")) punishmentCategory=value;
    }

    public ArrayList<Statement> getStatement (Model namedModel, Resource subject) {
        ArrayList<Statement> statements = new ArrayList<Statement>();
        if (!offenceCategory.isEmpty()) {
            Property metaProperty = namedModel.createProperty(ResourcesUri.oldbaily, "offenceCategory");
            Resource object = namedModel.createResource(ResourcesUri.oldbaily+offenceCategory);
            Statement meta = namedModel.createStatement(subject, metaProperty, object);
            statements.add(meta);
        }
        if (!offenceSubcategory.isEmpty()) {
            Property metaProperty = namedModel.createProperty(ResourcesUri.oldbaily, "offenceSubcategory");
            Resource object = namedModel.createResource(ResourcesUri.oldbaily+offenceSubcategory);
            Statement meta = namedModel.createStatement(subject, metaProperty, object);
            statements.add(meta);
        }
        if (!verdictCategory.isEmpty()) {
            Property metaProperty = namedModel.createProperty(ResourcesUri.oldbaily, "verdictCategory");
            Resource object = namedModel.createResource(ResourcesUri.oldbaily+verdictCategory);
            Statement meta = namedModel.createStatement(subject, metaProperty, object);
            statements.add(meta);
        }
        if (!verdictSubcategory.isEmpty()) {
            Property metaProperty = namedModel.createProperty(ResourcesUri.oldbaily, "verdictSubcategory");
            Resource object = namedModel.createResource(ResourcesUri.oldbaily+verdictSubcategory);
            Statement meta = namedModel.createStatement(subject, metaProperty, object);
            statements.add(meta);
        }
        if (!punishmentCategory.isEmpty()) {
            Property metaProperty = namedModel.createProperty(ResourcesUri.oldbaily, "punishmentCategory");
            Resource object = namedModel.createResource(ResourcesUri.oldbaily+punishmentCategory);
            Statement meta = namedModel.createStatement(subject, metaProperty, object);
            statements.add(meta);
        }
        for (int i = 0; i < persons.size(); i++) {
            try {
                OldBaileyPerson oldBaileyPerson = persons.get(i);
                //System.out.println("caseid = " + caseid);
                //System.out.println("oldBaileyPerson.getGender() = " + oldBaileyPerson.getGender());
                //System.out.println("oldBaileyPerson.getGivenname() = " + oldBaileyPerson.getGivenname());
                //System.out.println("oldBaileyPerson.getSurname() = " + oldBaileyPerson.getSurname());
                Resource personResource = namedModel.createResource(oldBaileyPerson.getUri(caseid));
                //System.out.println("personResource.getURI() = " + personResource.getURI());
                oldBaileyPerson.addToModel(namedModel, personResource);
                Property metaProperty = namedModel.createProperty(ResourcesUri.oldbaily, oldBaileyPerson.getOccupation());
                Statement meta = namedModel.createStatement(subject, metaProperty, personResource);
                statements.add(meta);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statements;
    }

    public ArrayList<OldBaileyPlace> getPlaces() {
        return places;
    }

    public void addPlaces(OldBaileyPlace place) {
        this.places.add(place);
    }

    public ArrayList<OldBaileyPerson> getPersons() {
        return persons;
    }

    public void setPersons(ArrayList<OldBaileyPerson> persons) {
        this.persons = persons;
    }
    public void addPersons(OldBaileyPerson person) {
        this.persons.add(person);
    }

    public String getCaseid() {
        return caseid;
    }

    public void setCaseid(String caseid) {
        this.caseid = caseid;
    }

    public String getOffenceCategory() {
        return offenceCategory;
    }

    public void setOffenceCategory(String offenceCategory) {
        this.offenceCategory = offenceCategory;
    }

    public String getOffenceSubcategory() {
        return offenceSubcategory;
    }

    public void setOffenceSubcategory(String offenceSubcategory) {
        this.offenceSubcategory = offenceSubcategory;
    }

    public String getVerdictCategory() {
        return verdictCategory;
    }

    public void setVerdictCategory(String verdictCategory) {
        this.verdictCategory = verdictCategory;
    }

    public String getVerdictSubcategory() {
        return verdictSubcategory;
    }

    public void setVerdictSubcategory(String verdictSubcategory) {
        this.verdictSubcategory = verdictSubcategory;
    }

    public String getPunishmentCategory() {
        return punishmentCategory;
    }

    public void setPunishmentCategory(String punishmentCategory) {
        this.punishmentCategory = punishmentCategory;
    }

    public ArrayList<OldBaileyJoin> getJoins() {
        return joins;
    }

    public void addJoins(OldBaileyJoin oldBaileyJoin) {
        this.joins.add(oldBaileyJoin);
    }

    /**
     *
     <rs id="t18741123-56-offence-1" type="offenceDescription">
     <interp inst="t18741123-36-offence-1" type="offenceCategory" value="damage"/>

     <rs id="t18741123-56-verdict-1" type="verdictDescription">
     <interp inst="t18741123-35-verdict-1" type="verdictCategory" value="guilty"/>
     <interp inst="t18741123-35-verdict-1" type="verdictSubcategory" value="no_subcategory"/>GUILTY</rs>
     <interp inst="t18741123-37-verdict-1" type="verdictCategory" value="notGuilty"/>
     <interp inst="t18741123-37-verdict-1" type="verdictSubcategory" value="noEvidence"/>NOT GUILTY</rs>
     <interp inst="t18741123-38-offence-1" type="offenceCategory" value="violentTheft"/>
     <interp inst="t18741123-38-offence-1" type="offenceSubcategory" value="robbery"/>, Robbery with violence on <persName id="t18741123-name-219" type="victimName">
     <interp inst="t18741123-38-punishment-31" type="punishmentCategory" value="imprison"/>
     <interp inst="t18741123-38-punishment-31" type="punishmentSubcategory" value="no_subcategory"/>
     <interp inst="t18741123-38-verdict-1" type="verdictCategory" value="guilty"/>
     <interp inst="t18741123-38-verdict-1" type="verdictSubcategory" value="withRecommendation"/>GUILTY</rs>

     <rs id="t18741123-33-punishment-28" type="punishmentDescription">
     <persName id="t18741123-name-336" type="witnessName">
     <p>39. <persName id="def1-39-18741123" type="defendantName">
     <join result="defendantPunishment" targOrder="Y" targets="def1-4-18741123 t18741123-4-punishment-3"/>One Day's imprisonment</rs> , and to <rs id="t18741123-4-punishment-4" type="punishmentDescription">
     <join id="t18741123-55-charge-1" result="criminalCharge" targOrder="Y" targets="def1-55-18741123 t18741123-55-offence-1 t18741123-55-verdict-1"/>
     <interp inst="t18741123-name-365" type="surname" value="CHABOT"/>
     <interp inst="t18741123-name-366" type="gender" value="male"/>
     <interp inst="t18741123-name-366" type="given" value="WILLIAM"/>WILLIAM CLATWORTHY</persName>
     */
}
