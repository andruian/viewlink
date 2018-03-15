package cz.melkamar.andruian.viewlink.data.place;

import android.util.Log;

import java.util.List;

import cz.melkamar.andruian.viewlink.exception.PlaceFetchException;
import cz.melkamar.andruian.viewlink.exception.ReservedNameUsedException;
import cz.melkamar.andruian.viewlink.model.Place;
import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.ui.base.BaseView;
import cz.melkamar.andruian.viewlink.util.AsyncTaskResult;

public class PlaceFetcherHelper {
    private IndexServerPlaceFetcher indexServerPlaceFetcher;
    private SparqlPlaceFetcher sparqlPlaceFetcher;

    public PlaceFetcherHelper(IndexServerPlaceFetcher indexServerPlaceFetcher, SparqlPlaceFetcher sparqlPlaceFetcher) {
        this.indexServerPlaceFetcher = indexServerPlaceFetcher;
        this.sparqlPlaceFetcher = sparqlPlaceFetcher;
    }

    public PlaceFetcherHelper() {
        this.indexServerPlaceFetcher = new IndexServerPlaceFetcher();
        this.sparqlPlaceFetcher = new SparqlPlaceFetcher();
    }

    /**
     * Fetch all places defined by this {@link PlaceFetcherHelper}'s {@link cz.melkamar.andruian.viewlink.model.datadef.DataDef}.
     * If possible, use an index server. If that does not work or is not defined, resort to
     * contacting the SPARQL endpoint.
     *
     * @param latitude
     * @param longitude
     * @param radius
     * @return
     */
    public void fetchPlaces(BaseView view, DataDef dataDef, double latitude, double longitude, double radius, Listener listener) {
        try {
            List<Place> result = indexServerPlaceFetcher.fetchPlaces(dataDef, latitude, longitude, radius);
            // TODO call listener callback
            return;
        } catch (PlaceFetchException e) {
            Log.i("fetchPlaces", "Could not fetch places from the index server", e);
        }

        try {
            sparqlPlaceFetcher.fetchPlaces(view, dataDef, latitude, longitude, radius, listener);
            return;
        } catch (PlaceFetchException e) {
            Log.i("fetchPlaces", "Could not fetch places from the SPARQL endpoint", e);
        } catch (ReservedNameUsedException e) {
            e.printStackTrace();
            Log.e("fetchPlaces", e.getMessage(), e);
        }
    }

    public interface Listener {
        void onPlacesFetched(DataDef fromDataDef, AsyncTaskResult<List<Place>> result);
    }
}
