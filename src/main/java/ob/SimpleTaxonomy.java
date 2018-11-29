package ob;

import org.apache.tools.bzip2.CBZip2InputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.GZIPInputStream;

/**
 * Created by piek on 29/05/16.
 */
public class SimpleTaxonomy {
    static final int colmax = 650;
    static final int colmaxevents = 150;
    public HashMap<String, ArrayList<String>> conceptToLabels = new HashMap<String, ArrayList<String>>();
    public HashMap<String, String> labelToConcept = new HashMap<String, String>();
    public HashMap<String, String> conceptToPrefLabel = new HashMap<String, String>();
    public HashMap<String, String> subToSuper = new HashMap<String, String>();
    public HashMap<String, ArrayList<String>> superToSub = new HashMap<String, ArrayList<String>>();
    static public final String accordion = "<div class=\"accordionItem\">";


    static public void main (String[] args) {
        String hierarchyPath = "/Users/piek/Desktop/NWR-INC/dasym/stats-4-normalised/DBpediaHierarchy_parent_child.tsv";
        SimpleTaxonomy simpleTaxonomy = new SimpleTaxonomy();
        simpleTaxonomy.readSimpleTaxonomyFromDbpFile(hierarchyPath);
    }

    public SimpleTaxonomy() {
        subToSuper = new HashMap<String, String>();
        superToSub = new HashMap<String, ArrayList<String>>();
    }

