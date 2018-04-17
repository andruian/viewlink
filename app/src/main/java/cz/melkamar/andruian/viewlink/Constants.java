package cz.melkamar.andruian.viewlink;

public class Constants {
    /**
     * Allow up to this number of markers to be shown without server-side clustering.
     *
     * This value effectively corresponds to an index server's /api/query?clusterLimit parameter.
     */
    public static final int CLUSTERING_THRESHOLD = 1000;
}
