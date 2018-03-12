package cz.melkamar.andruian.viewlink.data.rdf;


import android.support.test.filters.SmallTest;
import android.util.Log;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@SmallTest
public class DataDefParserTest {
    @Test
    public void foo() {
        InputStream stream = new ByteArrayInputStream(foostr().getBytes(StandardCharsets.UTF_8));
        String lines[] = foostr().split("\\r?\\n");
        for (int i = 0; i < lines.length; i++) Log.i((i + 1) + "", lines[i]);


        try {
            Model model = Rio.parse(stream, "", RDFFormat.TURTLE);
            IRI datadefClass = SimpleValueFactory.getInstance().createIRI("http://purl.org/net/andruian/datadef#DataDef");
            Model dataDefs = model.filter(null, null, datadefClass);
            for (Statement st: dataDefs){
                // the subject is always `ex:VanGogh`, an IRI, so we can safely cast it
                IRI subject = (IRI)st.getSubject();
                // the property predicate is always an IRI
                IRI predicate = st.getPredicate();

                // the property value could be an IRI, a BNode, or a Literal. In RDF4J,
                // Value is is the supertype of all possible kinds of RDF values.
                Value object = st.getObject();

                // let's print out the statement in a nice way. We ignore the namespaces
                // and only print the local name of each IRI
                System.out.print(subject.getLocalName() + " " + predicate.getLocalName() + " ");
                if (object instanceof Literal) {
                    // It's a literal. print it out nicely, in quotes, and without any ugly
                    // datatype stuff
                    System.out.println("\"" + ((Literal)object).getLabel() + "\"");
                }
                else if (object instanceof  IRI) {
                    // It's an IRI. Print it out, but leave off the namespace part.
                    System.out.println(((IRI)object).getLocalName());
                }
                else {
                    // It's a blank node. Just print out the internal identifier.
                    System.out.println(object);
                }
            }
            System.out.println("foo");
        } catch (IOException e) {
            Log.e("parse err", "x", e);
            e.printStackTrace();
        }
    }

    public String foostr() {
        return "@prefix andr: <http://purl.org/net/andruian/datadef#> .\n" +
                "@prefix ruian: <http://ruian.linked.opendata.cz/ontology/> .\n" +
                "@prefix sp: <http://spinrdf.org/sp#> .\n" +
                "@prefix s: <http://schema.org/> .\n" +
                "@prefix ex: <http://example.org/> .\n" +
                "@prefix : <http://foo/> .\n" +
                "\n" +
                ":dataDef\n" +
                "    a                 andr:DataDef;\n" +
                "    andr:locationDef  :locationDef;\n" +
                "    andr:sourceClassDef :sourceClassDef;\n" +
                "    andr:indexServer  :indexServer;\n" +
                "    .\n" +
                "\n" +
                "#\n" +
                "# INDEX SERVER\n" +
                "#\n" +
                ":indexServer\n" +
                "    a        andr:IndexServer;\n" +
                "    andr:uri <http://localhost:8080>;\n" +
                "    andr:version 1;\n" +
                "    .\n" +
                "\n" +
                "#\n" +
                "# DATA DEFINITION\n" +
                "#\n" +
                ":sourceClassDef\n" +
                "    a                        andr:SourceClassDef;\n" +
                "    andr:sparqlEndpoint      <http://localhost:3030/foo/query>;\n" +
                "    andr:class               ex:MyObject;\n" +
                "    andr:pathToLocationClass [ a sp:SeqPath;\n" +
                "                               sp:path1 ex:someLink;\n" +
                "                               sp:path2 ex:linksTo;\n" +
                "                             ];\n" +
                "    andr:selectProperty [ a andr:SelectProperty;\n" +
                "                          s:name \"labelForIdProperty\";\n" +
                "                          sp:path [ a        sp:SeqPath;\n" +
                "                                    sp:path1 ex:id\n" +
                "                                  ];\n" +
                "                        ];\n" +
                "    .\n" +
                "\n" +
                "\n" +
                "#\n" +
                "# LOCATION DEFINITON\n" +
                "#\n" +
                ":locationDef\n" +
                "    a                   andr:LocationDef;\n" +
                "    andr:sparqlEndpoint <http://ruian.linked.opendata.cz/sparql>;\n" +
                "    andr:class          ruian:AdresniMisto;\n" +
                "    andr:classToLocPath :adresniMistoClassToLocPath;\n" +
                "    .\n" +
                "\n" +
                "# Description of how to get from object ruian:AdresniMisto to its lat/long coordinates\n" +
                ":adresniMistoClassToLocPath\n" +
                "    a          andr:ClassToLocPath;\n" +
                "    andr:class ruian:AdresniMisto;\n" +
                "    andr:lat   [ a sp:SeqPath;\n" +
                "                 sp:path1 ruian:adresniBod;\n" +
                "                 sp:path2 [\n" +
                "                     sp:path1 s:geo;\n" +
                "                     sp:path2 s:latitude;\n" +
                "                 ]\n" +
                "               ];\n" +
                "    andr:long  [ a sp:SeqPath;\n" +
                "                 sp:path1 ruian:adresniBod;\n" +
                "                 sp:path2 [\n" +
                "                     sp:path1 s:geo;\n" +
                "                     sp:path2 s:longitude;\n" +
                "                 ]\n" +
                "               ];\n" +
                "    .";
    }
}
