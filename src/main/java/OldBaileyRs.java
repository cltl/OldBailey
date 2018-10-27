import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class OldBaileyRs {
/*
<rs id="t19130107-58-verdict-1" type="verdictDescription">
     <interp inst="t19130107-58-verdict-1" type="verdictCategory" value="guilty"/>
     <interp inst="t19130107-58-verdict-1" type="verdictSubcategory" value="no_subcategory"/>Guilty</rs>.

<rs id="t19130107-4-offence-1" type="offenceDescription">
 <interp inst="t19130107-4-offence-1" type="offenceCategory" value="theft"/>
 <interp inst="t19130107-4-offence-1" type="offenceSubcategory" value="mail"/>of stealing a postal packet and the sum of £2 10s., the property of <persName id="t19130107-name-27" type="victimName">
    <interp inst="t19130107-name-27" type="gender" value="indeterminate"/>
    <join result="offenceVictim" targOrder="Y"
          targets="t19130107-4-offence-1 t19130107-name-27"/>His Majesty's Postmaster-General</persName>, he being an officer of the Post Office.</rs>
 */
  private String id;
  private String type;
  private ArrayList<OldBaileyInterp> interpArrayList;
  private String mention;

    public OldBaileyRs(String id, String type) {
        this.id = id;
        this.type = type;
        this.interpArrayList = new ArrayList<>();
        this.mention = "";
    }

    public OldBaileyRs() {
        this.id = "";
        this.type = "";
        this.interpArrayList = new ArrayList<>();
        this.mention = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public void addToModel(Model namedModel) throws UnsupportedEncodingException {
        Resource subjectResource = namedModel.createResource(id);
        Statement meta = namedModel.createStatement(subjectResource, RDF.type, "Event");
        namedModel.add(meta);
        if (!type.isEmpty()) {
            meta = namedModel.createStatement(subjectResource, RDF.type, type);
            namedModel.add(meta);
        }
        if (!mention.isEmpty()) {
            Property metaProperty = namedModel.createProperty(ResourcesUri.oldbaily, "mention");
            meta = namedModel.createStatement(subjectResource, metaProperty, mention);
            namedModel.add(meta);
        }
        for (int i = 0; i < interpArrayList.size(); i++) {
            OldBaileyInterp oldBaileyInterp = interpArrayList.get(i);
            oldBaileyInterp.addToModel(namedModel);
        }
    }
}
