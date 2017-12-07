import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by piek on 05/12/2017.
 */
public class OldBaileyXml extends org.xml.sax.helpers.DefaultHandler {
    static String value = "";
    static boolean PERSON = false;
    static String caseId = "";
    static HashMap<String, OldBaileyData> fileData = new HashMap<String, OldBaileyData>();
    static OldBaileyData data = new OldBaileyData();
    static OldBaileyPerson person = new OldBaileyPerson();

    static public void main(String[] args) {
        String trigFolder = "";
        String ext = "";
        String xmlFolder = "";
        xmlFolder = "/Code//vu/OldBailey/example/xml";
        trigFolder = "/Code//vu/OldBailey/example/trig";
        ext = ".trig";
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("--xml") && args.length>(i+1)) {
              xmlFolder = args[i+1];
            }
            else if (arg.equals("--trig") && args.length>(i+1)) {
              trigFolder = args[i+1];
            }
            else if (arg.equals("--extension") && args.length>(i+1)) {
              ext = args[i+1];
            }
        }
        ArrayList<File> xmlFiles = Util.makeFlatFileList(new File(xmlFolder), ".xml");
        ArrayList<File> trigFiles = Util.makeFlatFileList(new File(trigFolder), ext);
        System.out.println("xmlFiles.size() = " + xmlFiles.size());
        System.out.println("trigFiles.size() = " + trigFiles.size());
        HashMap<String, ArrayList<File>> map = makeFileIdentifierMap(xmlFiles, trigFiles);
        for (int i = 0; i < xmlFiles.size(); i++) {
            File xmlFile = xmlFiles.get(i);
            System.out.println("xmlFile.getName() = " + xmlFile.getName());
            processXmlFiles(xmlFile, map);
           // processXmlFiles(xmlFile, trigFiles);
           // break;
        }
    }


    static HashMap<String, ArrayList<File>> makeFileIdentifierMap (ArrayList<File> xmlFiles, ArrayList<File> trigFiles) {
        HashMap<String, ArrayList<File>> map = new HashMap<String, ArrayList<File>>();
        for (int x = 0; x < xmlFiles.size(); x++) {
            File xmlFile = xmlFiles.get(x);
            String fileIdentifier = xmlFile.getName();
            ///OBC2POS-18390513.xml
            int idx_s = fileIdentifier.indexOf("-");
            int idx_e = fileIdentifier.indexOf(".xml");
            if (idx_s>-1 && idx_e>-1)  {
                fileIdentifier = "t"+fileIdentifier.substring(idx_s+1, idx_e);
                // System.out.println("fileIdentifier = " + fileIdentifier);
            }
            OldBaileyXml oldBaileysXml = new OldBaileyXml();
            for (int i = 0; i < trigFiles.size(); i++) {
                File trigFile = trigFiles.get(i);
                //System.out.println("trigFile.getName() = " + trigFile.getName());
                if (trigFile.getName().startsWith(fileIdentifier)) {
                    if (map.containsKey(fileIdentifier)) {
                        ArrayList<File>  files = map.get(fileIdentifier);
                        files.add(trigFile);
                        map.put(fileIdentifier, files);
                    }
                    else {
                        ArrayList<File>  files = new ArrayList<File>();
                        files.add(trigFile);
                        map.put(fileIdentifier, files);
                    }
                }
            }
        }
        return map;
    }

    static void processXmlFiles (File xmlFile, HashMap<String, ArrayList<File>> trigFileMap) {
        OldBaileyXml oldBaileysXml = new OldBaileyXml();
        oldBaileysXml.parseFile(xmlFile);
        String fileIdentifier = xmlFile.getName();
        ///OBC2POS-18390513.xml
        int idx_s = fileIdentifier.indexOf("-");
        int idx_e = fileIdentifier.indexOf(".xml");
        if (idx_s>-1 && idx_e>-1)  {
            fileIdentifier = "t"+fileIdentifier.substring(idx_s+1, idx_e);
            // System.out.println("fileIdentifier = " + fileIdentifier);
        }
        if (trigFileMap.containsKey(fileIdentifier)) {
            ArrayList<File> trigFiles = trigFileMap.get(fileIdentifier);
            for (int i = 0; i < trigFiles.size(); i++) {
                File trigFile = trigFiles.get(i);
                //System.out.println("trigFile.getName() = " + trigFile.getName());
                if (trigFile.getName().startsWith(fileIdentifier)) {
                    /// the meta data applies to this trigFile
                    Dataset dataset = TDBFactory.createDataset();
                    try {
                        OutputStream fos = new FileOutputStream(trigFile.getAbsoluteFile() + ".meta");
                        dataset = RDFDataMgr.loadDataset(trigFile.getAbsolutePath());
                        adaptTriples(dataset, OldBaileyXml.fileData);
                        removeTriples(dataset, fileIdentifier);
                        RDFDataMgr.write(fos, dataset, RDFFormat.TRIG_PRETTY);
                        fos.close();
                        //break;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    static void processXmlFiles (File xmlFile, ArrayList<File> trigFiles) {
        OldBaileyXml oldBaileysXml = new OldBaileyXml();
        oldBaileysXml.parseFile(xmlFile);
        String fileIdentifier = xmlFile.getName();
        ///OBC2POS-18390513.xml
        int idx_s = fileIdentifier.indexOf("-");
        int idx_e = fileIdentifier.indexOf(".xml");
        if (idx_s>-1 && idx_e>-1)  {
            fileIdentifier = "t"+fileIdentifier.substring(idx_s+1, idx_e);
            // System.out.println("fileIdentifier = " + fileIdentifier);
        }
        for (int i = 0; i < trigFiles.size(); i++) {
            File trigFile = trigFiles.get(i);
            //System.out.println("trigFile.getName() = " + trigFile.getName());
            if (trigFile.getName().startsWith(fileIdentifier)) {
                /// the meta data applies to this trigFile
                Dataset dataset = TDBFactory.createDataset();
                try {
                    OutputStream fos = new FileOutputStream(trigFile.getAbsoluteFile()+".meta");
                    dataset = RDFDataMgr.loadDataset(trigFile.getAbsolutePath());
                    adaptTriples(dataset, OldBaileyXml.fileData);
                    removeTriples(dataset, fileIdentifier);
                    RDFDataMgr.write(fos, dataset, RDFFormat.TRIG_PRETTY);
                    fos.close();
                    //break;

                }
                catch (Exception e)   {
                    e.printStackTrace();
                }
            }
        }
    }


    static void removeTriples (Dataset dataset, String caseId) {
        dataset.getDefaultModel().setNsPrefix("oldbailey", ResourcesUri.oldbaily);
        dataset.removeNamedModel(ResourcesUri.provenanceGraph);
        Model instanceModel = dataset.getNamedModel(ResourcesUri.instanceGraph);
        HashMap<String, String> rename = new HashMap<String, String>();
        ArrayList<Statement> removedStatements = new ArrayList<Statement>();
        StmtIterator siter = instanceModel.listStatements();
        while (siter.hasNext()) {
            Statement s = siter.nextStatement();
            if (s.getPredicate().getLocalName().equals("label")) {
                removedStatements.add(s);
            }
            else  if (s.getPredicate().getLocalName().equals("count")) {
                removedStatements.add(s);
            }
            else  if (s.getPredicate().getLocalName().equals("phrasecount")) {
                removedStatements.add(s);
            }
            else {
                if (s.getSubject().getURI().indexOf("dbpedia")>-1) {
                    /**
                     *     <http://dbpedia.org/resource/Henry_Johnson_(Louisiana)>
                     gaf:denotedBy   <http://cltl.nl/old_bailey/sessionpaper/t18390513-1565#char=8,21&word=w3,w4&term=t3,t4&sentence=2&paragraph=1> ;
                     <http://www.newsreader-project.eu//phrasecount>
                     <http://dbpedia.org/resource/Henry_Johnson_(Louisiana)#0> ;
                     skos:prefLabel  "HENRY JOHNSON" .
                     */
                    if (s.getPredicate().getLocalName().equals("prefLabel")) {
                        try {
                            String uri  = ResourcesUri.oldbaily +caseId+"/entities/"+ URLEncoder.encode(s.getObject().asLiteral().toString(), "UTF-8").toLowerCase();

                            //System.out.println("s.getSubject().getURI() = " + s.getSubject().getURI());
                            rename.put(s.getSubject().getURI(), uri);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                    }
                    //removedStatements.add(s);
                }
                String subject = s.getSubject().getLocalName();
                if (subject.startsWith("ev")) {
                    //// event....
                    if (s.getPredicate().getLocalName().equals("relatedMatch")) {
                        removedStatements.add(s);
                    }
                }
            }
        }
        instanceModel.remove(removedStatements);

        //replace dbpedia URIs
        removedStatements = new ArrayList<Statement>();
        ArrayList<Statement> newStatements = new ArrayList<Statement>();
        siter = instanceModel.listStatements();
        while (siter.hasNext()) {
            Statement s = siter.nextStatement();
            if (s.getSubject().getURI().indexOf("dbpedia")>-1) {
                /**
                 *     <http://dbpedia.org/resource/Henry_Johnson_(Louisiana)>
                 gaf:denotedBy   <http://cltl.nl/old_bailey/sessionpaper/t18390513-1565#char=8,21&word=w3,w4&term=t3,t4&sentence=2&paragraph=1> ;
                 <http://www.newsreader-project.eu//phrasecount>
                 <http://dbpedia.org/resource/Henry_Johnson_(Louisiana)#0> ;
                 skos:prefLabel  "HENRY JOHNSON" .
                 */

                if (rename.containsKey(s.getSubject().getURI())) {
                    String newUri = rename.get(s.getSubject().getURI());
                    Resource resource = instanceModel.createResource(newUri);
                    Statement newS = instanceModel.createStatement(resource, s.getPredicate(), s.getObject());
                    newStatements.add(newS);
                    Resource ont = instanceModel.createResource(ResourcesUri.nwrontology+"ENTITY");
                    Statement typeS = instanceModel.createStatement(resource, RDF.type, ont);
                    newStatements.add(typeS);
                    removedStatements.add(s);
                }
            }
        }
        instanceModel.remove(removedStatements);
        instanceModel.add(newStatements);

        Iterator<String> models = dataset.listNames();
        ArrayList<String> relModels = new ArrayList<String>();
        while (models.hasNext()) {
            String model = models.next();
           // System.out.println("model = " + model);
            if (
                    !model.equals(ResourcesUri.provenanceGraph) &&
                    !model.equals(ResourcesUri.instanceGraph) &&
                    !model.equals(ResourcesUri.graspGraph)
                    ) {
                Model relationModel = dataset.getNamedModel(model);
                relModels.add(model);
                siter = relationModel.listStatements();
                while (siter.hasNext()) {
                    Statement s = siter.nextStatement();
                   // System.out.println("s.getObject().toString() = " + s.getObject().toString());
                    if (s.getObject().toString().indexOf("dbpedia")>-1) {
                        //removedStatements.add(s);
                        if (rename.containsKey(s.getObject().toString())) {
                            String newUri = rename.get(s.getObject().toString());
                            Resource resource = instanceModel.createResource(newUri);
                            Statement newS = instanceModel.createStatement(s.getSubject(), s.getPredicate(), resource);
                            instanceModel.add(newS);
                        }
                    }
                   // System.out.println("s.getNameSpace() = " + s.getPredicate().getNameSpace());
                    else if (s.getPredicate().getNameSpace().indexOf("domain-ontology")>-1) {
                        //removedStatements.add(s);
                    }
                    else if (s.getPredicate().getNameSpace().indexOf("propbank")>-1) {
                        //removedStatements.add(s);
                    }
                    else if (s.getPredicate().getNameSpace().indexOf("domain-ontology")>-1) {
                        //removedStatements.add(s);
                    }
                    else if (s.getPredicate().getLocalName().indexOf("hasAtTime")>-1) {
                        //removedStatements.add(s);
                    }
                    else {
                       instanceModel.add(s);
                    }
                }
            }
        }
        for (int i = 0; i < relModels.size(); i++) {
            String m = relModels.get(i);
            dataset.removeNamedModel(m);
        }
    }

    static void adaptTriples (Dataset dataset, HashMap<String, OldBaileyData> dataHashMap) {
        Model namedModel = dataset.getNamedModel(ResourcesUri.instanceGraph);
        ArrayList<Statement> newStatements = new ArrayList<Statement>();
        StmtIterator siter = namedModel.listStatements();
        while (siter.hasNext()) {
            Statement s = siter.nextStatement();
            String subject = s.getSubject().getLocalName();
            if (subject.startsWith("ev")) {
                //// event....
                String caseId = s.getSubject().getNameSpace(); //http://cltl.nl/old_bailey/sessionpaper/t18390513-1553#
                int idx_s =  caseId.lastIndexOf("/");
                if (idx_s>-1 && idx_s<(caseId.length()-1)) {
                    caseId = caseId.substring(idx_s+1, caseId.length()-1);
                    if (fileData.containsKey(caseId)) {
                        OldBaileyData data = fileData.get(caseId);
                        ArrayList<Statement> statements = data.getStatement(namedModel, s.getSubject());
                        newStatements.addAll(statements);
                    } else {
                     //   System.out.println("fileData = " + fileData.size());
                     //   System.out.println("no data for caseid:" + caseId);
                    }
                }
            }
        }
       // System.out.println("newStatements.size() = " + newStatements.size());
        namedModel.add(newStatements);
    }


    public OldBaileyXml() {
        init();
    }


    public void init () {
        fileData = new HashMap<String, OldBaileyData>();
        data = new OldBaileyData();
        person = new OldBaileyPerson();
        PERSON = false;
        caseId = "";
    }

    public boolean parseFile(File file) {
        try {
            FileReader reader = new FileReader(file);
            InputSource inp = new InputSource(reader);
            boolean result = parseFile(inp);
            reader.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("file.getName() = " + file.getName());
            return false;
        }
    }

    public boolean parseFile(InputStream stream)
    {
        InputSource source = new InputSource(stream);
        boolean result = parseFile(source);
        try
        {stream.close();}
        catch (IOException e)
        {}
        return result;
    }

    public boolean parseFile(InputSource source)
    {
        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);
            SAXParser parser = factory.newSAXParser();
            parser.parse(source, this);
            return true;
        }
        catch (FactoryConfigurationError factoryConfigurationError)
        {
            factoryConfigurationError.printStackTrace();
        }
        catch (ParserConfigurationException e)
        {
            e.printStackTrace();
        }
        catch (SAXException e)
        {
            //System.out.println("last value = " + previousvalue);
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // e.printStackTrace();
        }
        return false;
    }
    /*
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
    public void startElement(String uri, String localName,
                             String qName, Attributes attributes)
            throws SAXException {
        value = "";
        if (qName.equalsIgnoreCase("interp")) {
            String inst = attributes.getValue("inst");
            if (inst.indexOf("-name-")>-1) {
                //// this is a name instance and not a case id
                //// we ignore it
            }
            else if (inst.startsWith("def1-")) {
                //// this is a name instance and not a case id
                //// we ignore it
            }
            else {
                caseId = inst;
            }
            if (caseId!=null && caseId.startsWith("t")) {
                String[] fields = caseId.split("-");
                if (fields.length >= 2) {
                    caseId = fields[0] + "-" + fields[1];
                    //System.out.println("caseId = " + caseId);
                    String type = attributes.getValue("type");
                    String value = attributes.getValue("value");
                    //if (type.equals("age")) System.out.println("value = " + value);
                    if (type != null && value != null) {
                        if (!PERSON) {
                            if (fileData.containsKey(caseId)) {
                                OldBaileyData data = fileData.get(caseId);
                                data.setValue(type, value);
                                fileData.put(caseId, data);
                            } else {
                                OldBaileyData data = new OldBaileyData();
                                data.setValue(type, value);
                                data.setCaseid(caseId);
                                fileData.put(caseId, data);
                            }
                        }
                        else {
                            if (person!=null) person.setValue(type, value);
                        }
                    }
                }
            }
            else {
                caseId = "";
            }
        }
        /*
        <persName id="def1-1621-18390513" type="defendantName">
         <interp inst="def1-1621-18390513" type="gender" value="male"/>
         <interp inst="def1-1621-18390513" type="age" value="21"/>
         <interp inst="def1-1621-18390513" type="surname" value="ATKINSON"/>
         <interp inst="def1-1621-18390513" type="given" value="EDWARD"/>
         <hi rend="largeCaps">EDWARD ATKINSON</hi>
         </persName>
         */
        else if (qName.equalsIgnoreCase("persName")) {
            PERSON = true;
            person = new OldBaileyPerson();
            String type = attributes.getValue("type");
            //System.out.println(caseId+": type = " + type);
            if (type!=null) {
                person.setRole(type);
            }
        }
        else {
           ////
        }
    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (qName.equalsIgnoreCase("persName")) {
            PERSON = false;
            if (!caseId.isEmpty() && person!=null) {
              //  System.out.println("caseId = " + caseId);
                if (fileData.containsKey(caseId)) {
                    OldBaileyData data = fileData.get(caseId);
                    data.addPersons(person);
                    fileData.put(caseId, data);
                } else {
                    OldBaileyData data = new OldBaileyData();
                    data.addPersons(person);
                    fileData.put(caseId, data);
                }
               // System.out.println("person.getRole() = " + person.getRole());
               // System.out.println("ADDED");
            }
            person = null;
        }
    }

    public void characters(char ch[], int start, int length)
            throws SAXException {
        value += new String(ch, start, length);
    }

}

