package ob;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDBFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OldBaileyRdf {

    static public HashMap<String,OldBaileyPerson> personMap = new HashMap<>();
    static public HashMap<String,OldBaileyPlace> placeMap = new HashMap<>();
    static public HashMap<String,OldBaileyRs> rsMap = new HashMap<>();
    static public HashMap<String,OldBaileyTrial> trialMap = new HashMap<>();


    static String testOrdinary = "--xml-folder /Users/piek/Desktop/DigHum-2018/4775434/OBO_XML_7-2/ordinarysAccounts --extension .xml --rdf-folder /Users/piek/Desktop/DigHum-2018/4775434/OBO_XML_7-2/ordinarysAccounts/rdf";
    static String testSessions = "--xml-folder /Users/piek/Desktop/DigHum-2018/4775434/OBO_XML_7-2/sessionsPapers --extension .xml --rdf-folder /Users/piek/Desktop/DigHum-2018/4775434/OBO_XML_7-2/sessionsPapers/rdf";

    static public void main(String[] args) {
        File rdfFolder = null;
        boolean singleoutputfile = false;
        String ext = "";
        File xmlFolder = null;
        File xmlFile = null;
        ext = ".xml";
        //args = testOrdinary.split(" ");
        //args = testSessions.split(" ");

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("--xml-folder") && args.length > (i + 1)) {
                xmlFolder = new File(args[i + 1]);
            }
            else if (arg.equals("--xml-file") && args.length > (i + 1)) {
                xmlFile = new File(args[i + 1]);
                singleoutputfile=true;
            }
            else if (arg.equals("--rdf-folder") && args.length > (i + 1)) {
                rdfFolder = new File(args[i + 1]);
            }
            else if (arg.equals("--extension") && args.length > (i + 1)) {
                ext = args[i + 1];
            }
        }

        OldBaileyXml oldBaileyXml = new OldBaileyXml();
        ArrayList<File> xmlFiles = new ArrayList<>();
        if (singleoutputfile && xmlFile!=null) {
            xmlFiles.add(xmlFile);
        }
        else {
            if (xmlFolder!=null) {
                xmlFiles = OBHelper.makeFlatFileList(xmlFolder, ext);
            }
        }
        System.out.println("xmlFiles.size() = " + xmlFiles.size());
        if (singleoutputfile) {
            Dataset dataset = TDBFactory.createDataset();
            Model instanceModel = dataset.getDefaultModel();
            instanceModel.setNsPrefix("oldbailey", ResourcesUri.oldbaily);
            for (int i = 0; i < xmlFiles.size(); i++) {
                File file = xmlFiles.get(i);
                System.out.println("file.getName() = " + file.getName());
                init();
                oldBaileyXml = new OldBaileyXml();
                oldBaileyXml.parseFile(file);
                getMaps(oldBaileyXml);
                addToRdf(instanceModel);
            }
            try {
                File rdfFile = new File(xmlFile.getAbsolutePath()  + ".ob-meta.trig");
                OutputStream fos = new FileOutputStream(rdfFile);
                RDFDataMgr.write(fos, dataset, RDFFormat.TRIG_PRETTY);
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            if (!rdfFolder.exists()) {
                        rdfFolder.mkdir();
                        System.out.println("xmlFolder.getAbsolutePath() = " + xmlFolder.getAbsolutePath());
                }
                if (rdfFolder.exists()) {
                    for (int i = 0; i < xmlFiles.size(); i++) {
                        File file = xmlFiles.get(i);
                        System.out.println("file.getName() = " + file.getName());
                        init();
                        oldBaileyXml = new OldBaileyXml();
                        oldBaileyXml.parseFile(file);
                        getMaps(oldBaileyXml);
                        File rdfFile = new File(rdfFolder.getAbsolutePath() + "/" + file.getName() + ".trig");
                        outputRdf(rdfFile);
                        // break;
                    }
                }
                else {
                    System.out.println("rdfFolder = " + rdfFolder.exists());
                    System.out.println("rdfFolder.getAbsolutePath() = " + rdfFolder.getAbsolutePath());
                }
        }
    }

    static void init() {
        personMap = new HashMap<>();
        placeMap = new HashMap<>();
        rsMap = new HashMap<>();
        trialMap = new HashMap<>();

    }
    static public void outputRdf (File outputRdf) {
        Dataset dataset = TDBFactory.createDataset();
        Model instanceModel = dataset.getDefaultModel();
        instanceModel.setNsPrefix("oldbailey", ResourcesUri.oldbaily);
        addToRdf(instanceModel);
        try {
            OutputStream fos = new FileOutputStream(outputRdf);
            RDFDataMgr.write(fos, dataset, RDFFormat.TRIG_PRETTY);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static public void addToRdf (Model instanceModel) {
         try {
            for (Map.Entry<String,OldBaileyPerson> entry : personMap.entrySet()) {
                OldBaileyPerson person = entry.getValue();
                person.addToModel(instanceModel);
            }
            for (Map.Entry<String,OldBaileyPlace> entry : placeMap.entrySet()) {
                OldBaileyPlace place = entry.getValue();
                place.addToModel(instanceModel);
            }
            for (Map.Entry<String,OldBaileyRs> entry : rsMap.entrySet()) {
                OldBaileyRs rs = entry.getValue();
                rs.addToModel(instanceModel);
            }
            for (Map.Entry<String,OldBaileyTrial> entry : trialMap.entrySet()) {
                OldBaileyTrial trial = entry.getValue();
                trial.addInterpToModel(instanceModel);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    static public void getMaps (OldBaileyXml oldBaileyXml) {
        for (int i = 0; i < oldBaileyXml.personArrayList.size(); i++) {
            OldBaileyPerson oldBaileyPerson = oldBaileyXml.personArrayList.get(i);
            personMap.put(oldBaileyPerson.getId(), oldBaileyPerson);
        }
        for (int i = 0; i < oldBaileyXml.placeArrayList.size(); i++) {
            OldBaileyPlace oldBaileyPlace = oldBaileyXml.placeArrayList.get(i);
            placeMap.put(oldBaileyPlace.getId(), oldBaileyPlace);
        }
        for (int i = 0; i < oldBaileyXml.rsArrayList.size(); i++) {
            OldBaileyRs oldBaileyRs = oldBaileyXml.rsArrayList.get(i);
            rsMap.put(oldBaileyRs.getId(), oldBaileyRs);
        }
        for (int i = 0; i < oldBaileyXml.trialArrayList.size(); i++) {
            OldBaileyTrial oldBaileyTrial = oldBaileyXml.trialArrayList.get(i);
            trialMap.put(oldBaileyTrial.getId(), oldBaileyTrial);
        }
        for (int i = 0; i < oldBaileyXml.interpArrayList.size(); i++) {
            OldBaileyInterp oldBaileyInterp = oldBaileyXml.interpArrayList.get(i);
            if (personMap.containsKey(oldBaileyInterp.getInst())) {
                OldBaileyPerson oldBaileyPerson = personMap.get(oldBaileyInterp.getInst());
                oldBaileyPerson.addInterpArrayList(oldBaileyInterp);
                personMap.put(oldBaileyInterp.getInst(), oldBaileyPerson);
            }
            else if (placeMap.containsKey(oldBaileyInterp.getInst())) {
                OldBaileyPlace oldBaileyPlace  = placeMap.get(oldBaileyInterp.getInst());
                oldBaileyPlace.addInterpArrayList(oldBaileyInterp);
                placeMap.put(oldBaileyInterp.getInst(), oldBaileyPlace);
            }
            else if (rsMap.containsKey(oldBaileyInterp.getInst())) {
                OldBaileyRs oldBaileyRs  = rsMap.get(oldBaileyInterp.getInst());
                oldBaileyRs.addInterpArrayList(oldBaileyInterp);
                rsMap.put(oldBaileyInterp.getInst(), oldBaileyRs);
            }
            else if (trialMap.containsKey(oldBaileyInterp.getInst())) {
                OldBaileyTrial oldBaileyTrial  = trialMap.get(oldBaileyInterp.getInst());
                oldBaileyTrial.addInterpArrayList(oldBaileyInterp);
                trialMap.put(oldBaileyInterp.getInst(), oldBaileyTrial);
            }
            else {
/*
                System.out.println("oldBaileyInterp.getInst() = " + oldBaileyInterp.getInst());
                System.out.println("oldBaileyInterp.getType() = " + oldBaileyInterp.getType());
                System.out.println("oldBaileyInterp.getValue() = " + oldBaileyInterp.getValue());
*/
            }
        }

        for (int i = 0; i < oldBaileyXml.joinArrayList.size(); i++) {
            OldBaileyJoin join =  oldBaileyXml.joinArrayList.get(i);
            if (join.getResult().equalsIgnoreCase("persNamePlace")) {
                if (personMap.containsKey(join.getSubject())) {
                    OldBaileyPerson oldBaileyPerson = personMap.get(join.getSubject());
                    OldBaileyInterp interp = new OldBaileyInterp(join.getSubject(), "origin", join.getObject());
                    oldBaileyPerson.addInterpArrayList(interp);
                    personMap.put(join.getSubject(),oldBaileyPerson);
                }
            }
            else if (join.getResult().equalsIgnoreCase("persNameOccupation")) {
                if (personMap.containsKey(join.getSubject())) {
                    OldBaileyPerson oldBaileyPerson = personMap.get(join.getSubject());
                    OldBaileyInterp interp = new OldBaileyInterp(join.getSubject(), "occupation", join.getObject());
                    oldBaileyPerson.addInterpArrayList(interp);
                    personMap.put(join.getSubject(),oldBaileyPerson);
                }
            }
            else if (join.getResult().equalsIgnoreCase("criminalCharge")) {
                /*
join.getSubject() = t18070408-1-defend52
join.getObject() = t18070408-1-off1
                 */
                OldBaileyInterp interp = new OldBaileyInterp(join.getObject(), join.getResult(), join.getSubject());
                if (rsMap.containsKey(join.getObject())) {
                    OldBaileyRs rs = rsMap.get(join.getObject());
                    rs.addInterpArrayList(interp);
                    rsMap.put(join.getObject(), rs);
                }
            }
            else if (join.getResult().equalsIgnoreCase("offenceVictim")) {
                /*
join.getSubject() = t18070408-1-off1
join.getObject() = t18070408-1-victim54
                 */
                OldBaileyInterp interp = new OldBaileyInterp(join.getSubject(), join.getResult(), join.getObject());
                if (rsMap.containsKey(join.getSubject())) {
                    OldBaileyRs rs = rsMap.get(join.getSubject());
                    rs.addInterpArrayList(interp);
                    rsMap.put(join.getSubject(), rs);
                }
            }
            else if (join.getResult().equalsIgnoreCase("offenceCrimeDate")) {
                /*
join.getSubject() = t18070408-1-off1
join.getObject() = t18070408-1-cd2
                 */

                OldBaileyInterp interp = new OldBaileyInterp(join.getSubject(), join.getResult(), join.getObject());
                if (rsMap.containsKey(join.getSubject())) {
                    OldBaileyRs rs = rsMap.get(join.getSubject());
                    rs.addInterpArrayList(interp);
                    rsMap.put(join.getSubject(), rs);
                }
            }
            else if (join.getResult().equalsIgnoreCase("offencePlace")) {
                /*
join.getSubject() = t18070408-1-off1
join.getObject() = t18070408-1-crimeloc4
                 */

                OldBaileyInterp interp = new OldBaileyInterp(join.getSubject(), join.getResult(), join.getObject());
                if (rsMap.containsKey(join.getSubject())) {
                    OldBaileyRs rs = rsMap.get(join.getSubject());
                    rs.addInterpArrayList(interp);
                    rsMap.put(join.getSubject(), rs);
                }
            }
            else if (join.getResult().equalsIgnoreCase("defendantPunishment")) {
                /*
join.getSubject() = t18070408-1-defend52
join.getObject() = t18070408-1-punish6
                 */
                OldBaileyInterp interp = new OldBaileyInterp(join.getObject(), join.getResult(), join.getSubject());
                if (rsMap.containsKey(join.getObject())) {
                    OldBaileyRs rs = rsMap.get(join.getObject());
                    rs.addInterpArrayList(interp);
                    rsMap.put(join.getObject(), rs);
                }
            }
            else {
/*
                System.out.println("join.getResult() = " + join.getResult());
                System.out.println("join.getSubject() = " + join.getSubject());
                System.out.println("join.getObject() = " + join.getObject());
*/
            }
        }
    }
}
