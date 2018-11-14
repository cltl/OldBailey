package ob;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class OldBaileyPlace {
    /*
          <placeName id="OA16980309-geo-1">
                        <interp inst="OA16980309-geo-1" type="type" value="parish"/>
                        <join result="persNamePlace" targOrder="Y"
                              targets="OA16980309n24-1 n24-3 OA16980309-geo-1"/>Aldgate Parish</placeName>

     */

    private String id;
    private ArrayList<OldBaileyInterp> interpArrayList;

    private String mention;

    public OldBaileyPlace() {
        this.id = "";
        this.mention = "";
        this.interpArrayList = new ArrayList<>();
    }


    public String getMention() {
        return mention;
    }

    public void setMention(String mention) {
        this.mention = mention;
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

    public void addToModel(Model namedModel) throws UnsupportedEncodingException {
        Resource subjectResource = namedModel.createResource(ResourcesUri.oldbaily+id);
        Resource objectResource = namedModel.createResource(ResourcesUri.oldbaily+"Place");
        com.hp.hpl.jena.rdf.model.Statement meta = namedModel.createStatement(subjectResource, RDF.type, objectResource);
        namedModel.add(meta);
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
