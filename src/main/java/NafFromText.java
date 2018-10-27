import eu.kyotoproject.kaf.KafSaxParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class NafFromText {


    static public void main (String[] args) {
        KafSaxParser kafSaxParser = new KafSaxParser();
        String textFolder = "/Users/piek/Desktop/DigHum-2018/4775434/OBO_XML_7-2/ordinarysAccounts/text/OrdinaryAccounts";
        String nafFolderpath = "/Users/piek/Desktop/DigHum-2018/4775434/OBO_XML_7-2/ordinarysAccounts/naf/";
/*
        String textFolder = "/Users/piek/Desktop/DigHum-2018/4775434/OBO_XML_7-2/sessionsPapers/text/OBP";
        String nafFolderpath = "/Users/piek/Desktop/DigHum-2018/4775434/OBO_XML_7-2/sessionsPapers/naf/";
*/
        File nafFolder = new File(nafFolderpath);
        if (!nafFolder.exists()) {
            nafFolder.mkdir();
        }
        if (nafFolder.exists()) {
            ArrayList<File> txtFiles = OBHelper.makeFlatFileList(new File(textFolder), ".txt");
            for (int i = 0; i < txtFiles.size(); i++) {
                File txtFile = txtFiles.get(i);
                String contents = null;
                try {
                    contents = new String(Files.readAllBytes(Paths.get(txtFile.getAbsolutePath())));
                    if (contents != null) {
                        String date = txtFile.getName().substring(0, txtFile.getName().indexOf("_"));
                        kafSaxParser.init();
                        kafSaxParser.getKafMetaData().setCreationtime(date);
                        kafSaxParser.getKafMetaData().setUrl(txtFile.getName());
                        kafSaxParser.rawText = contents;
                        String nafFile = nafFolderpath+txtFile.getName()+".naf";
                        OutputStream fos = new FileOutputStream(nafFile);
                        kafSaxParser.writeNafToStream(fos);
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
