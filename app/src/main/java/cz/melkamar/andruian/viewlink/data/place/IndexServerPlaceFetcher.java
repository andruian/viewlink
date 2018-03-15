package cz.melkamar.andruian.viewlink.data.place;

import java.util.List;

import cz.melkamar.andruian.viewlink.exception.PlaceFetchException;
import cz.melkamar.andruian.viewlink.model.Place;
import cz.melkamar.andruian.viewlink.model.datadef.DataDef;

public class IndexServerPlaceFetcher {
    public List<Place> fetchPlaces(DataDef dataDef, double latitude, double longitude, double radius) throws PlaceFetchException{
        throw new PlaceFetchException(); // TODO implement
    }
}
