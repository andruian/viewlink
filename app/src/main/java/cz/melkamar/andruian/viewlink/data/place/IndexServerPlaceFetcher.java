package cz.melkamar.andruian.viewlink.data.place;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cz.melkamar.andruian.viewlink.Constants;
import cz.melkamar.andruian.viewlink.data.NetHelper;
import cz.melkamar.andruian.viewlink.data.NetHelperProvider;
import cz.melkamar.andruian.viewlink.exception.IndexServerNotDefinedException;
import cz.melkamar.andruian.viewlink.exception.PlaceFetchException;
import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.model.place.MapElement;
import cz.melkamar.andruian.viewlink.model.place.Place;
import cz.melkamar.andruian.viewlink.model.place.PlaceCluster;
import cz.melkamar.andruian.viewlink.model.place.Property;
import cz.melkamar.andruian.viewlink.util.AsyncTaskResult;
import cz.melkamar.andruian.viewlink.util.KeyVal;
import cz.melkamar.andruian.viewlink.util.Util;

public class IndexServerPlaceFetcher {
    public PlaceFetcher.FetchPlacesResult fetchPlaces(DataDef dataDef, double latitude, double longitude, double radius) throws PlaceFetchException, IndexServerNotDefinedException {
        Log.v("IndexServerPlaceFetcher", "fetchPlaces [" + latitude + "," + longitude + "(" + radius + ") for " + dataDef);
        if (dataDef.getIndexServer() == null) {
            throw new IndexServerNotDefinedException("No index server defined");
        }

        String queryUri = dataDef.getIndexServer().getUri() + "/api/query";

        NetHelper netHelper = NetHelperProvider.getProvider().getInstance();
        double kmRadius = Util.convertRadiusToKilometers(latitude, longitude, radius);

        AsyncTaskResult<String> result = netHelper.httpGet(queryUri,
                new KeyVal[]{
                        new KeyVal("lat", latitude + ""),
                        new KeyVal("long", longitude + ""),
                        new KeyVal("r", kmRadius + ""),
                        new KeyVal("type", dataDef.getSourceClassDef().getClassUri() + ""),
                        new KeyVal("clusterLimit", Constants.CLUSTERING_THRESHOLD + ""),
                        new KeyVal("forceCluster", kmRadius > Constants.FORCE_CLUSTER_RADIUS_THRESHOLD ? "true" : "false")
                });

        if (result.hasError()) {
            throw new PlaceFetchException(result.getError().getMessage(), result.getError());
        }

        try {
            return parseResponse(result.getResult(), dataDef);
        } catch (JSONException e) {
            Log.w("fetchPlacesIdx", e.getMessage(), e);
            throw new PlaceFetchException(e.getMessage(), e);
        }
    }

    PlaceFetcher.FetchPlacesResult parseResponse(String json, DataDef parentDataDef) throws JSONException {
        Log.v("parsePlaces", "Raw data: " + json);

        JSONObject responseObj = new JSONObject(json);
        int responseType = responseObj.getInt("responseType");
        JSONArray responseBody = responseObj.getJSONArray("responseBody");

        switch (responseType) {
            case 1:
                return parsePlaces(responseBody, parentDataDef);
            case 2:
                return parseClusters(responseBody, parentDataDef);

            default:
                throw new JSONException("Unknown response type: " + responseType);
        }
    }


    PlaceFetcher.FetchPlacesResult parsePlaces(JSONArray arr, DataDef parentDataDef) throws JSONException {
        List<MapElement> result = new ArrayList<>();

        for (int i = 0; i < arr.length(); i++) {
            JSONObject jsonPlace = arr.getJSONObject(i);
            String label = null;
            try {
                label = jsonPlace.getString("label");
            } catch (JSONException ex) {
                // Label not provided in JSON, just use null
            }
            Place newPlace = new Place(
                    jsonPlace.getString("iri"),
                    jsonPlace.getString("locationObjectIri"),
                    jsonPlace.getDouble("latPos"),
                    jsonPlace.getDouble("longPos"),
                    jsonPlace.getString("type"),
                    parentDataDef,
                    label);

            JSONObject properties = jsonPlace.getJSONObject("properties");
            Iterator<String> keyIter = properties.keys();
            while (keyIter.hasNext()) {
                try {
                    String key = keyIter.next();
                    newPlace.addProperty(new Property(key, properties.getString(key)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            result.add(newPlace);
        }

        return new PlaceFetcher.FetchPlacesResult(PlaceFetcher.FetchPlacesResult.RESULT_TYPE_PLACES, result);
    }

    PlaceFetcher.FetchPlacesResult parseClusters(JSONArray arr, DataDef parentDataDef) throws JSONException {
        List<MapElement> result = new ArrayList<>();

        for (int i = 0; i < arr.length(); i++) {
            JSONObject jsonCluster = arr.getJSONObject(i);
            PlaceCluster placeCluster = new PlaceCluster(
                    jsonCluster.getDouble("latPos"),
                    jsonCluster.getDouble("longPos"),
                    jsonCluster.getInt("placesCount"),
                    parentDataDef);

            result.add(placeCluster);
        }

        return new PlaceFetcher.FetchPlacesResult(PlaceFetcher.FetchPlacesResult.RESULT_TYPE_CLUSTERS, result);
    }
}
