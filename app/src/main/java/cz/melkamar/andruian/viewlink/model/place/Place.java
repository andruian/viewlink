package cz.melkamar.andruian.viewlink.model.place;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cz.melkamar.andruian.viewlink.model.datadef.DataDef;

public class Place implements Serializable {
    private final String uri;
    private final String locationObjectUri;
    private final double latitude;
    private final double longitude;
    private final String classType;
    private final DataDef parentDatadef;
    private final List<Property> properties;

    public Place(String uri, String locationObjectUri, double latitude, double longitude, String classType, DataDef parentDatadef) {
        this.uri = uri;
        this.locationObjectUri = locationObjectUri;
        this.latitude = latitude;
        this.longitude = longitude;
        this.classType = classType;
        this.parentDatadef = parentDatadef;
        this.properties = new ArrayList<>();
    }

    public void addProperty(Property property){
        properties.add(property);
    }

    public List<Property> getProperties() {
        return properties;
    }

    public String getUri() {
        return uri;
    }

    public String getLocationObjectUri() {
        return locationObjectUri;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getClassType() {
        return classType;
    }

    public DataDef getParentDatadef() {
        return parentDatadef;
    }

    /**
     * Provide a best possible name for this object to display.
     */
    public String getDisplayName(){
        // TODO preflabel
        return uri;
    }

    @Override
    public String toString() {
        return "Place{" +
                "uri='" + uri + '\'' +
                ", locationObjectUri='" + locationObjectUri + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", classType='" + classType + '\'' +
                ", parentDatadef=" + parentDatadef +
                ", properties=" + properties +
                '}';
    }
}
