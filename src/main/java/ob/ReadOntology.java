package ob;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ReadOntology extends org.xml.sax.helpers.DefaultHandler {
    static String value = "";
    HashMap<String, ArrayList<String>> map = new HashMap<>();
    static String owlClass = "";
    static String fnClass = "";
    static String prefix = "@prefix eso:   <http://cltl.nl/ontology#> .\n" +
            "@prefix owl:   <http://www.w3.org/2002/07/owl#> .\n" +
            "@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
            "@prefix xml: <http://www.w3.org/XML/1998/namespace> .\n" +
            "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n" +
            "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\n" +
            "@prefix owl2xml: <http://www.w3.org/2006/12/owl2-xml#> .\n" +
            "@prefix fn:    <http://www.newsreader-project.eu/ontologies/framenet/> .\n\n";
    static public void main (String [] args) {
        try {
            String pathToOntology = "/Users/piek/Desktop/DigHum-2018/ontologies/CLTL_CEO_version_1_sameas.owl";
            OutputStream fos = new FileOutputStream(pathToOntology+"mappings.ttl");
            fos.write(prefix.getBytes());

            ReadOntology readOntology = new ReadOntology();
            readOntology.parseFile(new File (pathToOntology));
            for (Map.Entry<String,ArrayList<String>> entry : readOntology.map.entrySet()) {
                ArrayList<String> mappings = entry.getValue();
                for (int i = 0; i < mappings.size(); i++) {
                    String m =  mappings.get(i);
                    String str = "<"+entry.getKey()+">"+" rdfs:subClassOf "+"<"+ m +">"+" .\n";
                    fos.write(str.getBytes());
                }
            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ReadOntology() {
        this.map = new HashMap<>();
    }

    static void readOWL (String file) {
        /*Dataset newDataset = TDBFactory.createDataset();
                try {
                    Dataset dataset = RDFDataMgr.loadDataset(new File(pathToOntology).getAbsolutePath(), Lang.RDFXML);
                    System.out.println("dataset.getDefaultModel() = " + dataset.getDefaultModel());
                    Iterator<String> models = dataset.listNames();
                    while (models.hasNext()) {
                        String model = models.next();
                        System.out.println("model = " + model);
                        StmtIterator siter = dataset.getNamedModel(model).listStatements();
                        while (siter.hasNext()) {
                            Statement s = siter.nextStatement();
                            System.out.println("s = " + s.toString());
                        }
                    }

                }
                catch (Exception e){
                    e.printStackTrace();
                }*/
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
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);
            SAXParser parser = factory.newSAXParser();
            parser.parse(source, this);
    }
    public void startElement(String uri, String localName,
                             String qName, Attributes attributes)
            throws SAXException {
        value = "";
        if (qName.equalsIgnoreCase("owl:Class")) {
            //   <owl:Class rdf:about="http://cltl.nl/ontology#Exporting">
            owlClass = attributes.getValue("rdf:about");
        }
        else if (qName.equalsIgnoreCase("owl:sameAs")) {
            //        <owl:sameAs rdf:resource="http://www.newsreader-project.eu/framenet#Explosion"/>
            fnClass = attributes.getValue("rdf:resource");
            if (!owlClass.isEmpty()) {
                if (map.containsKey(fnClass)) {
                    ArrayList<String> mappings = map.get(fnClass);
                    if (!mappings.contains(owlClass)) {
                        mappings.add(owlClass);
                        map.put(fnClass, mappings);
                    }
                } else {
                    ArrayList<String> mappings = new ArrayList<>();
                    mappings.add(owlClass);
                    map.put(fnClass, mappings);
                }
            }

        }
        else {
           ////
        }
    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (qName.equalsIgnoreCase("owl:Class")) {
            owlClass = "";
            fnClass = "";
        }


    }

    public void characters(char ch[], int start, int length)
            throws SAXException {
        value += new String(ch, start, length);
    }

    static public Vector<String> ReadFileToStringVector(String fileName) {
        Vector<String> vector = new Vector<String>();
        if (new File(fileName).exists() ) {
            try {
                FileInputStream fis = new FileInputStream(fileName);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader in = new BufferedReader(isr);
                String inputLine;
                while (in.ready()&&(inputLine = in.readLine()) != null) {
                    //System.out.println(inputLine);

                    //    <owl:Class rdf:about="http://cltl.nl/ontology#Explosion">
                    //        <owl:sameAs rdf:resource="http://www.newsreader-project.eu/framenet#Explosion"/>

                    if (inputLine.trim().length()>0) {
                        vector.add(inputLine.trim().toLowerCase());
                    }
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return vector;
    }
}
