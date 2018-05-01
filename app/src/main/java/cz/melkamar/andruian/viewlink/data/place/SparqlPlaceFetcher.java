package cz.melkamar.andruian.viewlink.data.place;

import android.util.Log;

import cz.melkamar.andruian.viewlink.exception.IndexServerNotDefinedException;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import cz.melkamar.andruian.viewlink.R;
import cz.melkamar.andruian.viewlink.data.NetHelper;
import cz.melkamar.andruian.viewlink.data.NetHelperProvider;
import cz.melkamar.andruian.viewlink.data.persistence.AppDatabase;
import cz.melkamar.andruian.viewlink.exception.PlaceFetchException;
import cz.melkamar.andruian.viewlink.exception.ReservedNameUsedException;
import cz.melkamar.andruian.viewlink.model.datadef.ClassToLocPath;
import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.model.datadef.SelectProperty;
import cz.melkamar.andruian.viewlink.model.place.MapElement;
import cz.melkamar.andruian.viewlink.model.place.Place;
import cz.melkamar.andruian.viewlink.model.place.Property;
import cz.melkamar.andruian.viewlink.ui.base.BaseView;
import cz.melkamar.andruian.viewlink.util.AsyncTaskResult;
import cz.melkamar.andruian.viewlink.util.KeyVal;
import cz.melkamar.andruian.viewlink.util.Util;

/**
 * This class facilitates querying a SPARQL endpoint with a "naive" SPARQL query and parsing the resulting data.
 */
public class SparqlPlaceFetcher {

    /**
     * Execute a SPARQL query at an endpoint defined in the data definition.
     *
     * The result in case of a successful query will always be a list of places, no clusters. In order to make it
     * easier for the consumers of this method, it is wrapped in the
     * {@link cz.melkamar.andruian.viewlink.data.place.PlaceFetcher.FetchPlacesResult} class.
     *
     * The method will run a SPARQL query and expect the server to be able to return the data in a CSV format.
     * The result is then parsed into native POJOs.
     *
     * @param dataDef   The data definition object specifying which index server to talk to.
     * @param latitude  The latitude coordinate of the point around which to query data.
     * @param longitude The longitude coordinate of the point around which to query data.
     * @param radius    The radius in which to query for data, in kilometers.
     * @return A {@link cz.melkamar.andruian.viewlink.data.place.PlaceFetcher.FetchPlacesResult} object containing the
     *         result of the query.
     * @throws PlaceFetchException When an exception occurs while querying the server.
     */
    public PlaceFetcher.FetchPlacesResult fetchPlaces(BaseView baseView, DataDef dataDef, double latitude, double longitude, double radius) throws PlaceFetchException, ReservedNameUsedException, IOException {
        Log.v("SparqlPlaceFetcher", "fetchPlaces [" + latitude + "," + longitude + "(" + radius + ") for " + dataDef);
        String placesCsv = getPlacesRawCsv(baseView, dataDef, latitude, longitude, radius);
        return new PlaceFetcher.FetchPlacesResult(
                PlaceFetcher.FetchPlacesResult.RESULT_TYPE_PLACES,
                placesFromCsv(placesCsv, dataDef)
        );
    }

    /**
     * Parse a string containing CSV data into a list of {@link MapElement} objects.
     *
     * @param csv     The source CSV data.
     * @param dataDef The data definition the data should be associated with.
     * @return A list of {@link MapElement} objects.
     * @throws IOException
     */
    private List<MapElement> placesFromCsv(String csv, DataDef dataDef) throws IOException {
        List<MapElement> result = new ArrayList<>();

        ICsvListReader listReader = new CsvListReader(new StringReader(csv), CsvPreference.STANDARD_PREFERENCE);
        List<String> header = listReader.read();

        while (true) {
            List<String> line = listReader.read();
            if (line == null) break;

            String label = line.get(5);
            if (label == null || label.isEmpty()) label = line.get(6);

            Place place = new Place(
                    line.get(0),
                    line.get(1),
                    Double.parseDouble(line.get(2)),
                    Double.parseDouble(line.get(3)),
                    line.get(4),
                    dataDef,
                    label
            );
            for (int i = 7; i < line.size(); i++)
                place.addProperty(new Property(header.get(i), line.get(i)));

            result.add(place);
        }

        return result;
    }


    /**
     * Perform a SPARQL query and return a string containing CSV response.
     *
     * @throws PlaceFetchException       When an error occurred during the fetch operation.
     * @throws ReservedNameUsedException When a reserved name for a property has been used in the data definition. The
     *                                   value of andr:selectProperty/s:name must not be reserved and already used in the query template.
     * @throws IOException
     */
    private String getPlacesRawCsv(BaseView baseView, DataDef dataDef, double latitude, double longitude, double radius) throws PlaceFetchException, ReservedNameUsedException, IOException {
        AppDatabase database = baseView.getViewLinkApplication().getAppDatabase();
        List<ClassToLocPath> classToLocPaths = database.classToLocPathDao().getAllForDataDefUriAndClass(dataDef.getUri(), dataDef.getLocationClassDef().getClassUri());
        List<SelectProperty> selectProperties = database.selectPropertyDao().getAllForDataDefUri(dataDef.getUri());

        if (classToLocPaths.size() != 1)
            Log.e("getSparqlQuery", classToLocPaths.size() + " ClassToLocPaths found for "
                    + dataDef.getUri() + " and class " + dataDef.getLocationClassDef().getClassUri());

        String queryTemplate = null;
        try {
            queryTemplate = Util.readRawTextFile(baseView.getActivity(), R.raw.placequery);
        } catch (IOException e) {
            Log.e("SparqlQueryBuilder", "Could not find queryTemplate query.", e);
            throw new IOException("Could not read query template from resources", e);
        }
        String query = getSparqlQuery(queryTemplate, dataDef, classToLocPaths.get(0), selectProperties, latitude, longitude, radius);

        NetHelper netHelper = NetHelperProvider.getProvider().getInstance();
        AsyncTaskResult<String> result = netHelper.httpPost(
                dataDef.getSourceClassDef().getSparqlEndpoint(),
                new KeyVal[]{new KeyVal("query", query)},
                new KeyVal("Accept", "text/csv"));

        if (result.hasError()) {
            throw new PlaceFetchException(result.getError().getMessage(), result.getError());
        }
        Log.d("getPlacesRawCsv", "Got string of length " + result.getResult().length());
        return result.getResult();
    }

    /**
     * Construct a SPARQL query for the given DataDef. This cannot be run on the UI thread.
     */
    private String getSparqlQuery(String queryTemplate,
                                  DataDef dataDef, ClassToLocPath classToLocPath,
                                  List<SelectProperty> selectProperties,
                                  double latitude, double longitude, double radius)
            throws ReservedNameUsedException {
        SparqlQueryBuilder builder = new SparqlQueryBuilder(
                queryTemplate,
                dataDef.getSourceClassDef().getClassUri(),
                dataDef.getSourceClassDef().getPathToLocationClass(),
                dataDef.getLocationClassDef().getSparqlEndpoint(),
                classToLocPath.getLatCoord(),
                classToLocPath.getLongCoord());

        for (SelectProperty selectProperty : selectProperties) {
            builder.addSelectProperty(selectProperty);
        }
        builder.limitToArea(latitude, longitude, radius);
        return builder.build();
    }
}
