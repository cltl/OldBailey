package ob;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.*;

public class PrepareAmCatImport extends org.xml.sax.helpers.DefaultHandler {
    static String previousvalue = "";
    static String value = "";
    static String date = "";
    static String headline = "";
    static String medium = "OLDBAILEY";
    static String plainText = "";
    static File xmlFolder = null;
    static File textFolder = null;
    static HashMap<String, String> trials = new HashMap<String, String>();

    static String testOrdinary = "--xml-folder /Users/piek/Desktop/DigHum-2018/4775434/OBO_XML_7-2/ordinarysAccounts --extension .xml --text-folder /Users/piek/Desktop/DigHum-2018/4775434/OBO_XML_7-2/ordinarysAccounts/text --source ordinarysAccounts";
    static String testSessions = "--xml-folder /Users/piek/Desktop/DigHum-2018/4775434/OBO_XML_7-2/sessionsPapers/186 --extension .xml --text-folder /Users/piek/Desktop/DigHum-2018/4775434/OBO_XML_7-2/sessionsPapers/text186 --source sessionPapers";


    static public void main(String[] args) {
        PrepareAmCatImport prepareAmCatImport = new PrepareAmCatImport();
        String tsvFile = "";
        String ext = "";
        ext = ".xml";
        //args = testOrdinary.split(" ");
        //args = testSessions.split(" ");
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("--xml-folder") && args.length > (i + 1)) {
                xmlFolder = new File(args[i + 1]);
            }
            else if (arg.equals("--text-folder") && args.length > (i + 1)) {
                textFolder = new File(args[i + 1]);
            }
            else if (arg.equals("--extension") && args.length > (i + 1)) {
                ext = args[i + 1];
            }
            else if (arg.equals("--source") && args.length > (i + 1)) {
                medium = args[i + 1];
            }
        }
        if (!textFolder.exists()) {
            textFolder.mkdir();
        }
        try {
            if (textFolder.exists()) {
                tsvFile = textFolder + "/meta.tsv";
                OutputStream fosTsv = null;
                if (new File(tsvFile).exists()) {
                    fosTsv  = new FileOutputStream(tsvFile, true);
                }
                else {
                    fosTsv  = new FileOutputStream(tsvFile);
                }
                String str = "filename\tdate\theadline\tmedium\n";
                fosTsv.write(str.getBytes());
                ArrayList<File> xmlFiles = makeRecursiveFileList(xmlFolder, ext);
                System.out.println("xmlFiles.size() = " + xmlFiles.size());
                for (int i = 0; i < xmlFiles.size(); i++) {
                    File xmlFile = xmlFiles.get(i);
                    trials = new HashMap<String, String>();
                    prepareAmCatImport.parseFile(xmlFile);

                    if (trials.size()==0) {
                       // File textFile = new File(textFolder + "/" + textName+".txt");
                        File textFile = new File(textFolder + "/" +  adaptDateToIso(date)+"_"+headline+".txt");
                        OutputStream fosText = new FileOutputStream(textFile);
                        str = textFile.getName() + "\t";
                        plainText = cleanText(plainText);
                        fosText.write(plainText.getBytes());
                        fosText.close();
                        str += adaptDateToIso(date) + "\t" + headline + "\t" + medium + "\n";
                        fosTsv.write(str.getBytes());
                    }
                    else {
                        System.out.println("trials = " + trials.size());
                        for (Map.Entry<String,String> entry : trials.entrySet()) {
                            String key = entry.getKey();
                            String text = entry.getValue();
                            File textFile = new File(textFolder + "/" +  adaptDateToIso(date)+"_"+key+".txt");
                            OutputStream fosText = new FileOutputStream(textFile);
                            fosText.write(text.getBytes());
                            fosText.close();
                            str = textFile.getName() + "\t";
                            str += adaptDateTimeToIso(date) + "\t" + key + "\t" + medium + "\n";
                            fosTsv.write(str.getBytes());
                        }
                    }
                }
                fosTsv.close();
            }
            else {
                System.out.println("textFolder = " + textFolder.exists());
                System.out.println("textFolder.getAbsolutePath() = " + textFolder.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static String cleanText (String text) {
        String cleanText = "";
        char pchar = 0;
        char c = 0;
        for (int i = 0; i < text.length(); i++) {
            c =text.charAt(i);
            if ((c==' ') && (pchar==' ')) {
            }
            else if ((c=='\n') && (pchar=='\n')) {
            }
            else if ((c==' ') && (pchar=='\n')) {
            }
            else {
                cleanText += c;
            }

            pchar = c;
        }
        return cleanText;
    }
    static String adaptDateToIso(String dateString) {
        String dateISO = dateString;
        if (dateString.length()==8) {
            dateISO = dateString.substring(0,4)+"-"+dateString.substring(4,6)+"-"+dateString.substring(6); //+"T00:00:00";
        }
        return dateISO;
    }
    static String adaptDateTimeToIso(String dateString) {
        String dateISO = dateString;
        if (dateString.length()==8) {
            dateISO = dateString.substring(0,4)+"-"+dateString.substring(4,6)+"-"+dateString.substring(6)+"T00:00:00";
        }
        return dateISO;
    }
    public void startElement(String uri, String localName,
                             String qName, Attributes attributes)
            throws SAXException {
        previousvalue = value;
        if (qName.equalsIgnoreCase("p")) {
            value = "";
        }
        else if (qName.equalsIgnoreCase("interp")) {
            headline = attributes.getValue("inst");
            String type = attributes.getValue("type");
            if (type!=null & type.equals("date")) {
                date = attributes.getValue("value");
            }
        }
        else if (qName.equalsIgnoreCase("div1")) {
            String type = attributes.getValue("type");
            if (type!=null & type.equals("trialAccount")) {
                headline = attributes.getValue("id");
            }
        }
        else {
           ////
        }
    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        /*
         </div1>
            <div1 type="trialAccount" id="t19130107-3">
         */
        if (qName.equalsIgnoreCase("p")) {
            plainText += value.trim()+"\n";
        }
        else if (qName.equalsIgnoreCase("div1")) {
            if (!headline.isEmpty()) {
                trials.put(headline, cleanText(plainText));
            }
            headline = "";
            plainText = "";
        }
    }

    public void characters(char ch[], int start, int length)
            throws SAXException {
        value += new String(ch, start, length);
    }


    static public ArrayList<File> makeRecursiveFileList(File inputFile, String theFilter) {
            ArrayList<File> acceptedFileList = new ArrayList<File>();
            File[] theFileList = null;
            if ((inputFile.canRead())) {
                theFileList = inputFile.listFiles();
                for (int i = 0; i < theFileList.length; i++) {
                    File newFile = theFileList[i];
                    if (newFile.isDirectory()) {
                        ArrayList<File> nextFileList = makeRecursiveFileList(newFile, theFilter);
                        acceptedFileList.addAll(nextFileList);
                    } else {
                        if (newFile.getName().endsWith(theFilter)) {
                            acceptedFileList.add(newFile);
                        }
                    }
                   // break;
                }
            } else {
                System.out.println("Cannot access file:" + inputFile + "#");
                if (!inputFile.exists()) {
                    System.out.println("File/folder does not exist!");
                }
            }
            return acceptedFileList;
        }



    public void init() {
          value = "";
          date = "";
          headline = "";
          plainText = "";
    }

    public void parseFile(File file) {
        try {
            FileReader reader = new FileReader(file);
            InputSource inp = new InputSource(reader);
            parseFile(inp);
            reader.close();
/*
            File doneFile = new File(file.getAbsolutePath()+".done");
            file.renameTo(doneFile);
*/
        } catch (Exception e) {
            //e.printStackTrace();
            //
            //File errFile = new File (xmlFolder+".err/"+file.getName());
            //file.renameTo(errFile);
            //System.out.println("file.getName() = " + file.getName());
            //System.out.println("plainText = " + plainText);
            //System.out.println("last value = " + previousvalue);
        }
    }

    public void parseFile(InputSource source) throws ParserConfigurationException, SAXException, IOException {
           init();
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);
            SAXParser parser = factory.newSAXParser();
            parser.parse(source, this);
    }

    /*public boolean parseFile(InputSource source)
    {
        try
        {   init();
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
            System.out.println("last value = " + previousvalue);
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // e.printStackTrace();
        }
        return false;
    }*/
}
