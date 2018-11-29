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

public class RewiteNwrRdf {


     static public void main (String[] args) {
         String pathToHierarchyFile = "/Code/vu/newsreader/vua-resources/instance_types_en.ttl.gz";     // English
         SimpleTaxonomy simpleTaxonomy = new SimpleTaxonomy();
         if (!pathToHierarchyFile.isEmpty()) {
            /// if Dutch
            //simpleTaxonomy.readSimpleTaxonomyFromFile(pathToHierarchyFile);
            /// if English
            simpleTaxonomy.readSimpleTaxonomyFromTtlFile(pathToHierarchyFile);
         }
         String pathToTrigFile = "/Users/piek/Desktop/DigHum-2018/4775434/OBO_XML_7-2/sessions/nwr-rdf/19af.trig";
         for (int i = 0; i < args.length; i++) {
             String arg = args[i];
             if (arg.equals("--trig") && args.length > (i + 1)) {
                 pathToTrigFile = args[i + 1];
             }
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
     ///            a              nwrontology:ENTITY , nwrontology:PER , oldbailey:t16901210-14-defend94 , oldbailey:t16840903-39-person185 , oldbailey:s16851014-1-person203 , oldbailey:f16931206-1-person12 , oldbailey:t16910708-20-defend47 , oldbailey:t16900430-21-defend131 , oldbailey:t16860707-22-defend121 , oldbailey:s16900430-1-person277 , oldbailey:s16870114-1-person224 , oldbailey:t16921207-29-victim103 , oldbailey:t16820223-9-defend21 , oldbailey:t16970224-10-person50 , oldbailey:t16800910a-2-defend11 , oldbailey:t16900226-15-victim62 , oldbailey:t16871207-53-victim231 , oldbailey:s16891009-1-person236 , oldbailey:s16820426-1-person80 , oldbailey:t16890703-26-victim86 , oldbailey:t16880113-25-defend118 , oldbailey:f16980720-1-person15 , oldbailey:s16850429-1-person282 , oldbailey:t16910115-10-person66 , oldbailey:s16850429-1-person284 , oldbailey:t16991011-6-victim42 , oldbailey:f16901015-1-person12 , oldbailey:t16980223-49-defend149 , oldbailey:t16801013-7-victim39 , oldbailey:t16980504-58-victim179 , oldbailey:t16841210-43-defend120 , oldbailey:t16931012-4-defend35 , oldbailey:t16810706-1-defend3 , oldbailey:t16870512-36-victim151 , oldbailey:s16880425-1-person289 , oldbailey:f16890828-1-person13 , oldbailey:s16891009-1-person228 , oldbailey:s16891009-1-person227 , oldbailey:t16861013-12-victim71 , oldbailey:s16800707-1-person56 , oldbailey:s16850429-1-person274 , oldbailey:t16960909-14-defend59 , oldbailey:t16960909-14-defend57 , oldbailey:t16770711a-8-defend24 , oldbailey:t16880711-17-victim104 , oldbailey:t16951014-27-defend88 , oldbailey:f16991011-1-person20 , oldbailey:t16850429-59-victim251 , oldbailey:f16880425-1-person24 , oldbailey:o16820224-2-victim63 , oldbailey:s16880425-1-person282 , oldbailey:t16891009-8-defend55 , oldbailey:t16920629-2-defend28 , oldbailey:f16980114-1-person7 , oldbailey:t16851014-29-defend129 , oldbailey:t16851014-29-victim131 , oldbailey:s16891009-1-person217 , oldbailey:t16880831-8-defend65 , oldbailey:t16921012-30-defend92 , oldbailey:t16960909-68-person184 , oldbailey:s16950220-1-person119 , oldbailey:t16950403-30-defend104 , oldbailey:o16830418-1-defend91 , oldbailey:t16800226-9-defend35 , oldbailey:t16850826-31-defend153 , oldbailey:t16870406-39-defend197 , oldbailey:s16860224-1-person215 , oldbailey:s16821206-1-person134 , oldbailey:t16850429-14-defend119 , oldbailey:s16930116-1-defend226 , oldbailey:t16880831-25-victim141 , oldbailey:f16930713-1-person5 , oldbailey:f16930713-1-person3 , oldbailey:t16950508-27-person100 , oldbailey:t16971208-37-defend109 , oldbailey:o16901015-1-defend218 , oldbailey:f16951014-1-person15 , oldbailey:t16941010-8-defend44 , oldbailey:f16880711-1-person25 , oldbailey:t16941010-8-defend43 , oldbailey:s16791015-1-person50 , oldbailey:s16800226-1-person70 , oldbailey:s16800226-1-person75 , oldbailey:t16991213-12-defend51 , oldbailey:t16930116-32-defend108 , oldbailey:s16830223-1-person68 , oldbailey:t16940524-25-person85 , oldbailey:s16930116-1-defend212 , oldbailey:s16830117-1-person151 , oldbailey:t16940524-22-defend79 , oldbailey:t16980504-14-victim55 , oldbailey:t16900903-20-defend122 , oldbailey:s16860114-1-person200 , oldbailey:f16930713-1-person18 , oldbailey:t16961209-18-victim77 , oldbailey:t16820224-4-defend12 , oldbailey:t16940524-9-victim46 , oldbailey:f16910909-1-person4 , oldbailey:t16990524-13-victim57 , oldbailey:t16861208-31-defend143 , oldbailey:f16890516-1-person7 , oldbailey:o16930116-2-defend184 , oldbailey:t16961209-54-defend167 , oldbailey:u16810706a-6-victim34 , oldbailey:f16990524-1-person13 , oldbailey:t16860707-26-defend140 , oldbailey:f16930713-1-person10 , oldbailey:t16910422-26-defend106 , oldbailey:s16860114-1-person203 , oldbailey:t16860901-25-defend118 , oldbailey:t16960227-41-defend115 , oldbailey:f16990524-1-person26 , oldbailey:s16880831-1-person217 , oldbailey:t16890516-69-defend312 , oldbailey:s16950220-1-person127 , oldbailey:t16851209-23-person126 , oldbailey:t16880222-2-victim25 , oldbailey:t16870701-25-victim115 , oldbailey:t16861013-15-defend80 , oldbailey:t16960227-50-defend139 , oldbailey:t16830829-12-defend28 , oldbailey:t16840227-20-person67 , oldbailey:f16850604-1-person1 , oldbailey:t16870223-44-victim232 , oldbailey:t16930906-25-defend90 , oldbailey:o16831010a-2-person120 , oldbailey:f16940711-1-person5 , oldbailey:t16800226-14-defend56 , oldbailey:o16901015-1-defend245 , oldbailey:t16930116-26-defend93 , oldbailey:t16850225-25-victim82 , oldbailey:t16861208-22-defend106 , oldbailey:o16831010a-2-person119 , oldbailey:t16991213-10-victim47 , oldbailey:o16840702-2-defend216 , oldbailey:t16850225-21-victim72 , oldbailey:t16890828-23-victim127 , oldbailey:t16890703-39-defend134 , oldbailey:s16861208-1-person232 , oldbailey:t16960909-44-victim140 , oldbailey:t16960227-10-victim47 , oldbailey:t16960227-4-defend32 , oldbailey:t16830117-9-victim30 , oldbailey:t16970519-33-defend109 , oldbailey:t16880222-9-defend51 , oldbailey:t16941010-12-defend53 , oldbailey:t16910422-1-defend28 , oldbailey:t16920406-1-person6 , oldbailey:t16800117-1-person9 , oldbailey:s16840702-1-person257 , oldbailey:t16851014-41-defend181 , oldbailey:t16840116-24-person101 , oldbailey:t16840116-24-person102 , oldbailey:t16781211e-4-defend20 , oldbailey:t16830418a-2-defend5 , oldbailey:t16851209-29-person161 , oldbailey:f16870114-1-person3 , oldbailey:o16930116-2-defend198 , oldbailey:t16860114-29-defend135 , oldbailey:f16860901-1-person9 , oldbailey:t16841210-19-victim73 , oldbailey:t16891009-24-defend128 , oldbailey:t16880113-6-victim32 , oldbailey:t16980223-13-victim58 , oldbailey:t16901015-30-defend148 , oldbailey:t16890516-9-person55 , oldbailey:u16820906-1-defend62 , oldbailey:t16860707-19-victim113 , oldbailey:s16831212-1-person143 , oldbailey:t16810117a-5-victim23 , oldbailey:t16900903-12-victim81 , oldbailey:t16880831-40-defend204 , oldbailey:t16901015-31-defend153 , oldbailey:f16871012-1-person3 , oldbailey:t16931012-24-defend81 , oldbailey:t16891009-24-defend132 , oldbailey:t16910909-28-defend88 , oldbailey:t16960909-19-victim84 , oldbailey:s16831212-1-person145 , oldbailey:t16900717-23-victim133 , oldbailey:s16820906a-1-person80 , oldbailey:t16820426-12-defend47 , oldbailey:t16980504-23-victim80 , oldbailey:t16850429-4-person55 , oldbailey:t16920629-14-defend58 , oldbailey:s16840409-1-person137 , oldbailey:t16930426-42-victim130 , oldbailey:t16870114-21-victim129 , oldbailey:t16931012-50-person153 , oldbailey:t16821206-9-defend48 , oldbailey:t16840903-44-victim205 , oldbailey:t16850429-3-victim50 , oldbailey:t16991213a-4-defend9 , oldbailey:t16840116-9-defend32 , oldbailey:t16850116-27-victim89 , oldbailey:t16980720-5-defend36 , oldbailey:t16841210-49-victim139 , oldbailey:t16940221-1-defend27 , oldbailey:t16901210-15-defend97 , oldbailey:f16971208-1-person22 , oldbailey:f16841210-1-person23 , oldbailey:f16950508-1-person20 , oldbailey:t16921207-32-victim110 , oldbailey:s16830418-1-person100 , oldbailey:s16890516-1-person539 , oldbailey:s16830418-1-person101 , oldbailey:t16900605-11-victim67 , oldbailey:t16900605-20-defend110 , oldbailey:t16991213-58-defend189 , oldbailey:f16930426-1-person10 , oldbailey:t16900903-23-person139 , oldbailey:t16931206-38-person127 , oldbailey:t16931206-43-victim141 , oldbailey:f16900605-1-person2 , oldbailey:t16801208-1-victim6 , oldbailey:t16930116-24-victim89 , oldbailey:t16900430-21-defend129 , oldbailey:f16910115-1-person2 , oldbailey:t16900430-8-person77 , oldbailey:f16910115-1-person3 , oldbailey:t16870114-36-victim192 , oldbailey:t16921207-46-victim150 , oldbailey:s16850429-1-person304 , oldbailey:t16800117-1-defend15 , oldbailey:f16900226-1-person17 , oldbailey:t16940524-11-victim53 , oldbailey:t16960708-9-defend48 , oldbailey:f16880113-1-person14 , oldbailey:s16820906-1-person64 , oldbailey:t16960227-51-defend141 , oldbailey:s16831010a-1-person149 , oldbailey:t16830712-1-defend2 , oldbailey:t16940418-6-victim40 , oldbailey:t16820223-9-victim25 , oldbailey:s16840702-1-defend280 , oldbailey:t16960227-14-defend59 , oldbailey:t16850225-23-defend76 , oldbailey:t16920629-46-defend125 , oldbailey:t16941010-27-victim92 , oldbailey:t16910422-41-defend143 , oldbailey:t16821206-25-defend129 , oldbailey:t16910527-3-defend8 , oldbailey:t16910422-17-person83 , oldbailey:u16820601a-6-victim42 , oldbailey:t16940830-6-victim42 , oldbailey:f16941010-1-person6 , oldbailey:u16820224-10-defend49 , oldbailey:s16890516-1-person513 , oldbailey:f16910218-1-person13 , oldbailey:f16941010-1-person9 , oldbailey:t16820906-1-defend4 , oldbailey:s16900430-1-person302 , oldbailey:t16980504-24-defend81 , oldbailey:t16901015-35-victim168 , oldbailey:t16950220-15-defend56 , oldbailey:t16831212-19-defend84 , oldbailey:f16850429-1-person11 , oldbailey:u16830524-11-defend15 , oldbailey:s16870512-1-person201 , oldbailey:t16841210-13-defend50 , oldbailey:f16820426-1-person1 , oldbailey:t16831212-11-defend56 , oldbailey:t16910218-31-defend104 , oldbailey:t16910422-45-victim155 , oldbailey:t16830418a-10-defend36 , oldbailey:t16901210-52-victim245 , oldbailey:t16870512-40-defend160 , oldbailey:t16970519-28-defend98 , oldbailey:f16930531-1-person13 , oldbailey:t16930906-77-defend209 , oldbailey:f16991011-1-person7 , oldbailey:f16850826-1-person1 , oldbailey:f16850429-1-person23 , oldbailey:t16980720-67-victim196 , oldbailey:o16840702-1-defend147 , oldbailey:f16861013-1-person5 , oldbailey:t16970707-52-defend139 , oldbailey:f16920629-1-person2 , oldbailey:t16940830-16-person69 , oldbailey:f16940711-1-person10 , oldbailey:t16970519-20-defend77 , oldbailey:t16960708-30-person100 , oldbailey:t16921207-15-defend63 , oldbailey:t16851014-35-defend153 , oldbailey:t16861208-48-person205 , oldbailey:t16971208-19-defend67 , oldbailey:t16870512-31-victim132 , oldbailey:t16960708-24-person84 , oldbailey:t16891211-41-victim190 , oldbailey:t16941010-5-defend38 , oldbailey:t16820426-18-defend63 , oldbailey:t16940418-13-victim61 , oldbailey:t16850826-19-defend106 , oldbailey:o16840702-2-defend170 , oldbailey:t16870114-29-defend165 , oldbailey:t16860114-25-victim122 , oldbailey:f16940221-1-person20 , oldbailey:t16901015-26-defend128 , oldbailey:f16901210-1-person20 , oldbailey:t16890516-15-victim81 , oldbailey:t16881010-11-victim74 , oldbailey:s16840116-1-person154 , oldbailey:t16950220-5-victim37 , oldbailey:t16860707-23-victim129 , oldbailey:f16970519-1-person19 , oldbailey:t16930426-43-victim132 , oldbailey:t16950828-72-defend173 , oldbailey:s16900605-1-person137 , oldbailey:t16880113-9-victim46 , oldbailey:t16951014-12-victim52 , oldbailey:s16871012-1-person190 , oldbailey:t16931012-3-defend33 , oldbailey:t16951014-12-victim51 , oldbailey:t16840116-24-defend98 , oldbailey:f16890828-1-person3 , oldbailey:t16810117a-6-defend25 , oldbailey:s16840702-1-defend242 , oldbailey:t16980223-27-victim100 , oldbailey:f16960708-1-person6 , oldbailey:t16840116-24-defend100 , oldbailey:t16900226-14-victim60 , oldbailey:s16791210-1-person33 , oldbailey:t16851014-41-person182 , oldbailey:t16810520-7-defend13 , oldbailey:s16890703-1-person187 , oldbailey:s16890703-1-person186 , oldbailey:t16880113-17-defend80 , oldbailey:t16861208-34-defend153 , oldbailey:t16891211-52-victim233 , oldbailey:t16991011-3-victim34 , oldbailey:t16841008-15-defend96 , oldbailey:f16930116-1-person15 , oldbailey:t16910909-18-person66 , oldbailey:t16880531-17-defend92 , oldbailey:f16840903-1-person17 , oldbailey:f16851014-1-person1 , oldbailey:t16900430-8-defend61 , oldbailey:t16880113-26-defend120 , oldbailey:f16850116-1-person1 , oldbailey:f16850116-1-person4 , oldbailey:f16850116-1-person5 , oldbailey:t16961209-68-defend199 , oldbailey:t16951014-19-victim70 , oldbailey:f16930116-1-person16 , oldbailey:t16860901-31-defend143 , oldbailey:t16851209-8-defend57 , oldbailey:s16830418a-1-person79 , oldbailey:s16881010-1-defend271 , oldbailey:t16910218-21-defend76 , oldbailey:t16891009-12-defend83 , oldbailey:s16830418a-1-person77 , oldbailey:s16840116-1-person170 , oldbailey:s16810117-1-person68 , oldbailey:t16980504-59-victim181 , oldbailey:f16850716-1-person1 , oldbailey:f16980720-1-person9 , oldbailey:t16970519-9-victim51 , oldbailey:t16870114-18-defend110 , oldbailey:t16911209-13-defend52 , oldbailey:t16931012-50-defend152 , oldbailey:t16980114-8-victim47 , oldbailey:u16831010a-15-defend46 , oldbailey:t16850429-33-defend171 , oldbailey:t16980114-31-victim109 , oldbailey:t16880831-35-defend185 , oldbailey:t16880831-6-defend50 , oldbailey:t16860707-14-defend89 , oldbailey:f16901015-1-person6 , oldbailey:s16881010-1-person207 , oldbailey:t16880222-11-victim59 , oldbailey:t16980504-46-defend145 , oldbailey:t16990524-14-defend58 , oldbailey:t16951014-33-defend104 , oldbailey:t16851209-28-person155 , oldbailey:t16840702-25-defend86 , oldbailey:t16940221-16-victim64 , oldbailey:t16950508-15-defend66 , oldbailey:t16851014-5-victim44 , oldbailey:f16891211-1-person14 , oldbailey:t16820223-12-victim32 , oldbailey:t16830829-15-defend36 , oldbailey:f16951014-1-person3 , oldbailey:t16830524-11-defend36 , oldbailey:t16870406-4-person44 , oldbailey:t16910909-36-person111 , oldbailey:t16950828-44-defend123 , oldbailey:t16951203-6-victim39 , oldbailey:t16970224-44-person142 , oldbailey:t16901015-30-person151 , oldbailey:t16961209-48-victim156 , oldbailey:t16950508-40-person131 , oldbailey:f16841210-1-person1 , oldbailey:t16960909-21-victim88 , oldbailey:f16900903-1-person13 , oldbailey:f16851209-1-person8 , oldbailey:t16910218-1-defend28 , oldbailey:s16840227-1-person139 , oldbailey:f16880113-1-person3 , oldbailey:t16980114-26-victim95 , oldbailey:f16991213-1-person8 , oldbailey:t16870406-43-victim215 , oldbailey:f16940524-1-person14 , oldbailey:f16940524-1-person15 , oldbailey:t16840702-10-victim39 , oldbailey:s16840702-1-defend326 , oldbailey:f16980504-1-person5 , oldbailey:t16811017a-3-defend7 , oldbailey:f16850225-1-person1 , oldbailey:f16940524-1-person19 , oldbailey:t16840702-8-victim34 , oldbailey:t16891211-3-person38 , oldbailey:f16980608-1-person8 , oldbailey:f16980608-1-person6 , oldbailey:t16861208-48-defend204 , oldbailey:t16830223-12-defend39 , oldbailey:t16991213-26-defend92 , oldbailey:t16850826-23-victim123 , oldbailey:t16880425-40-defend203 , oldbailey:f16920629-1-person19 , oldbailey:f16860414-1-person14 , oldbailey:t16980114-25-defend86 , oldbailey:t16951203-33-victim100 , oldbailey:t16961014-18-victim72 , oldbailey:t16970901-8-victim47 , oldbailey:t16841210-9-victim43 , oldbailey:f16860414-1-person12 , oldbailey:t16870114-27-defend157 , oldbailey:s16850826-1-person159 , oldbailey:t16931206-9-defend52 , oldbailey:t16921207-56-victim173 , oldbailey:t16950403-11-defend52 , oldbailey:t16850116-27-defend88 , oldbailey:t16950828-68-defend169 , oldbailey:u16830418a-10-person82 , oldbailey:t16960227-27-defend86 , oldbailey:o16820223-1-victim56 , oldbailey:t16810706a-6-victim27 , oldbailey:t16921207-14-victim62 , oldbailey:f16851209-1-person24 , oldbailey:t16880711-36-defend182 , oldbailey:t16850429-28-victim162 , oldbailey:t16890516-73-defend330 , oldbailey:t16930116-16-defend66 , oldbailey:t16990524-34-victim106 , oldbailey:f16900115-1-person2 , oldbailey:f16961014-1-person3 , oldbailey:t16841210-7-defend39 , oldbailey:t16860114-30-defend139 , oldbailey:f16940221-1-person8 , oldbailey:t16910218-13-defend57 , oldbailey:t16931206-22-defend87 , oldbailey:t16861208-34-person157 , oldbailey:t16961014-12-victim57 , oldbailey:u16830418-6-person70 , oldbailey:t16831010a-15-defend65 , oldbailey:s16880113-1-person208 , oldbailey:t16870406-6-victim56 , oldbailey:t16951203-40-victim115 , oldbailey:t16841210-12-victim49 , oldbailey:t16971208-14-victim57 , oldbailey:t16880711-1-defend30 , oldbailey:t16861208-28-victim133 , oldbailey:t16980720-18-defend71 , oldbailey:t16861208-45-victim193 , oldbailey:t16781211e-35-defend96 , oldbailey:t16871012-8-victim60 , oldbailey:f16850429-1-person1 , oldbailey:f16931012-1-person5 , oldbailey:t16870831-1-defend29 , oldbailey:t16920115-24-person87 , oldbailey:f16860114-1-person3 , oldbailey:t16980223-46-defend140 , oldbailey:f16800117-1-person2 , oldbailey:t16940711-6-defend39 , oldbailey:t16830117-12-defend39 , oldbailey:t16921207-5-victim38 , oldbailey:t16910708-29-victim75 , oldbailey:t16910527-1-defend2 , oldbailey:f16921012-1-person7 , oldbailey:t16740429-6-person16 , oldbailey:f16921012-1-person8 , oldbailey:f16980608-1-person14 , oldbailey:s16881010-1-defend260 , oldbailey:s16880113-1-person211 , oldbailey:s16881010-1-defend264 , oldbailey:t16900903-3-defend34 , oldbailey:s16831212-1-person150 , oldbailey:t16920831-7-defend41 , oldbailey:t16870512-13-defend73 , oldbailey:t16970707-10-defend49 , oldbailey:t16910115-9-person54 , oldbailey:t16970224-5-victim35 , oldbailey:s16830418a-1-person83 , oldbailey:t16980608-68-person182 , oldbailey:t16980720-25-victim92 , oldbailey:t16991213-56-victim186 , oldbailey:t16900717-8-defend62 , oldbailey:t16871207-35-defend168 , oldbailey:t16870114-20-defend123 , oldbailey:t16931206-31-victim110 , oldbailey:s16850225-1-person134 , oldbailey:t16840116-12-victim45 , oldbailey:t16980504-73-defend216 , oldbailey:t16931012-36-defend107 , oldbailey:t16931206-25-victim97 , oldbailey:s16860901-1-person214 , oldbailey:t16891211-9-victim60 , oldbailey:t16960909-56-defend162 , oldbailey:t16871207-12-person67 , oldbailey:t16880425-27-defend145 , oldbailey:t16921207-17-defend69 , oldbailey:f16781211e-1-person2 , oldbailey:f16870406-1-person13 , oldbailey:t16940418-36-defend114 , oldbailey:t16940830-45-victim138 , oldbailey:t16781211e-36-defend97 , oldbailey:s16900903-1-person257 , oldbailey:t16961209-41-victim135 , oldbailey:t16961014-14-defend64 , oldbailey:t16970224-12-victim56 , oldbailey:t16910218-22-victim80 , oldbailey:s16900903-1-person252 , oldbailey:t16850716-21-victim105 , oldbailey:t16861208-38-defend168 , oldbailey:t16991011-7-victim46 , oldbailey:t16970224-31-defend110 , oldbailey:s16900430-1-person289 , oldbailey:s16880711-1-person216 , oldbailey:t16840227-20-defend58 , oldbailey:t16920831-20-victim78 , oldbailey:t16930116-49-defend147 , oldbailey:f16960708-1-person19 , oldbailey:t16940711-14-victim61 , oldbailey:t16940221-50-defend146 , oldbailey:s16870114-1-person232 , oldbailey:t16871012-16-defend94 , oldbailey:t16980504-36-person120 , oldbailey:t16991011-38-defend131 , oldbailey:f16861013-1-person21 , oldbailey:t16781211e-38-person104 , oldbailey:t16881010-12-victim77 , oldbailey:f16880531-1-person21 , oldbailey:t16861208-49-defend207 , oldbailey:t16931206-23-defend89 , oldbailey:t16950828-41-victim116 , oldbailey:t16950828-15-victim64 , oldbailey:t16870701-9-victim59 , oldbailey:t16910708-32-defend84 , oldbailey:u16820601a-5-defend39 , oldbailey:t16840409-6-victim14 , oldbailey:t16850604-6-defend52 , oldbailey:t16850429-18-defend133 , oldbailey:t16850604-6-defend50 , oldbailey:t16920831-23-defend82 , oldbailey:t16930116-46-defend136 , oldbailey:t16820223-11-defend27 , oldbailey:o16831010a-1-defend117 , oldbailey:t16890703-46-defend154 , oldbailey:t16961209-92-defend239 , oldbailey:t16980720-44-victim143 , oldbailey:t16890703-46-defend153 , oldbailey:s16880222-1-person166 , oldbailey:t16980223-21-defend81 , oldbailey:t16920115-8-defend44 , oldbailey:t16880222-21-defend112 , oldbailey:t16880222-24-person144 , oldbailey:t16900903-50-defend241 , oldbailey:t16931012-16-defend63 , oldbailey:t16920629-38-defend110 , oldbailey:t16860224-9-defend60 , oldbailey:t16920629-37-defend109 , oldbailey:t16820224-12-victim47 , oldbailey:t16970224-47-defend147 , oldbailey:t16860901-17-defend89 , oldbailey:t16991011-31-person115 , oldbailey:t16921012-35-victim103 , oldbailey:t16870114-37-defend195 , oldbailey:t16950220-16-defend60 , oldbailey:s16850604-1-person179 , oldbailey:t16871012-8-defend58 , oldbailey:t16901210-56-person272 , oldbailey:t16940418-33-defend109 , oldbailey:t16901210-56-person269 , oldbailey:t16940221-35-defend106 , oldbailey:t16930906-70-victim195 , oldbailey:t16820601a-6-victim25 , oldbailey:t16900226-33-defend111 , oldbailey:t16920115-25-defend91 , oldbailey:f16950508-1-person8 , oldbailey:f16950508-1-person6 , oldbailey:t16991011-31-person104 , oldbailey:s16901015-1-person257 , oldbailey:s16830524-1-person49 , oldbailey:t16851209-42-defend206 , oldbailey:s16901015-1-person254 , oldbailey:o16881010-3-defend171 , oldbailey:s16901015-1-person255 , oldbailey:t16881010-7-defend56 , oldbailey:f16940418-1-person9 , oldbailey:t16851209-38-victim192 , oldbailey:t16841210-49-defend138 , oldbailey:s16841008-1-person146 , oldbailey:t16880113-33-defend141 , oldbailey:t16840116-20-defend75 , oldbailey:s16901210-1-person292 , oldbailey:o16881010-3-defend160 , oldbailey:t16930531-19-person72 , oldbailey:t16940830-48-victim146 , oldbailey:o16881010-3-defend164 , oldbailey:s16900717-1-person155 , oldbailey:t16910708-8-victim22 , oldbailey:t16951203-44-victim121 , oldbailey:t16840903-30-victim153 , oldbailey:f16891009-1-person13 , oldbailey:t16931206-42-defend136 , oldbailey:t16900226-18-person80 , oldbailey:t16900226-18-person81 , oldbailey:t16891009-28-defend144 , oldbailey:t16940418-12-defend58 , oldbailey:t16860520-40-defend120 , oldbailey:o16900226-2-defend141 , oldbailey:s16850429-1-person293 , oldbailey:t16880711-16-victim100 , oldbailey:t16850429-40-victim189 , oldbailey:u16830418a-10-defend78 , oldbailey:u16830524-12-defend21 , oldbailey:t16840702-22-defend76 , oldbailey:t16840409-1-defend4 , oldbailey:t16870701-12-person71 , oldbailey:f16961014-1-person15 ;

}
