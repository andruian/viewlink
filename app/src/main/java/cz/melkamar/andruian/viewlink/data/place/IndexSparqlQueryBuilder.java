package cz.melkamar.andruian.viewlink.data.place;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cz.melkamar.andruian.viewlink.exception.ReservedNameUsedException;
import cz.melkamar.andruian.viewlink.model.datadef.PropertyPath;
import cz.melkamar.andruian.viewlink.model.datadef.SelectProperty;
import cz.melkamar.andruian.viewlink.util.MapFormat;

public class IndexSparqlQueryBuilder {
    public static final Set<String> RESERVED_VAR_NAMES = new HashSet<>(Arrays.asList("dataObj",
            "locationObj",
            "lat",
            "long"));

    public IndexSparqlQueryBuilder(String queryTemplate, String dataClassUri,
                                   PropertyPath dataToLocationClassPropPath,
                                   String locationSparqlEndpoint,
                                   PropertyPath locClassToLatPropPath,
                                   PropertyPath locClassToLongPropPath) {
        this.queryTemplate = queryTemplate;
        this.dataClassUri = dataClassUri;
        this.dataToLocationClassPropPath = dataToLocationClassPropPath;
        this.locationSparqlEndpoint = locationSparqlEndpoint;
        this.locClassToLatPropPath = locClassToLatPropPath;
        this.locClassToLongPropPath = locClassToLongPropPath;
        this.selectProperties = new ArrayList<>();
        this.excludeDataObjUris = new ArrayList<>();
    }

    private final String queryTemplate;

    // Mandatory
    private final String dataClassUri;
    private final PropertyPath dataToLocationClassPropPath;
    private final String locationSparqlEndpoint;
    private final PropertyPath locClassToLatPropPath;
    private final PropertyPath locClassToLongPropPath;

    // Optional
    private List<SelectProperty> selectProperties;
    private List<String> excludeDataObjUris;

    public void addSelectProperty(SelectProperty selectProperty) {
        selectProperties.add(selectProperty);
    }

    public void excludeUri(String excludeUri) {
        excludeDataObjUris.add(excludeUri);
    }

    public String build() throws ReservedNameUsedException {
        Log.d("IndexSparqlQueryBuilder", "Building an index SPARQL query from template");
        Log.v("SparqlQuery", queryTemplate);


        Map<String, String> argMap = new HashMap<>();
        argMap.put("dataClassUri", dataClassUri);
        argMap.put("pathToLocClass", dataToLocationClassPropPath.toString());
        argMap.put("locationSparqlEndpoint", locationSparqlEndpoint);
        argMap.put("latLocationPathForLocationClass", locClassToLatPropPath.toString());
        argMap.put("longLocationPathForLocationClass", locClassToLongPropPath.toString());
        argMap.put("selectProps", buildSelectProps(selectProperties));
        argMap.put("selectPropsMapping", buildSelectPropsMapping(selectProperties));
        argMap.put("excludeDataObjects", buildExcludeDataObjectsExpr(excludeDataObjUris));

        String result = MapFormat.format(queryTemplate, argMap);
        Log.v("SparqlQuery resolved", result);
        return result;
    }

    /**
     * Build string of selected property names used in the SELECT section of the query.
     * Example, SelectProperty objects with names "a", "xyz", "propname" will be transformed into:
     * "?a ?xyz ?propname ".
     *
     * @param selectProperties List of SelectProperty objects which will be converted into a list of prop names.
     */
    private String buildSelectProps(List<SelectProperty> selectProperties) throws ReservedNameUsedException {
        StringBuilder builder = new StringBuilder();
        for (SelectProperty selectProperty : selectProperties) {
            checkReservedVariableUsed(selectProperty.getName());

            builder.append("?").append(selectProperty.getName()).append(" ");
        }
        return builder.toString();
    }

    /**
     * Build string of selected property mappings used in the body of the query.
     * <p>
     * Example, SelectProperty objects
     * <p>
     * (a | ex:a)
     * <p>
     * (xyz | ex:x/ex:y/ex:z)
     * <p>
     * Will be transformed into:
     * <p>
     * ?dataObj ex:a ?a .
     * <p>
     * ?dataObj ex:x/ex:y/ex:z ?xyz .
     *
     * @param selectProperties List of SelectProperty objects which will be converted into a mapping.
     */
    private String buildSelectPropsMapping(List<SelectProperty> selectProperties) throws ReservedNameUsedException {
        StringBuilder builder = new StringBuilder();
        for (SelectProperty selectProperty : selectProperties) {
            checkReservedVariableUsed(selectProperty.getName());

            builder.append("?dataObj ")
                    .append(selectProperty.getPath().toString())
                    .append(" ?")
                    .append(selectProperty.getName())
                    .append(" .\n");
        }
        return builder.toString();
    }

    /**
     * Build filter expression for data objects that should be discarded from the query.
     * <p>
     * Example, to discard two objects, the following will be generated:
     * <pre>
     * {@code ?dataObj != <http://example.org/linkedobject-24481611> &&
     *   ?dataObj != <http://example.org/linkedobject-72715057> &&
     * }
     * </pre>
     *
     * @param excludeDataObjUris List of URIs as strings that will be filtered out of the query. E.g. "http://ex.org".
     *                           NOT &lt;http://ex.org&gt;.
     */
    private String buildExcludeDataObjectsExpr(List<String> excludeDataObjUris) {
        StringBuilder builder = new StringBuilder();
        for (String excludeUri : excludeDataObjUris) {
            builder.append("?dataObj != <").append(excludeUri).append("> &&\n");
        }
        return builder.toString();
    }

    private void checkReservedVariableUsed(String varName) throws ReservedNameUsedException {
        if (RESERVED_VAR_NAMES.contains(varName)) {
            throw new ReservedNameUsedException("This variable name is reserved and cannot be used: " + varName);
        }
    }
}
