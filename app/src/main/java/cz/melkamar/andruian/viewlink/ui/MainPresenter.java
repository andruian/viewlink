package cz.melkamar.andruian.viewlink.ui;

import android.util.Log;
import cz.melkamar.andruian.viewlink.data.DataManagerProvider;
import cz.melkamar.andruian.viewlink.model.DataSource;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public class MainPresenter implements MainMvpPresenter {
    private MainMvpView view;

    public MainPresenter(MainMvpView view) {
        this.view = view;
    }

    @Override
    public void manageDataSources() {
        Log.i("manageDataSources", "foo");
//        view.showMessage("add datasource: "+ DataManagerProvider.getDataManager().getHttpFile("someUrl"));
        view.showManageDatasources();
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {
        view = null;
    }

    @Override
    public void onFabClicked() {
//        view.setKeepMapCentered(true);
        DataManagerProvider.getDataManager().getDataSource("https://raw.githubusercontent.com/andruian/example-data/master/ruian/ruian-datadef.ttl", this::dostuff
        );
    }

    public void dostuff(DataSource dataSource){
        Log.i(dataSource.getName(), dataSource.getContent());
//        InputStream stream = new ByteArrayInputStream(dataSource.getContent().getBytes(StandardCharsets.UTF_8));
        InputStream stream = new ByteArrayInputStream(foostr().getBytes(StandardCharsets.UTF_8));
        String lines[] = foostr().split("\\r?\\n");
        for (int i=0; i<lines.length; i++) Log.i((i+1)+"", lines[i]);


        try {
            Model model = Rio.parse(stream, "", RDFFormat.TURTLE);
            System.out.println("foo");
        } catch (IOException e) {
            Log.e("parse err", "x", e);
            e.printStackTrace();
        }
    }

    public String foostr(){
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
