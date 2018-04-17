package cz.melkamar.andruian.viewlink.data.place;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import cz.melkamar.andruian.viewlink.model.place.PlaceCluster;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class IndexServerPlaceFetcherTest {

    IndexServerPlaceFetcher fetcher;

    @Before
    public void setUp() throws Exception {
        fetcher = new IndexServerPlaceFetcher();
    }

    @Test
    public void parsePlacesNoCluster() throws JSONException {
        String json = convertStreamToString(
                IndexServerPlaceFetcherTest.class.getClassLoader()
                        .getResourceAsStream("indexerapi/places-no-clustering.json"));
        PlaceFetcher.FetchPlacesResult result = fetcher.parseResponse(json, null);


        assertEquals(PlaceFetcher.FetchPlacesResult.RESULT_TYPE_PLACES, result.resultType);
        assertEquals(3, result.places.size());
    }

    @Test
    public void parsePlacesWithCluster() throws JSONException {
        String json = convertStreamToString(
                IndexServerPlaceFetcherTest.class.getClassLoader()
                        .getResourceAsStream("indexerapi/places-with-clustering.json"));
        PlaceFetcher.FetchPlacesResult result = fetcher.parseResponse(json, null);


        assertEquals(PlaceFetcher.FetchPlacesResult.RESULT_TYPE_CLUSTERS, result.resultType);
        assertEquals(4, result.places.size());

        int[] clusterSizes = new int[2];
        for (PlaceCluster placeCluster : result.getClusters()) {
            clusterSizes[placeCluster.placesCount - 1]++;
        }
        assertArrayEquals(new int[]{3, 1}, clusterSizes);
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}