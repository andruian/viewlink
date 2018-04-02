package cz.melkamar.andruian.viewlink.data.place;

import cz.melkamar.andruian.viewlink.data.Factory;

public class PlaceFetcherProvider extends Factory<PlaceFetcher> {
    private static PlaceFetcherProvider provider = new PlaceFetcherProvider();

    public static PlaceFetcherProvider getProvider() {
        return provider;
    }

    @Override
    protected PlaceFetcher getDefaultInstance() {
        return new PlaceFetcher(new IndexServerPlaceFetcher(), new SparqlPlaceFetcher());
    }
}
