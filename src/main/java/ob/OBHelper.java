package ob;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.tdb.TDBFactory;
import eu.kyotoproject.kaf.KafSense;
import org.apache.jena.riot.RDFDataMgr;

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 11/14/13
 * Time: 7:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class OBHelper {


    static public void main (String[] args) {
         String folder = "/Code/vu/OldBailey/example/huygen/projecten/oldbailey/temp/wrong_trigs";
         String filePath = "/Code/vu/OldBailey/example/huygen/projecten/oldbailey/temp/wrong_trigs/t17250224-51.trig";
         String extension = ".meta";
         //RDFCheck(folder, extension);
         RDFCheck(new File (filePath));
    }

    static public void RDFCheck(String trigFolder, String ext) {
        ArrayList<File> trigFiles = OBHelper.makeFlatFileList(new File(trigFolder), ext);
        for (int i = 0; i < trigFiles.size(); i++) {
            File metaFile = trigFiles.get(i);
            String rename = metaFile.getAbsoluteFile().getParentFile()+"/"+metaFile.getName().substring(0, metaFile.getName().lastIndexOf("."));

            File trigFile = new File (rename);
            metaFile.renameTo(trigFile);
            RDFCheck(trigFile);
        }

    }

    static public void RDFCheck(File trigFile) {
            System.out.println("trigFile.getName() = " + trigFile.getName());
            Dataset dataset = TDBFactory.createDataset();
            try {
              dataset = RDFDataMgr.loadDataset(trigFile.getAbsolutePath());
              Iterator<String> names = dataset.listNames();
              while (names.hasNext()) {
                  String name = names.next();
                  System.out.println("name = " + name);
              }
            } catch (Exception e) {
              e.printStackTrace();
            }
    }

    static public String alphaNumericUri(String uri) {
        final String alfanum="1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String cleanUri = "";
        for (int i = 0; i < uri.toCharArray().length; i++) {
            char c = uri.toCharArray()[i];
            if (alfanum.indexOf(c)>-1) {
               cleanUri +=c;
            }
        }
        return cleanUri;
    }

    static public boolean hasAlphaNumeric(String uri) {
        final String alfanum="1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < uri.toCharArray().length; i++) {
            char c = uri.toCharArray()[i];
            if (alfanum.indexOf(c)>-1) {
               return true;
            }
        }
        return false;
    }

    static public String keepAlphaNumeric(String uri) {
        String newString = "";
        final String alfanum="1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < uri.toCharArray().length; i++) {
            char c = uri.toCharArray()[i];
            if (alfanum.indexOf(c)>-1) {
                newString+=c;
            }
        }
        return newString;
    }


    static public ArrayList<File> makeRecursiveFileList(File inputFile) {
        ArrayList<File> acceptedFileList = new ArrayList<File>();
        File[] theFileList = null;
        if ((inputFile.canRead()) && inputFile.isDirectory()) {
            theFileList = inputFile.listFiles();
            for (int i = 0; i < theFileList.length; i++) {
                File newFile = theFileList[i];
                if (newFile.isDirectory()) {
                    ArrayList<File> nextFileList = makeRecursiveFileList(newFile);
                    acceptedFileList.addAll(nextFileList);
                } else {
                    acceptedFileList.add(newFile);
                }
            }
        } else {
            System.out.println("Cannot access file:" + inputFile + "#");
            if (!inputFile.exists()) {
                System.out.println("File does not exist!");
            }
        }
        return acceptedFileList;
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
    static public ArrayList<File> makeRecursiveFileList(File inputFile, String prefix, String theFilter) {
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
                    if (newFile.getName().endsWith(theFilter) && (prefix.isEmpty() || newFile.getName().startsWith(prefix))) {
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

    static public ArrayList<File> makeRecursiveFileListFromFilteredFolders(File inputFile, String theFilter, String folderFilter) {
        ArrayList<File> acceptedFileList = new ArrayList<File>();
        File[] theFileList = null;
        if ((inputFile.canRead())) {
            theFileList = inputFile.listFiles();
            for (int i = 0; i < theFileList.length; i++) {
                File newFile = theFileList[i];
                if (newFile.isDirectory()) {
                    ArrayList<File> nextFileList = makeRecursiveFileListFromFilteredFolders(newFile, theFilter, folderFilter);
                    acceptedFileList.addAll(nextFileList);
                } else {
                    String parentFolderName = newFile.getParentFile().getName();
                    if (parentFolderName.startsWith(folderFilter)) {
                        if (newFile.getName().endsWith(theFilter)) {
                            acceptedFileList.add(newFile);
                        }
                    }
                }
            }
        } else {
            System.out.println("Cannot access file:" + inputFile + "#");
            if (!inputFile.exists()) {
                System.out.println("File/folder does not exist!");
            }
        }
        return acceptedFileList;
    }

    static public ArrayList<File> makeFlatFileList(File inputFile, String theFilter) {
        ArrayList<File> acceptedFileList = new ArrayList<File>();
        File[] theFileList = null;
        if ((inputFile.canRead()) && inputFile.isDirectory()) {
            theFileList = inputFile.listFiles();
            for (int i = 0; i < theFileList.length; i++) {
                File newFile = theFileList[i];
                if (!newFile.isDirectory()) {
                    if (newFile.getName().endsWith(theFilter)) {
                        acceptedFileList.add(newFile);
                    }
                }
            }
        } else {
            System.out.println("Cannot access file:" + inputFile + "#");
            if (!inputFile.exists()) {
                System.out.println("File does not exist!");
            }
        }
        return acceptedFileList;
    }

    static public ArrayList<File> makeFolderList(File inputFile) {
        ArrayList<File> folderList = new ArrayList<File>();
        File[] theFileList = null;
        if ((inputFile.canRead()) && inputFile.isDirectory()) {
            theFileList = inputFile.listFiles();
            for (int i = 0; i < theFileList.length; i++) {
                File newFile = theFileList[i];
                if (newFile.isDirectory()) {
                    folderList.add(newFile);
                }
            }
        } else {
            System.out.println("Cannot access file:" + inputFile + "#");
            if (!inputFile.exists()) {
                System.out.println("File does not exist!");
            }
        }
        return folderList;
    }




    static public HashMap<String, ArrayList<String>> ReadFileToStringHashMap(String fileName) {
        HashMap<String, ArrayList<String>> lineHashMap = new HashMap<String, ArrayList<String>>();
        if (new File(fileName).exists() ) {
            try {
                FileInputStream fis = new FileInputStream(fileName);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader in = new BufferedReader(isr);
                String inputLine;
                while (in.ready()&&(inputLine = in.readLine()) != null) {
                    //System.out.println(inputLine);
                    if (inputLine.trim().length()>0) {
                        int idx_s = inputLine.indexOf("\t");
                        if (idx_s>-1) {
                            String key = inputLine.substring(0, idx_s).trim();
                            String value = inputLine.substring(idx_s+1).trim();
                            if (lineHashMap.containsKey(key)) {
                                ArrayList<String> files = lineHashMap.get(key);
                                files.add(value);
                                lineHashMap.put(key, files);
                            }
                            else {
                                ArrayList<String> files = new ArrayList<String>();
                                files.add(value);
                                lineHashMap.put(key, files);
                            }
                        }
                    }
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lineHashMap;
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

    static public ArrayList<String> ReadFileToStringArrayList(String fileName) {
        ArrayList<String> vector = new ArrayList<String>();
        if (new File(fileName).exists() ) {
            try {
                FileInputStream fis = new FileInputStream(fileName);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader in = new BufferedReader(isr);
                String inputLine;
                while (in.ready()&&(inputLine = in.readLine()) != null) {
                    //System.out.println(inputLine);
                    if (inputLine.trim().length()>0) {
                      //  System.out.println("inputLine = " + inputLine);
                        vector.add(inputLine.trim());
                    }
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("Cannot find fileName = " + fileName);
        }
        return vector;
    }

    static public void addNewReferences (ArrayList<KafSense> refsNew, ArrayList<KafSense> refs) {
        for (int i = 0; i < refsNew.size(); i++) {
            KafSense kafSense = refsNew.get(i);
            boolean match = false;
            for (int j = 0; j < refs.size(); j++) {
                KafSense sense = refs.get(j);
                if (kafSense.getSensecode().equals(sense.getSensecode())) {
                    match = true;
                    break;
                }
            }
            if (!match) {
                refs.add(kafSense);
            }
        }
    }

    static public ArrayList<String> getDifference (ArrayList<String> l1, ArrayList<String> l2) {
        ArrayList<String> l3 = new ArrayList<String>();
        for (int i = 0; i < l1.size(); i++) {
            String s = l1.get(i);
            if (!l2.contains(s)) {
                l3.add(s);
            }
        }
        return l3;
    }

/*    static public ArrayList<String> getDifference (String s1, ArrayList<String> l1, ArrayList<String> l2) {
        ArrayList<String> l3 = new ArrayList<String>();
        for (int i = 0; i < l1.size(); i++) {
            String s = l1.get(i);
            if (!l2.contains(s) && !s1.equals(s)) {
                l3.add(s);
            }
        }
        return l3;
    }*/

    static public String fixUri (String inputLine) {
        /// PeterShaffyPeterShaffyPeterShaffy
        String fixed = "";
        String buffer = "";
        for (int i = 0; i < inputLine.length(); i++) {
            char c = inputLine.charAt(i);
            buffer += c;
            if (repeatingString(buffer, inputLine)) {
                return buffer;
            }
        }
        return inputLine;
    }

    static public boolean repeatingString (String s1, String s2) {
        /// PeterShaffyPeterShaffyPeterShaffy
        String s3 = s1;
        if (s2.startsWith(s3) && s2.endsWith(s3)) {
             s3 = s2.substring(s1.length());
             if (s3.equals(s1)) {
                 return true;
             }
            else {
                 return repeatingString(s1, s3);
             }
        }
        return false;
    }


    static public String fixUriId (String id) {
        int idx = id.indexOf("/entities/");
        if (idx>-1) {
           // System.out.println("id = " + id);
            idx = id.lastIndexOf("/");
            String s1 = id.substring(0, idx+1);
            String s2 = id.substring(idx+1);
            String s2new = OBHelper.fixUri(s2);
            if (!s2.equals(s2new)) {
                String newId = s1+s2new;
                /*if (id.indexOf("Frank")>-1) {
                     System.out.println("id = " + id);
                     System.out.println("newId = " + newId);
                }*/
                return newId;
            }
        }
        return id;
    }

    public static Map<String, Integer> sortByComparatorIncreasing(Map<String, Integer> unsortMap) {

        // Convert Map to List
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // Convert sorted map back to a Map
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
            Map.Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public static String getLastTop(Map<String, Integer> unsortMap) {
          String topKey = "";
          Integer topValue = -1;
          List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
            Map.Entry<String, Integer> entry = it.next();
            //System.out.println("entry.getKey() = " + entry.getKey());
            if (entry.getValue()>topValue) {
                topValue = entry.getValue();
                topKey = entry.getKey();
            }
        }
      //  System.out.println("topKey = " + topKey);
        return topKey;
    }

     public static Map<String, Integer> sortByComparatorDecreasing(Map<String, Integer> unsortMap) {

            // Convert Map to List
            List<Map.Entry<String, Integer>> list =
                    new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

            // Sort list with comparator, to compare the Map values
            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                public int compare(Map.Entry<String, Integer> o2,
                                   Map.Entry<String, Integer> o1) {
                    return (o1.getValue()).compareTo(o2.getValue());
                }
            });

            // Convert sorted map back to a Map
            Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
            for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
                Map.Entry<String, Integer> entry = it.next();
                sortedMap.put(entry.getKey(), entry.getValue());
            }
            return sortedMap;
        }

    public static void printMap(Map<String, Integer> map) {
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.println("[Key] : " + entry.getKey()
                    + " [Value] : " + entry.getValue());
        }
    }
    
    public static ArrayList<String> splitSubstring (String str, String substr) {
        ArrayList<String> split = new ArrayList<String>();
        int idx_s = 0;
        int idx_e = str.indexOf(substr);
        while (idx_e>-1) {
            String f = str.substring(idx_s, idx_e);
            split.add(f.trim());
            idx_s = idx_e+substr.length();
            idx_e = str.indexOf(substr, idx_s);
        }
        if (idx_s<str.length()) {
           String f = str.substring(idx_s).trim();
            split.add(f);
        }
        //System.out.println("split.toString() = " + split.toString());
        return split;
    }

    public static String getCamelName (String name) {
        final String capital = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lastName = "";
        boolean CAMEL = false;
        System.out.println("name = " + name);
        for (int i = 1; i < name.length(); i++) {
            char c = name.charAt(i);
            if (capital.indexOf(c)>-1) {
               CAMEL = true;
            }
            if (CAMEL) lastName+=c;
        }
        System.out.println("lastName = " + lastName);
        return lastName;
    }

    public static boolean isSimpleQuery (String[] args) {
        int q = 0;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equalsIgnoreCase("--entity")) {
               q++;
            }
            else if (arg.equalsIgnoreCase("--event")) {
                q++;
            }
            else if (arg.equalsIgnoreCase("--grasp")) {
                q++;
            }
            else if (arg.equalsIgnoreCase("--year")) {
                q++;
            }
            else if (arg.equalsIgnoreCase("--topic")) {
                q++;
            }
            else if (arg.equalsIgnoreCase("--source")) {
                q++;
            }
            else if (arg.equalsIgnoreCase("--word")) {
                q++;
            }
        }
        if (q>1) {
            return false;
        }
        else {
            return true;
        }
    }
}
