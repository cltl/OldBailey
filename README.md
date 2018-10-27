# OldBailey
Processing the OldBailey data to create LOD

The main function OldBaileyXmlOrg reads an XML file and extracts the meta data fields from the XML together with
a caseId. The caseId is used to load the corresponding TRiG-RDF files using the Jena library.
The meta data fields are added as properties to the events and the result is saved to an output file as TRiG-RDF.

Output example:

    <http://cltl.nl/old_bailey/sessionpaper/t18390513-1553#ev21>
            a                       ili:i50522 , sem:Event , nwrontology:contextualEvent ;
            gaf:denotedBy           <http://cltl.nl/old_bailey/sessionpaper/t18390513-1553#char=330,336&word=w74&term=t74&sentence=5&paragraph=1> , <http://cltl.nl/old_bailey/sessionpaper/t18390513-1553#char=509,515&word=w112&term=t112&sentence=7&paragraph=1> ;
            nwr:offenceCategory     "theft" ;
            nwr:offenceSubcategory  "simpleLarceny" ;
            nwr:phrasecount         <http://cltl.nl/old_bailey/sessionpaper/t18390513-1553#ev21#0> ;
            nwr:verdictCategory     "notGuilty" ;
            skos:prefLabel          "basket" ;
            skos:relatedMatch       <http://eurovoc.europa.eu/1085> , <http://eurovoc.europa.eu/1602> , <http://eurovoc.europa.eu/2734> , <http://eurovoc.europa.eu/584> , <http://eurovoc.europa.eu/3784> , <http://eurovoc.europa.eu/368> .


How to run:

OldBaileyXmlOrg --xml <path to XML files> --trig <path to trig files> --extension ".trig"

