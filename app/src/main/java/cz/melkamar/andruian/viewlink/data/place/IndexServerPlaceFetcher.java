package cz.melkamar.andruian.viewlink.data.place;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cz.melkamar.andruian.viewlink.data.NetHelper;
import cz.melkamar.andruian.viewlink.data.NetHelperProvider;
import cz.melkamar.andruian.viewlink.exception.PlaceFetchException;
import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.model.place.Place;
import cz.melkamar.andruian.viewlink.model.place.Property;
import cz.melkamar.andruian.viewlink.util.AsyncTaskResult;
import cz.melkamar.andruian.viewlink.util.KeyVal;
import cz.melkamar.andruian.viewlink.util.Util;

public class IndexServerPlaceFetcher {
    public List<Place> fetchPlaces(DataDef dataDef, double latitude, double longitude, double radius) throws PlaceFetchException {
        Log.v("IndexServerPlaceFetcher", "fetchPlaces [" + latitude + "," + longitude + "(" + radius + ") for " + dataDef);
        if (dataDef.getIndexServer() == null) {
            throw new PlaceFetchException("No index server defined");
        }

        String queryUri = dataDef.getIndexServer().getUri() + "/api/query";

        NetHelper netHelper = NetHelperProvider.getNetHelper();
        double kmRadius = Util.convertRadiusToKilometers(latitude, longitude, radius);

        AsyncTaskResult<String> result = netHelper.httpGet(queryUri,
                new KeyVal[]{
                        new KeyVal("lat", latitude + ""),
                        new KeyVal("long", longitude + ""),
                        new KeyVal("r", kmRadius + ""),
                        new KeyVal("type", dataDef.getSourceClassDef().getClassUri() + "")
                });

        if (result.hasError()) {
            throw new PlaceFetchException(result.getError().getMessage(), result.getError());
        }

        try {
            return parsePlaces(result.getResult(), dataDef);
        } catch (JSONException e) {
            Log.w("fetchPlacesIdx", e.getMessage(), e);
            throw new PlaceFetchException(e.getMessage(), e);
        }
    }

    private List<Place> parsePlaces(String json, DataDef parentDataDef) throws JSONException {
        List<Place> result = new ArrayList<>();
        Log.v("parsePlaces", "Raw data: " + json);

        JSONArray arr = new JSONArray(json);
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

        return result;
    }
}