    public void readSimpleTaxonomyFromSkosFile (String filePath) {
        //<rdf:Description rdf:about="http://eurovoc.europa.eu/8404">
        // <skos:broader rdf:resource="http://eurovoc.europa.eu/2467"/>
        try {
            InputStreamReader isr = null;
            if (filePath.toLowerCase().endsWith(".gz")) {
                try {
                    InputStream fileStream = new FileInputStream(filePath);
                    InputStream gzipStream = new GZIPInputStream(fileStream);
                    isr = new InputStreamReader(gzipStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (filePath.toLowerCase().endsWith(".bz2")) {
                try {
                    InputStream fileStream = new FileInputStream(filePath);
                    InputStream gzipStream = new CBZip2InputStream(fileStream);
                    isr = new InputStreamReader(gzipStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                FileInputStream fis = new FileInputStream(filePath);
                isr = new InputStreamReader(fis);
            }
            if (isr!=null) {
                BufferedReader in = new BufferedReader(isr);
                String inputLine;
                String subClass = "";
                String superClass= "";
                while (in.ready() && (inputLine = in.readLine()) != null) {
                    // System.out.println(inputLine);
                    inputLine = inputLine.trim();
                    if (inputLine.trim().length() > 0) {

                        //<rdf:Description rdf:about="http://eurovoc.europa.eu/8404">
                        // <skos:broader rdf:resource="http://eurovoc.europa.eu/2467"/>
                        int idx_s = inputLine.indexOf("<rdf:Description rdf:about=");
                        int idx_e = -1;
                        if (idx_s>-1) {
                            idx_s = inputLine.indexOf("\"");
                            idx_e = inputLine.lastIndexOf("\"");
                            subClass = inputLine.substring(idx_s+1, idx_e);
                           // System.out.println("subClass = " + subClass);
                        }
                        else {
                            idx_s = inputLine.indexOf("<skos:broader rdf:resource=");
                            idx_e = -1;
                            if (idx_s>-1) {
                                idx_s = inputLine.indexOf("\"");
                                idx_e = inputLine.lastIndexOf("\"");
                                superClass = inputLine.substring(idx_s+1, idx_e);
                                //System.out.println("parent = " + superClass);
                                if (!subClass.equals(superClass)) {
                                    subToSuper.put(subClass, superClass);
                                    if (superToSub.containsKey(superClass)) {
                                        ArrayList<String> subs = superToSub.get(superClass);
                                        if (!subs.contains(subClass)) {
                                            subs.add(subClass);
                                            superToSub.put(superClass, subs);
                                        }
                                    }
                                    else {
                                        ArrayList<String> subs = new ArrayList<String>();
                                        subs.add(subClass);
                                        superToSub.put(superClass, subs);
                                    }
                                }
                            }
                            else {
                                //<skos:prefLabel xml:lang="en">
                                //<skos:altLabel xml:lang="en">resolution of the European Parliament</skos:altLabel>
                                idx_s = inputLine.indexOf("skos:prefLabel xml:lang=\"en\">");
                                idx_e = -1;
                                if (idx_s>-1) {
                                    idx_s = inputLine.indexOf(">");
                                    idx_e = inputLine.lastIndexOf("</");
                                    String label = inputLine.substring(idx_s+1, idx_e);
                                    labelToConcept.put(label, subClass);
                                    conceptToPrefLabel.put(subClass, label);
                                    if (conceptToLabels.containsKey(subClass)) {
                                        ArrayList<String> labels = conceptToLabels.get(subClass);
                                        labels.add(label);
                                        conceptToLabels.put(subClass, labels);
                                    }
                                    else {
                                        ArrayList<String> labels = new ArrayList<String>();
                                        labels.add(label);
                                        conceptToLabels.put(subClass, labels);
                                    }
                                }
                                else {
                                    idx_s = inputLine.indexOf("skos:altLabel xml:lang=\"en\">");
                                    idx_e = -1;
                                    if (idx_s > -1) {
                                        idx_s = inputLine.indexOf(">");
                                        idx_e = inputLine.lastIndexOf("</");
                                        String label = inputLine.substring(idx_s + 1, idx_e);
                                        labelToConcept.put(label, subClass);
                                        if (conceptToLabels.containsKey(subClass)) {
                                            ArrayList<String> labels = conceptToLabels.get(subClass);
                                            labels.add(label);
                                            conceptToLabels.put(subClass, labels);
                                        }
                                        else {
                                            ArrayList<String> labels = new ArrayList<String>();
                                            labels.add(label);
                                            conceptToLabels.put(subClass, labels);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
        //printTree();
    }

    /**
      * Assume the following structure TAB separated structure with Parent TAB Child per line
      * Parent   Child
      * Parent   Child
      * etc...
      * @param filePath
      */
     public void readSimpleTaxonomyFromFile (String filePath) {
         try {
             System.out.println("filePath = " + filePath);
             InputStreamReader isr = null;
             if (filePath.toLowerCase().endsWith(".gz")) {
                 try {
                     InputStream fileStream = new FileInputStream(filePath);
                     InputStream gzipStream = new GZIPInputStream(fileStream);
                     isr = new InputStreamReader(gzipStream);
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
             else if (filePath.toLowerCase().endsWith(".bz2")) {
                 try {
                     InputStream fileStream = new FileInputStream(filePath);
                     InputStream gzipStream = new CBZip2InputStream(fileStream);
                     isr = new InputStreamReader(gzipStream);
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
             else {
                 FileInputStream fis = new FileInputStream(filePath);
                 isr = new InputStreamReader(fis);
             }
             if (isr!=null) {
                 BufferedReader in = new BufferedReader(isr);
                 String inputLine;
                 while (in.ready() && (inputLine = in.readLine()) != null) {
                     inputLine = inputLine.trim();
                     System.out.println("inputLine = " + inputLine);
                     if (inputLine.trim().length() > 0) {
                         String[] fields = inputLine.split("\t");
                         if (fields.length == 2) {
                             String superClass = fields[1];
                             String subClass = fields[0];
                                // System.out.println("subClass = " + subClass);
                                // System.out.println("superClass = " + superClass);
                                 if (!subClass.equals(superClass)) {
                                     subToSuper.put(subClass, superClass);
                                     if (superToSub.containsKey(superClass)) {
                                         ArrayList<String> subs = superToSub.get(superClass);
                                         if (!subs.contains(subClass)) {
                                             subs.add(subClass);
                                             superToSub.put(superClass, subs);
                                         }
                                     }
                                     else {
                                         ArrayList<String> subs = new ArrayList<String>();
                                         subs.add(subClass);
                                         superToSub.put(superClass, subs);
                                     }
                                 }
                         }
                         else {
                             System.out.println("Skipping line:"+inputLine);
                         }
                     }
                 }
             }
         } catch (IOException e) {
             e.printStackTrace();
         }
         //printTree();
     }

     public void readSimpleTaxonomyFromTtlFile (String filePath) {
         try {
             System.out.println("filePath = " + filePath);
             InputStreamReader isr = null;
             if (filePath.toLowerCase().endsWith(".gz")) {
                 try {
                     InputStream fileStream = new FileInputStream(filePath);
                     InputStream gzipStream = new GZIPInputStream(fileStream);
                     isr = new InputStreamReader(gzipStream);
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
             else if (filePath.toLowerCase().endsWith(".bz2")) {
                 try {
                     InputStream fileStream = new FileInputStream(filePath);
                     InputStream gzipStream = new CBZip2InputStream(fileStream);
                     isr = new InputStreamReader(gzipStream);
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
             else {
                 FileInputStream fis = new FileInputStream(filePath);
                 isr = new InputStreamReader(fis);
             }
             if (isr!=null) {
                 BufferedReader in = new BufferedReader(isr);
                 String inputLine;
                 while (in.ready() && (inputLine = in.readLine()) != null) {
                     inputLine = inputLine.trim();
                    // System.out.println("inputLine = " + inputLine);
                     //<http://dbpedia.org/resource/Aristotle> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Philosopher> .
                     if (inputLine.trim().length() > 0) {
                         String[] fields = inputLine.split(" ");
                         if (fields.length == 4) {
                             String superClass = fields[2].substring(1, fields[2].length()-1);
                             String subClass = fields[0].substring(1, fields[0].length()-1);
                            // System.out.println("subClass = " + subClass);
                            // System.out.println("superClass = " + superClass);
                             //http://dbpedia.org/resource/China
                             if (!subClass.equals(superClass)) {
                                 subToSuper.put(subClass, superClass);
                                 // do not store reverse because
                                 /*if (superToSub.containsKey(superClass)) {
                                     ArrayList<String> subs = superToSub.get(superClass);
                                     if (!subs.contains(subClass)) {
                                         subs.add(subClass);
                                         superToSub.put(superClass, subs);
                                     }
                                 }
                                 else {
                                     ArrayList<String> subs = new ArrayList<String>();
                                     subs.add(subClass);
                                     superToSub.put(superClass, subs);
                                 }*/
                             }
                         }
                         else {
                             System.out.println("Skipping line:"+inputLine);
                         }
                     }
                 }
             }
         } catch (IOException e) {
             e.printStackTrace();
         }
         System.out.println("subToSuper = " + subToSuper.size());
         //printTree();
     }

    public void readSimpleTaxonomyFromDbpFile (String filePath) {
        try {
            InputStreamReader isr = null;
            if (filePath.toLowerCase().endsWith(".gz")) {
                try {
                    InputStream fileStream = new FileInputStream(filePath);
                    InputStream gzipStream = new GZIPInputStream(fileStream);
                    isr = new InputStreamReader(gzipStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (filePath.toLowerCase().endsWith(".bz2")) {
                try {
                    InputStream fileStream = new FileInputStream(filePath);
                    InputStream gzipStream = new CBZip2InputStream(fileStream);
                    isr = new InputStreamReader(gzipStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                FileInputStream fis = new FileInputStream(filePath);
                isr = new InputStreamReader(fis);
            }
            if (isr!=null) {
                BufferedReader in = new BufferedReader(isr);
                String inputLine;
                while (in.ready() && (inputLine = in.readLine()) != null) {
                    // System.out.println(inputLine);
                    inputLine = inputLine.trim();
                    if (inputLine.trim().length() > 0) {
                             /*
                             	Colour	9
	Currency	189
	Disease	8
	EthnicGroup	2
	Holiday	15
	Language	106
    Agent	Agent	9722
Agent	Family	Family	99
Agent	Organisation	Broadcaster	BroadcastNetwork	1
Agent	Organisation	Broadcaster	Broadcaster	15

www.w3.org/2002/07/owl#Thing	Agent	Person	Monarch
www.w3.org/2002/07/owl#Thing	Agent	Person	MovieDirector
www.w3.org/2002/07/owl#Thing	Agent	Person	Noble
www.w3.org/2002/07/owl#Thing	Agent	Person	OfficeHolder
www.w3.org/2002/07/owl#Thing	Agent	Person	OrganisationMember	SportsTeamMember
www.w3.org/2002/07/owl#Thing	Agent	Person	Orphan
www.w3.org/2002/07/owl#Thing	Agent	Person	Philosopher
     */
                       // System.out.println("inputLine = " + inputLine);
                        String[] fields = inputLine.split("\t");
                        if (fields.length > 1) {
                            for (int i = 0; i < fields.length-1; i++) {
                                String subClass = "dbp:"+fields[i+1];
                                Integer cnt = -1;
                                try {
                                    cnt = Integer.parseInt(subClass);
                                } catch (NumberFormatException e) {
                                   // e.printStackTrace();
                                    //So only if fields[i+1] is not a count!
                                    //System.out.println("subClass = " + subClass);
                                    String superClass = "dbp:"+fields[i];
                                    //System.out.println("subClass = " + subClass);
                                    //System.out.println("superClass = " + superClass);
                                    if (!subClass.equals(superClass)) {
                                        subToSuper.put(subClass, superClass);
                                        if (superToSub.containsKey(superClass)) {
                                            ArrayList<String> subs = superToSub.get(superClass);
                                            if (!subs.contains(subClass)) {
                                                subs.add(subClass);
                                                superToSub.put(superClass, subs);
                                            }
                                        }
                                        else {
                                            ArrayList<String> subs = new ArrayList<String>();
                                            subs.add(subClass);
                                            superToSub.put(superClass, subs);
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            System.out.println("Skipping line:"+inputLine);
                        }
                    }
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
        //printTree();
    }

    public void readSimpleTaxonomyFromDbpFile (String filePath, Set<String> keySet) {
        try {
            InputStreamReader isr = null;
            System.out.println("filePath = " + filePath);
            if (filePath.toLowerCase().endsWith(".gz")) {
                try {
                    InputStream fileStream = new FileInputStream(filePath);
                    InputStream gzipStream = new GZIPInputStream(fileStream);
                    isr = new InputStreamReader(gzipStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (filePath.toLowerCase().endsWith(".bz2")) {
                try {
                    InputStream fileStream = new FileInputStream(filePath);
                    InputStream gzipStream = new CBZip2InputStream(fileStream);
                    isr = new InputStreamReader(gzipStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                FileInputStream fis = new FileInputStream(filePath);
                isr = new InputStreamReader(fis);
            }
            if (isr!=null) {
                BufferedReader in = new BufferedReader(isr);
                String inputLine;
                while (in.ready() && (inputLine = in.readLine()) != null) {
                    // System.out.println(inputLine);
                    inputLine = inputLine.trim();
                    if (inputLine.trim().length() > 0) {
                             /*
<http://dbpedia.org/resource/Abraham_Lincoln__1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/TimePeriod> .
<http://dbpedia.org/resource/Abraham_Lincoln__2> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/TimePeriod> .
<http://dbpedia.org/resource/Abraham_Lincoln__3> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/TimePeriod> .
<http://dbpedia.org/resource/Austroasiatic_languages> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> .
<http://dbpedia.org/resource/Afroasiatic_languages> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Thing> .
     */
                        // System.out.println("inputLine = " + inputLine);
                        String[] fields = inputLine.split("\t");
                        if (fields.length == 3) {
                            String className = fields[0];
                            className = className.substring(className.lastIndexOf("/"));
                            System.out.println("className = " + className);
                            if (keySet.contains(className)) {
                                String subClass = "dbp:" + className;
                                className = fields[2];
                                className = className.substring(className.lastIndexOf("/"));
                                String superClass = "dbp:" + className;
                                if (!subClass.equals(superClass)) {
                                    subToSuper.put(subClass, superClass);
                                    if (superToSub.containsKey(superClass)) {
                                        ArrayList<String> subs = superToSub.get(superClass);
                                        if (!subs.contains(subClass)) {
                                            subs.add(subClass);
                                            superToSub.put(superClass, subs);
                                        }
                                    } else {
                                        ArrayList<String> subs = new ArrayList<String>();
                                        subs.add(subClass);
                                        superToSub.put(superClass, subs);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
        //printTree();
    }

    public ArrayList<String> getTops () {
        ArrayList<String> tops = new ArrayList<String>();
        Set keySet = superToSub.keySet();
        Iterator<String> keys = keySet.iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            if (!key.equals("eso:SituationRuleAssertion")) {
                if (!subToSuper.containsKey(key)) {
                    if (!tops.contains(key)) tops.add(key);
                }
            }
        }
        return tops;
    }

    public void getParentChain (String c, ArrayList<String> parents) {
        if (subToSuper.containsKey(c)) {
            String p = subToSuper.get(c);
            if (!parents.contains(p)) {
                parents.add(p);
                getParentChain(p, parents);
            }
        }
    }


    public void getDescendants (String c, ArrayList<String> decendants) {
        if (superToSub.containsKey(c)) {
            ArrayList<String> subs = superToSub.get(c);
            for (int i = 0; i < subs.size(); i++) {
                String sub = subs.get(i);
                if (!decendants.contains(sub)) {
                    decendants.add(sub);
                    getDescendants(sub, decendants);
                }
            }
        }
    }

    public String getMostSpecificChild (ArrayList<String> types) {
        String child = "";
        if (types.size()==1) {
            child = types.get(0);
        }
        else {
            ArrayList<String> parents = new ArrayList<String>();
            for (int i = 0; i < types.size(); i++) {
                String t = types.get(i);
                if (subToSuper.containsKey(t)) {
                    for (int j = 0; j < types.size(); j++) {
                        if (j!=i) {
                            String t2 = types.get(j);
                            if (subToSuper.get(t).equals(t2)) {
                                parents.add(t2);
                                if (!parents.contains(t)) {
                                    child = t;
                                }
                            }
                        }
                    }
                }
            }
        }
        return child;
    }

    public void printTree () {
        ArrayList<String> tops = this.getTops();
        printTree(tops, 0);
    }

    public void printTree (ArrayList<String> tops, int level) {
        level++;
        for (int i = 0; i < tops.size(); i++) {
            String top = tops.get(i);
            String str = "";
            for (int j = 0; j < level; j++) {
                str += "  ";

            }
            if (superToSub.containsKey(top)) {
                ArrayList<String> children = superToSub.get(top);
                str += top + ":" + children.size();
                System.out.println(str);
                printTree(children, level);
            }
            else {
                str += top;
                System.out.println(str);
            }
        }
    }

    public void printTree (ArrayList<String> tops, int level, HashMap<String, Integer> eventCounts) {
        level++;
        for (int i = 0; i < tops.size(); i++) {
            String top = tops.get(i);
            Integer cnt = 0;
            if (eventCounts.containsKey(top)) {
                cnt = eventCounts.get(top);
            }
            String str = "";
            for (int j = 0; j < level; j++) {
                str += "  ";

            }
            if (superToSub.containsKey(top)) {
                ArrayList<String> children = superToSub.get(top);
                str += top + ":" + cnt;
                System.out.println(str);
                printTree(children, level, eventCounts);
            }
            else {
                str += top;
                System.out.println(str);
            }
        }
    }


    

}
