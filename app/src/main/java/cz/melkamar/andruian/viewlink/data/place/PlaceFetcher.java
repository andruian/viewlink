package cz.melkamar.andruian.viewlink.data.place;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.melkamar.andruian.viewlink.exception.IndexServerNotDefinedException;
import cz.melkamar.andruian.viewlink.exception.PlaceFetchException;
import cz.melkamar.andruian.viewlink.exception.ReservedNameUsedException;
import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.model.place.MapElement;
import cz.melkamar.andruian.viewlink.model.place.Place;
import cz.melkamar.andruian.viewlink.model.place.PlaceCluster;
import cz.melkamar.andruian.viewlink.ui.base.BaseView;

/**
 * This class provides places-fetching functionality.
 *
 * It implements both index server-based querying and naive SPARQL querying. Both types of querying are delegated
 * to their respective classes.
 */
public class PlaceFetcher {
    private IndexServerPlaceFetcher indexServerPlaceFetcher;
    private SparqlPlaceFetcher sparqlPlaceFetcher;

    PlaceFetcher(IndexServerPlaceFetcher indexServerPlaceFetcher, SparqlPlaceFetcher sparqlPlaceFetcher) {
        this.indexServerPlaceFetcher = indexServerPlaceFetcher;
        this.sparqlPlaceFetcher = sparqlPlaceFetcher;
    }

    // TODO fetchPlaces may be extracted into an interface - that will require to create a hierarchy of exceptions however
    /**
     * Fetch all places defined by this {@link PlaceFetcher}'s {@link cz.melkamar.andruian.viewlink.model.datadef.DataDef}.
     * If possible, use an index server. If that does not work or is not defined, resort to
     * contacting the SPARQL endpoint.
     *
     * @param latitude  The latitude coordinate of the point around which to query data.
     * @param longitude The longitude coordinate of the point around which to query data.
     * @param radius    The radius in which to query for data, in kilometers.
     * @return A {@link FetchPlacesResult} object containing the result of the query.
     */
    public FetchPlacesResult fetchPlaces(BaseView view, DataDef dataDef, double latitude, double longitude, double radius) throws PlaceFetchException {
        Exception lastException = null;
        // TODO inform the user if the indexing query fails unexpectedly - not when no server defined, but when an error occurs
        try {
            return indexServerPlaceFetcher.fetchPlaces(dataDef, latitude, longitude, radius);
        } catch (PlaceFetchException e) {
            lastException = e;
            Log.i("fetchPlaces", "Could not fetch places from the index server", e);
//            view.showMessage("An error occurred when fetching data from the index server. "+e.getMessage());
        } catch (IndexServerNotDefinedException e) {
            e.printStackTrace();
            Log.i("fetchPlaces", "Index server not defined", e);
        }

        try {
            return sparqlPlaceFetcher.fetchPlaces(view, dataDef, latitude, longitude, radius);
        } catch (PlaceFetchException e) {
            lastException = e;
            Log.i("fetchPlaces", "Could not fetch places from the SPARQL endpoint", e);
        } catch (ReservedNameUsedException | IOException e) {
            lastException = e;
            e.printStackTrace();
            Log.e("fetchPlaces", e.getMessage(), e);
        }

        throw new PlaceFetchException(lastException.getMessage(), lastException);
    }

    /**
     * A wrapper class for a result of a query.
     * {@link FetchPlacesResult#resultType} identifies what type of a result is contained:
     * <ul>
     * <li>
     * {@link FetchPlacesResult#RESULT_TYPE_PLACES} indicates that individual places are contained in the result and
     * {@link FetchPlacesResult#getPlaces()} method should be used to retrieve a list of {@link Place} objects.
     * </li>
     * <li>{@link FetchPlacesResult#RESULT_TYPE_CLUSTERS} indicates that clusters of places are contained in the result
     * and {@link FetchPlacesResult#getClusters()} method should be used to retrieve a list of {@link PlaceCluster}
     * objects.</li>
     * </ul>
     */
    public static class FetchPlacesResult {
        public static final int RESULT_TYPE_PLACES = 0;
        public static final int RESULT_TYPE_CLUSTERS = 1;

        public final int resultType;
        public final List<MapElement> places;

        public FetchPlacesResult(int resultType, List<MapElement> places) {
            this.resultType = resultType;
            this.places = places;
        }

        public List<Place> getPlaces() {
            if (resultType != RESULT_TYPE_PLACES)
                throw new IllegalArgumentException("The result is not RESULT_TYPE_PLACES.");
            List<Place> result = new ArrayList<>(places.size());
            for (MapElement place : places) {
                result.add((Place) place);
            }
            return result;
        }

        public List<PlaceCluster> getClusters() {
            if (resultType != RESULT_TYPE_CLUSTERS)
                throw new IllegalArgumentException("The result is not RESULT_TYPE_CLUSTERS.");
            List<PlaceCluster> result = new ArrayList<>(places.size());
            for (MapElement place : places) {
                result.add((PlaceCluster) place);
            }
            return result;
        }
    }
}
