package cz.melkamar.andruian.viewlink.data.place;

import android.util.Log;

import java.io.IOException;
import java.util.List;

import cz.melkamar.andruian.viewlink.exception.PlaceFetchException;
import cz.melkamar.andruian.viewlink.exception.ReservedNameUsedException;
import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.model.place.Place;
import cz.melkamar.andruian.viewlink.ui.base.BaseView;

public class PlaceFetcher {
    private IndexServerPlaceFetcher indexServerPlaceFetcher;
    private SparqlPlaceFetcher sparqlPlaceFetcher;

    public PlaceFetcher(IndexServerPlaceFetcher indexServerPlaceFetcher, SparqlPlaceFetcher sparqlPlaceFetcher) {
        this.indexServerPlaceFetcher = indexServerPlaceFetcher;
        this.sparqlPlaceFetcher = sparqlPlaceFetcher;
    }

    public PlaceFetcher() {
        this.indexServerPlaceFetcher = new IndexServerPlaceFetcher();
        this.sparqlPlaceFetcher = new SparqlPlaceFetcher();
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
    public List<Place> fetchPlaces(BaseView view, DataDef dataDef, double latitude, double longitude, double radius) throws PlaceFetchException {
        Exception lastException = null;
        try {
            return indexServerPlaceFetcher.fetchPlaces(dataDef, latitude, longitude, radius);
        } catch (PlaceFetchException e) {
            lastException = e;
            Log.i("fetchPlaces", "Could not fetch places from the index server", e);
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

        throw new PlaceFetchException("Error while fetching places.", lastException);
    }
}
