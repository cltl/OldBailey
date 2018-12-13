package ob;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Vector;

public class RewriteNwrRdf {


     static public void main (String[] args) {
         String pathToHierarchyFile = "/Code/vu/newsreader/vua-resources/instance_types_en.ttl.gz";
         String pathToTrigFile = "/Users/piek/Desktop/DigHum-2018/4775434/OBO_XML_7-2/sessions/nwr-rdf/naf170rdf.trig";
         for (int i = 0; i < args.length; i++) {
             String arg = args[i];
             if (arg.equals("--trig") && args.length > (i + 1)) {
                 pathToTrigFile = args[i + 1];
             }
             else if (arg.equals("--ontology") && args.length > (i + 1)) {
                 pathToHierarchyFile = args[i + 1];
             }
         }
         // English
         SimpleTaxonomy simpleTaxonomy = new SimpleTaxonomy();
         if (!pathToHierarchyFile.isEmpty()) {
             /// if Dutch
             //simpleTaxonomy.readSimpleTaxonomyFromFile(pathToHierarchyFile);
             /// if English
             simpleTaxonomy.readSimpleTaxonomyFromTtlFile(pathToHierarchyFile);
         }
         readGraspTripleFromTrigFiles(new File(pathToTrigFile), simpleTaxonomy);
     }

     static public void readGraspTripleFromTrigFiles (File trigFile, SimpleTaxonomy simpleTaxonomy) {
         Dataset newDataset = TDBFactory.createDataset();
/*
         Model newNamedModel = newDataset.getNamedModel("http://www.newsreader-project.eu/instances");
         ResourcesUri.prefixSimpleModel(newDataset.getDefaultModel());
*/
         try {
             Dataset dataset = RDFDataMgr.loadDataset(trigFile.getAbsolutePath());
             Model namedModel = dataset.getNamedModel("http://www.newsreader-project.eu/instances");
             System.out.println("namedModel.size() = " + namedModel.size());
             int n = 0;
             Vector<Statement> wrongStatements =  new Vector<>();
             Vector<Statement> newStatements =  new Vector<>();
             StmtIterator siter = namedModel.listStatements();
             while (siter.hasNext()) {
                 Statement s = siter.nextStatement();
                 n++;
                 if (n%1000==0) {
                     System.out.println(n);
                     //break;
                 }
                 if (s.getObject().isURIResource()) {
                     //@prefix oldbailey: <http://cltl.nl/oldbailey/> .
                     if (s.getObject().asResource().getNameSpace().equals("http://cltl.nl/oldbailey/")) {
                         wrongStatements.add(s);
                         Statement newS = namedModel.createStatement(s.getSubject(), OWL.sameAs, s.getObject());
                         newStatements.add(newS);
                        // namedModel.add(s.getSubject(), OWL.sameAs, s.getObject());
                     }
                     //            skos:relatedMatch  dbpr:Ownership , dbpr:Glentoran_F.C .
                     else if (s.getPredicate().getLocalName().equals("relatedMatch")) {
                         wrongStatements.add(s);
                         Statement newS = namedModel.createStatement(s.getSubject(), RDF.type, s.getObject());
                         Resource r = s.getObject().asResource();
                        // System.out.println("r.getURI() = " + r.getURI());
                         //String uri = "<"+r.getURI()+">";
                         if (simpleTaxonomy.subToSuper.containsKey(r.getURI())) {
                             String superSense = simpleTaxonomy.subToSuper.get(r.getURI());
                             Resource t = namedModel.createResource(superSense);
                             Statement dbpr = namedModel.createStatement(r, RDFS.subClassOf, t);
                             //System.out.println("dbpr.toString() = " + dbpr.toString());
                             newStatements.add(dbpr);
                         }
                         newStatements.add(newS);
                     }
                     else {
                     }
                 }
                 else {
                 }

             }
             namedModel.remove(wrongStatements);
             namedModel.add(newStatements);
             OutputStream fos = new FileOutputStream(trigFile+".fix.trig");
             RDFDataMgr.write(fos, dataset, RDFFormat.TRIG_PRETTY);
             dataset.close();
             newDataset.close();
             fos.close();
         }
         catch (Exception e) {
             e.printStackTrace();
         }
     }
     /// <http:/www.newsreader-project.eu/data/entities/James>
     ///            a              nwrontology:ENTITY , nwrontology:PER , oldbailey:t16901210-14-defend94 , oldbailey:t16840903-39-person185 , oldbailey:s16851014-1-person203 , oldbailey:f16931206-1-person12 ,

}
