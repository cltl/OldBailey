import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class OldBaileyTrial {

    /*
     <div1 type="trialAccount" id="t18070408-35">
               <interp inst="t18070408-35" type="collection" value="BAILEY"/>
               <interp inst="t18070408-35" type="year" value="1807"/>
               <interp inst="t18070408-35" type="uri" value="sessionsPapers/18070408"/>
               <interp inst="t18070408-35" type="date" value="18070408"/>
               <join result="criminalCharge" id="t18070408-35-off201-c258" targOrder="Y"
                     targets="t18070408-35-defend337 t18070408-35-off201 t18070408-35-verdict204"/>
     */

    private String id;
    private ArrayList<OldBaileyInterp> interpArrayList;

    public OldBaileyTrial(String id) {
        this.id = id;
        interpArrayList = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<OldBaileyInterp> getInterpArrayList() {
        return interpArrayList;
    }

    public void addInterpArrayList(OldBaileyInterp oldBaileyInterp) {
        this.interpArrayList.add(oldBaileyInterp);
    }

    public void addInterpToModel (Model namedModel) throws UnsupportedEncodingException {
        Resource subjectResource = namedModel.createResource(ResourcesUri.oldbaily+id);
        Resource objectResource = namedModel.createResource(ResourcesUri.oldbaily+"Trial");
        Statement meta = namedModel.createStatement(subjectResource, RDF.type, objectResource);
        namedModel.add(meta);
        for (int i = 0; i < interpArrayList.size(); i++) {
            OldBaileyInterp oldBaileyInterp = interpArrayList.get(i);
            oldBaileyInterp.addToModel(namedModel);
        }
    }
}
