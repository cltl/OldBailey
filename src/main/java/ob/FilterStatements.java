package ob;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.tdb.TDBFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class FilterStatements {
    static Dataset filteredDataset = TDBFactory.createDataset();
    static boolean DEBUG = false;
    static String statementPrefixFilter = "";

    static public void main(String[] args) {
        String pathToTripleFiles = "/Users/piek/Desktop/DigHum-2018/4775434/OBO_XML_7-2/sessions/rdf";
        statementPrefixFilter = "19";
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equalsIgnoreCase("--rdf-folder") && args.length > (i + 1)) {
                pathToTripleFiles = args[i + 1];
            }
            else if (arg.equalsIgnoreCase("--prefix") && args.length > (i + 1)) {
                statementPrefixFilter = args[i + 1];
            }
        }
        Dataset dataset = TDBFactory.createDataset();
        ArrayList<File> tripleFiles = OBHelper.makeRecursiveFileList(new File(pathToTripleFiles), ".trig");
        for (int i = 0; i < tripleFiles.size(); i++) {
            File trigFile = tripleFiles.get(i);
            System.out.println("trigFile.getName() = " + trigFile.getName());
            dataset = RDFDataMgr.loadDataset(trigFile.getAbsolutePath());
            filterStatements(dataset);
            dataset = null;
        }
        File filteredFile = new File(pathToTripleFiles+"/"+statementPrefixFilter+".trig");
        outputRdf(filteredFile);
    }

    public static void filterStatements (Dataset dataset) {
        Model namedModel = dataset.getDefaultModel();
        StmtIterator siter = namedModel.listStatements();
        String [] fields = null;
        ArrayList<Statement> triples = null;
        int count = 0;
        while (siter.hasNext()) {
            Statement s = siter.nextStatement();
            String sessionKey = s.getSubject().getLocalName();
            fields = sessionKey.split("-");
            sessionKey = fields[0];
            if (fields.length>1) {
                sessionKey+="-"+fields[1];
            }
            if (!sessionKey.substring(1).startsWith(statementPrefixFilter))  {
                continue;
            }
            count++;
            if (count%10000==0) {
                System.out.println("Nr. statements processed = " + count+ " out of:"+namedModel.size());
            }
            filteredDataset.getDefaultModel().add(s);
        }
    }

    static public void outputRdf (File outputRdf) {
        filteredDataset.getDefaultModel().setNsPrefix("oldbailey", ResourcesUri.oldbaily);
        try {
            OutputStream fos = new FileOutputStream(outputRdf);
            RDFDataMgr.write(fos, filteredDataset, RDFFormat.TRIG_PRETTY);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
