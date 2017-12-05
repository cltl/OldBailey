import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.tdb.TDBFactory;
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
import java.util.ArrayList;
import java.util.HashMap;

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
        for (int i = 0; i < xmlFiles.size(); i++) {
            File xmlFile = xmlFiles.get(i);
            processXmlFiles(xmlFile, trigFiles);
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
                    RDFDataMgr.write(fos, dataset, RDFFormat.TRIG_PRETTY);
                    fos.close();
                }
                catch (Exception e)   {
                    e.printStackTrace();
                }
            }
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
                       // System.out.println("fileData = " + fileData.size());
                       // System.out.println("no data for caseid:" + caseId);
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
            caseId = attributes.getValue("inst");
            if (caseId!=null && caseId.startsWith("t")) {
                String[] fields = caseId.split("-");
                if (fields.length >= 2) {
                    caseId = fields[0] + "-" + fields[1];
                    //System.out.println("caseId = " + caseId);
                    String type = attributes.getValue("type");
                    String value = attributes.getValue("value");
                    if (type != null && value != null) {
                        if (!PERSON) {
                            if (fileData.containsKey(caseId)) {
                                OldBaileyData data = fileData.get(caseId);
                                data.setValue(type, value);
                                fileData.put(caseId, data);
                            } else {
                                OldBaileyData data = new OldBaileyData();
                                data.setValue(type, value);
                                fileData.put(caseId, data);
                            }
                        }
                        else {
                             person.setValue(type, value);
                        }
                    }
                }
            }
            else {
                caseId = "";
            }
        }
        else if (qName.equalsIgnoreCase("persName")) {
            PERSON = true;
            person = new OldBaileyPerson();
            String type = attributes.getValue("type");
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
            if (fileData.containsKey(caseId)) {
                OldBaileyData data = fileData.get(caseId);
                data.addPersons(person);
                fileData.put(caseId, data);
            } else {
                OldBaileyData data = new OldBaileyData();
                data.addPersons(person);
                fileData.put(caseId, data);
            }
        }
    }

    public void characters(char ch[], int start, int length)
            throws SAXException {
        value += new String(ch, start, length);
    }

}

