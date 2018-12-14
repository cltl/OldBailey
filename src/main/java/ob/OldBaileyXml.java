package ob;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class OldBaileyXml extends org.xml.sax.helpers.DefaultHandler {

    static String value = "";
    static String caseId = "";
    static public ArrayList<OldBaileyTrial> trialArrayList = new ArrayList<>();
    static public ArrayList<OldBaileyPerson> personArrayList = new ArrayList<>();
    static public ArrayList<OldBaileyPlace> placeArrayList = new ArrayList<>();
    static public ArrayList<OldBaileyJoin> joinArrayList;
    static public ArrayList<OldBaileyInterp> interpArrayList;
    static public ArrayList<OldBaileyRs> rsArrayList;

    public OldBaileyXml() {
        init();
    }


    public void init () {
        caseId = "";
        trialArrayList = new ArrayList<>();
        personArrayList = new ArrayList<>();
        placeArrayList = new ArrayList<>();
        joinArrayList = new ArrayList<>();
        interpArrayList = new ArrayList<>();
        rsArrayList = new ArrayList<>();
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
                String type = attributes.getValue("type");
                String avalue = attributes.getValue("value");
                OldBaileyInterp interp = new OldBaileyInterp(inst, type, avalue);
                if (trialArrayList.size()>0) {
                    String trialId = trialArrayList.get(trialArrayList.size()-1).getId();
                    interp.setTrial(trialId);
                }
                interpArrayList.add(interp);
        }
        else if (qName.equalsIgnoreCase("join")) {
                String result = attributes.getValue("result");
                String targOrder = attributes.getValue("targOrder");
                String targetString = attributes.getValue("targets");
                String [] fields = targetString.split(" ");
                String subject = fields[0];
                for (int i = 1; i < fields.length; i++) {
                    String field = fields[i];
                    OldBaileyJoin join = new OldBaileyJoin(result, targOrder, subject, field);
                    joinArrayList.add(join);
                }
        }
        else if (qName.equalsIgnoreCase("rs")) {
                String id = attributes.getValue("id");
                String type = attributes.getValue("type");
                OldBaileyRs rs = new OldBaileyRs(id, type);
                rsArrayList.add(rs);
        }
        else if (qName.equalsIgnoreCase("div1")) {
                String type = attributes.getValue("type");
                if (type.equalsIgnoreCase("trialAccount")) {
                    String id = attributes.getValue("id");
                    OldBaileyTrial trial = new OldBaileyTrial(id);
                    trialArrayList.add(trial);
                }
        }
        else if (qName.equalsIgnoreCase("persName")) {
            OldBaileyPerson person = new OldBaileyPerson();
            String id = attributes.getValue("id");
            if (id!=null) {
                person.setId(id);
            }
            String type = attributes.getValue("type");
            //System.out.println(caseId+": type = " + type);
            if (type!=null) {
                person.setType(type);
            }
            if (trialArrayList.size()>0) {
                String trialId = trialArrayList.get(trialArrayList.size()-1).getId();
                person.setTrial(trialId);
            }
            personArrayList.add(person);
        }
        else if (qName.equalsIgnoreCase("placeName")) {
            OldBaileyPlace place = new OldBaileyPlace();
            String id = attributes.getValue("id");
            if (id!=null) {
                place.setId(id);
            }

            if (trialArrayList.size()>0) {
                String trialId = trialArrayList.get(trialArrayList.size()-1).getId();
                place.setTrial(trialId);
            }
            placeArrayList.add(place);
        }
        else {
           ////
        }
    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (qName.equalsIgnoreCase("persName")) {
            String mention = cleanValue(value).trim();
            if (!mention.isEmpty()) {
                personArrayList.get(personArrayList.size() - 1).setMention(mention);
            }
        }
        else if (qName.equalsIgnoreCase("placeName")) {
            String mention = cleanValue(value).trim();
            if (!mention.isEmpty()) {
                placeArrayList.get(placeArrayList.size()-1).setMention(mention);
            }
        }
        else if (qName.equalsIgnoreCase("rs")) {
            String mention = cleanValue(value).trim();
            if (!mention.isEmpty()) {
                rsArrayList.get(rsArrayList.size()-1).setMention(mention);
            }
        }
    }

    String cleanValue (String value) {
        String clean = "";
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c!='\n') {
               clean+=c;
            }
        }
        return clean;
    }
    public void characters(char ch[], int start, int length)
            throws SAXException {
        value += new String(ch, start, length);
    }


}


