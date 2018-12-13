package ob;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by piek on 05/12/2017.
 */
public class OldBaileyPerson {

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
     <join result="persNameOccupation" targOrder="Y" targets="OA16980309n24-1 n24-3 OA16980309-occupation-1"/>,

       <persName id="OA16980309n37-1">
                         <interp inst="OA16980309n37-1" type="gender" value="male"/>
                         <interp inst="OA16980309n37-1" type="surname" value="Smith"/>
                         <interp inst="OA16980309n37-1" type="given" value="Sam"/>Sam. Smith</persName>,
                         <rs id="OA16980309-occupation-4" type="occupation">Ordinary</rs>
                      <join result="persNameOccupation" targOrder="Y"  targets="OA16980309n37-1 OA16980309-occupation-4"/>.</p>
       */
/*
Sessions
                  <persName id="def1-2-19130107" type="defendantName">
                     <interp inst="def1-2-19130107" type="gender" value="male"/>
                     <interp inst="def1-2-19130107" type="age" value="24"/>
                     <interp inst="def1-2-19130107" type="surname" value="HALL"/>
                     <interp inst="def1-2-19130107" type="given" value="EDWARD"/>
                     <interp inst="def1-2-19130107" type="occupation" value="labourer"/>
                     <hi rend="largeCaps">HALL</hi>, Edward (24, labourer)</persName>, and
                     <persName id="def2-2-19130107" type="defendantName">
                     <interp inst="def2-2-19130107" type="gender" value="male"/>
                     <interp inst="def2-2-19130107" type="age" value="32"/>
                     <interp inst="def2-2-19130107" type="surname" value="CORNISH"/>
                     <interp inst="def2-2-19130107" type="given" value="WILLIAM"/>
                     <interp inst="def2-2-19130107" type="occupation" value="labourer"/>
                     <hi rend="largeCaps">CORNISH</hi>, William (32, labourer)</persName>
 */
    private String type;
    private String id;
    private String trial;
    private String mention;
    private ArrayList<OldBaileyInterp> interpArrayList;


    public OldBaileyPerson() {
        this.id = "";
        this.type = "unknownRole";
        this.mention = "";
        this.trial = "";
        this.interpArrayList = new ArrayList<>();
    }

    public void addToModel(Model namedModel) throws UnsupportedEncodingException {
        Resource subjectResource = namedModel.createResource(ResourcesUri.oldbaily+id);
        Resource objectResource = namedModel.createResource(ResourcesUri.oldbaily+"Person");
        com.hp.hpl.jena.rdf.model.Statement meta = namedModel.createStatement(subjectResource, RDF.type, objectResource);
        namedModel.add(meta);

        if (!type.isEmpty()) {
            objectResource = namedModel.createResource(ResourcesUri.oldbaily+type);
            Property metaProperty = namedModel.createProperty(ResourcesUri.oldbaily, "role");
            meta = namedModel.createStatement(subjectResource, metaProperty, objectResource);
            namedModel.add(meta);
        }
        if (!mention.isEmpty()) {
            Property metaProperty = namedModel.createProperty(ResourcesUri.oldbaily, "mention");
            meta = namedModel.createStatement(subjectResource, metaProperty, mention);
            namedModel.add(meta);
        }
        if (!trial.isEmpty()) {
            objectResource = namedModel.createResource(ResourcesUri.oldbaily+trial);
            Property metaProperty = namedModel.createProperty(ResourcesUri.oldbaily, "trial");
            meta = namedModel.createStatement(subjectResource, metaProperty, objectResource);
            namedModel.add(meta);
        }
        for (int i = 0; i < interpArrayList.size(); i++) {
            OldBaileyInterp oldBaileyInterp = interpArrayList.get(i);
            oldBaileyInterp.addToModel(namedModel);
        }
    }

/*    public void addToModel (Model namedModel,Resource subject) throws UnsupportedEncodingException {
        if (!gender.isEmpty()) {
            Property metaProperty = namedModel.createProperty(ob.ResourcesUri.oldbaily, "gender");
            Resource objectResource = namedModel.createResource(ob.ResourcesUri.oldbaily+gender);
            Statement meta = namedModel.createStatement(subject, metaProperty, objectResource);
            namedModel.add(meta);
        }
        if (!age.isEmpty()) {
            Property metaProperty = namedModel.createProperty(ob.ResourcesUri.oldbaily, "age");
            Resource objectResource = namedModel.createResource(ob.ResourcesUri.oldbaily+age);
            Statement meta = namedModel.createStatement(subject, metaProperty, objectResource);
            namedModel.add(meta);
        }
        if (!type.isEmpty()) {
            Property metaProperty = namedModel.createProperty(ob.ResourcesUri.oldbaily, "type");
            Resource objectResource = namedModel.createResource(ob.ResourcesUri.oldbaily+type);
            Statement meta = namedModel.createStatement(subject, metaProperty, objectResource);
            namedModel.add(meta);
        }
        if (!givenname.isEmpty()) {
            Property metaProperty = namedModel.createProperty(ob.ResourcesUri.oldbaily, "givenname");
            Statement meta = namedModel.createStatement(subject, metaProperty, givenname);
            namedModel.add(meta);
        }
        if (!surname.isEmpty()) {
            Property metaProperty = namedModel.createProperty(ob.ResourcesUri.oldbaily, "surname");
            Statement meta = namedModel.createStatement(subject, metaProperty, surname);
            namedModel.add(meta);
        }
        if (!occupation.isEmpty()) {
            Property metaProperty = namedModel.createProperty(ob.ResourcesUri.oldbaily, "occupation");
            Statement meta = namedModel.createStatement(subject, metaProperty, occupation);
            namedModel.add(meta);
        }
        if (!id.isEmpty()) {
            Property metaProperty = namedModel.createProperty(ob.ResourcesUri.oldbaily, "id");
            Statement meta = namedModel.createStatement(subject, metaProperty, id);
            namedModel.add(meta);
        }
        if (!place.isEmpty()) {
            Property metaProperty = namedModel.createProperty(ob.ResourcesUri.oldbaily, "place");
            Statement meta = namedModel.createStatement(subject, metaProperty, place);
            namedModel.add(meta);
        }
    }*/

    public String getUri (String caseId) throws UnsupportedEncodingException {
        String uri = ResourcesUri.oldbaily+caseId + "/person/"+id;
        return uri;
    }

    public String getTrial() {
        return trial;
    }

    public void setTrial(String trial) {
        this.trial = trial;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMention() {
        return mention;
    }

    public void setMention(String mention) {
        this.mention = mention;
    }

    public ArrayList<OldBaileyInterp> getInterpArrayList() {
            return interpArrayList;
    }

    public void addInterpArrayList(OldBaileyInterp oldBaileyInterp) {
        this.interpArrayList.add(oldBaileyInterp);
    }
}
