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

public class PlaceFetcher {
    private IndexServerPlaceFetcher indexServerPlaceFetcher;
    private SparqlPlaceFetcher sparqlPlaceFetcher;

    PlaceFetcher(IndexServerPlaceFetcher indexServerPlaceFetcher, SparqlPlaceFetcher sparqlPlaceFetcher) {
        this.indexServerPlaceFetcher = indexServerPlaceFetcher;
        this.sparqlPlaceFetcher = sparqlPlaceFetcher;
    }

    /**
     * Fetch all places defined by this {@link PlaceFetcher}'s {@link cz.melkamar.andruian.viewlink.model.datadef.DataDef}.
     * If possible, use an index server. If that does not work or is not defined, resort to
     * contacting the SPARQL endpoint.
     *
     * @param latitude
     * @param longitude
     * @param radius
     * @return
     */
    public FetchPlacesResult fetchPlaces(BaseView view, DataDef dataDef, double latitude, double longitude, double radius) throws PlaceFetchException {
        Exception lastException = null;
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
