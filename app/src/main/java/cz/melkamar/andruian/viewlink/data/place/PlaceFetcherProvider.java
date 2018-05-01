package cz.melkamar.andruian.viewlink.data.place;

import cz.melkamar.andruian.viewlink.data.Factory;

/**
 * A provider class of {@link PlaceFetcher} instances.
 *
 * The default provided class is {@link PlaceFetcher}, but other implementations may be used instead.
 * This is useful for mocking during testing.
 */
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
