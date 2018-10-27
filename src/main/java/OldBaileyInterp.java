import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import java.io.UnsupportedEncodingException;

public class OldBaileyInterp {

    /*                 <interp inst="OA16980309n24-3" type="gender" value="male"/>
                        <interp inst="OA16980309n24-3" type="surname" value="Marslin"/>
                        <interp inst="OA16980309n24-3" type="given" value="Peter"/>
                        <interp inst="t19130107-2-punishment-1" type="punishmentCategory" value="imprison"/>
                        <interp inst="OA16980309-geo-1" type="type" value="parish"/>

     */

    private String inst;
    private String type;
    private String value;

    public OldBaileyInterp(String inst, String type, String value) {
        this.inst = inst;
        this.type = type;
        this.value = value;
    }

    public OldBaileyInterp() {
        this.inst = "";
        this.type = "";
        this.value = "";
    }

    public String getInst() {
        return inst;
    }

    public void setInst(String inst) {
        this.inst = inst;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void addToModel(Model namedModel) throws UnsupportedEncodingException {
        Property metaProperty = namedModel.createProperty(ResourcesUri.oldbaily, this.getType());
        Resource subjectResource = namedModel.createResource(this.getInst());
        //String uri  = ResourcesUri.oldbailyvalue + URLEncoder.encode(this.getValue(), "UTF-8").toLowerCase();
        //Resource objectResource = namedModel.createResource(uri);
        com.hp.hpl.jena.rdf.model.Statement meta = namedModel.createStatement(subjectResource, metaProperty, this.getValue());
        namedModel.add(meta);
    }

}
