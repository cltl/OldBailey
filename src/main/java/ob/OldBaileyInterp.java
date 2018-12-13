package ob;

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
    private String trial;
    private String value;

    public OldBaileyInterp(String inst, String type, String value) {
        this.inst = inst;
        this.type = type;
        this.trial = "";
        this.value = value;
    }

    public OldBaileyInterp() {
        this.inst = "";
        this.type = "";
        this.trial = "";
        this.value = "";
    }

    public String getTrial() {
        return trial;
    }

    public void setTrial(String trial) {
        this.trial = trial;
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

    public String getValue_replace_space () {
        return value.replaceAll(" ","_");
    }

    public void addToModel(Model namedModel) throws UnsupportedEncodingException {
        Property metaProperty = namedModel.createProperty(ResourcesUri.oldbaily, this.getType());
        Resource subjectResource = namedModel.createResource(ResourcesUri.oldbaily+this.getInst());
        if (type.equalsIgnoreCase("given") || type.equalsIgnoreCase("surname")) {
            com.hp.hpl.jena.rdf.model.Statement meta = namedModel.createStatement(subjectResource, metaProperty, this.value);
            namedModel.add(meta);
        }
        else if (type.equalsIgnoreCase("age")) {
            com.hp.hpl.jena.rdf.model.Statement meta = namedModel.createStatement(subjectResource, metaProperty, this.value);
            namedModel.add(meta);
        }
        else if (type.equalsIgnoreCase("placeName")) {
            com.hp.hpl.jena.rdf.model.Statement meta = namedModel.createStatement(subjectResource, metaProperty, this.value);
            namedModel.add(meta);
        }

        else if (!trial.isEmpty()) {
            Resource objectResource = namedModel.createResource(ResourcesUri.oldbaily+trial);
            metaProperty = namedModel.createProperty(ResourcesUri.oldbaily, "trial");
            com.hp.hpl.jena.rdf.model.Statement meta = namedModel.createStatement(subjectResource, metaProperty, objectResource);
            namedModel.add(meta);
        }
        else {
            String uri  = ResourcesUri.oldbaily + this.getValue_replace_space();
            Resource objectResource = namedModel.createResource(uri);
            com.hp.hpl.jena.rdf.model.Statement meta = namedModel.createStatement(subjectResource, metaProperty, objectResource);
            namedModel.add(meta);
        }
    }

    /*

        if (!this.year.isEmpty()) {
            Property year = model.createProperty(ob.ResourcesUri.owltime+"year");
            resource.addProperty(year, this.getYear(),XSDDatatype.XSDgYear);
            Property unit = model.createProperty(ob.ResourcesUri.owltime+"unitType");
            Property day = model.createProperty(ob.ResourcesUri.owltime+"unitDay");
            resource.addProperty(unit, day);
        }
     */

}
