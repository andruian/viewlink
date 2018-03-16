package cz.melkamar.andruian.viewlink.data;

import android.util.Log;

import org.eclipse.rdf4j.rio.RDFFormat;

import java.io.IOException;
import java.util.List;

import cz.melkamar.andruian.ddfparser.DataDefParser;
import cz.melkamar.andruian.ddfparser.exception.DataDefFormatException;
import cz.melkamar.andruian.ddfparser.exception.RdfFormatException;
import cz.melkamar.andruian.ddfparser.model.DataDef;
import cz.melkamar.andruian.viewlink.util.AsyncTaskResult;
import cz.melkamar.andruian.viewlink.util.KeyVal;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public class DataManagerImpl implements DataManager {
    private final NetHelper netHelper;

    public DataManagerImpl(NetHelper netHelper) {
        this.netHelper = netHelper;
    }

    @Override
    public AsyncTaskResult<String> httpGet(String url, KeyVal[] data, KeyVal... headers) {
        return netHelper.httpGet(url, data, headers);
    }

    @Override
    public void getHttpFileAsync(String url, HttpRequestCallback callback) {
        netHelper.getHttpFileAsync(url, callback);
    }

    @Override
    public AsyncTaskResult<String> httpPost(String url, KeyVal[] data, KeyVal... keyVals) {
        return netHelper.httpPost(url, data, keyVals);
    }

    @Override
    public void httpPostAsync(HttpRequestCallback callback, String url, String data, KeyVal... keyVals) {
        netHelper.httpPostAsync(callback, url, data, keyVals);
    }

    @Override
    public void getDataDefs(String url, GetDataDefsCallback callback) {
//        netHelper.getHttpFileAsync(url, response -> finishGetDataDefs(response, callback));
        netHelper.getHttpFileAsync(url, result -> finishGetDataDefs(result, callback));
    }

    protected void finishGetDataDefs(AsyncTaskResult<String> result, GetDataDefsCallback callback) {
        if (result.hasError()) {
            Log.w("finishGetDataDefs", result.getError().getMessage(), result.getError());
            callback.onFetchError(result.getError().getMessage(), 3);
            return;
        }
        String response = result.getResult();

        try {
            if (response == null) throw new IOException("Response body is empty");
            response = fakeBody(); // TODO remove this fake content

            Log.v("finishGetDataDefs body", response);
            List<DataDef> dataDefList = new DataDefParser().parse(response, RDFFormat.TURTLE);
            callback.onDataDefsFetched(dataDefList);
        } catch (IOException e) {
            Log.e("finishGetDataDefs", "Could not get response.body.string", e);
            e.printStackTrace();
            callback.onFetchError(e.toString(), 0);
        } catch (DataDefFormatException e) {
            Log.e("finishGetDataDefs", "Invalid datadef", e);
            e.printStackTrace();
            callback.onFetchError(e.toString(), 1);
        } catch (RdfFormatException e) {
            Log.e("finishGetDataDefs", "Invalid rdf format", e);
            e.printStackTrace();
            callback.onFetchError(e.toString(), 2);
        }
    }

    String fakeBody() {
        return "@prefix andr: <http://purl.org/net/andruian/datadef#> .\n" +
                "@prefix ruian: <http://ruian.linked.opendata.cz/ontology/> .\n" +
                "@prefix sp: <http://spinrdf.org/sp#> .\n" +
                "@prefix s: <http://schema.org/> .\n" +
                "@prefix ex: <http://example.org/> .\n" +
                "@prefix : <http://foo/> .\n" +
                "@prefix skos: <http://www.w3.org/2004/02/skos/core#> .\n" +
                "\n" +
                ":dataDef\n" +
                "    a                 andr:DataDef;\n" +
                "    andr:locationClassDef  :locationDef;\n" +
                "    andr:sourceClassDef :sourceClassDef;\n" +
                "    andr:indexServer  :indexServer;\n" +
                "    skos:prefLabel \"Example data definition\";\n" +
                "    .\n" +
                "\n" +
                "#\n" +
                "# INDEX SERVER\n" +
                "#\n" +
                ":indexServer\n" +
                "    a        andr:IndexServer;\n" +
                "    andr:uri \"http://10.0.2.2:8080\";\n" +
                "    andr:version 1;\n" +
                "    .\n" +
                "\n" +
                "#\n" +
                "# DATA DEFINITION\n" +
                "#\n" +
                ":sourceClassDef\n" +
                "    a                        andr:SourceClassDef;\n" +
                "#    andr:sparqlEndpoint      <http://localhost:3030/foo/query>;\n" +
                "    andr:sparqlEndpoint      \"http://10.0.2.2:3030/ruian/query\";\n" +
                "    andr:class               ex:MyObject;\n" +
                "    andr:pathToLocationClass ( ex:someLink ex:linksTo );\n" +
                "    andr:selectProperty [ a andr:SelectProperty;\n" +
                "                          s:name \"labelForIdProperty\";\n" +
                "                          andr:propertyPath ex:id\n" +
                "                        ];\n" +
                "    .\n" +
                "\n" +
                "\n" +
                "#\n" +
                "# LOCATION DEFINITON\n" +
                "#\n" +
                ":locationDef\n" +
                "    a                   andr:LocationDef;\n" +
                "    andr:sparqlEndpoint \"http://ruian.linked.opendata.cz/sparql\";\n" +
                "    andr:class          ruian:AdresniMisto;\n" +
                "    andr:classToLocPath :adresniMistoClassToLocPath;\n" +
                "    .\n" +
                "\n" +
                "# Description of how to get from object ruian:AdresniMisto to its lat/long coordinates\n" +
                ":adresniMistoClassToLocPath\n" +
                "    a          andr:ClassToLocPath;\n" +
                "    andr:class ruian:AdresniMisto;\n" +
                "    andr:lat   ( ruian:adresniBod s:geo s:latitude );\n" +
                "    andr:long  ( ruian:adresniBod s:geo s:longitude );\n" +
                "    .\n";
    }
}
