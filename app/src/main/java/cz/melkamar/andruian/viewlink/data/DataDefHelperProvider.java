package cz.melkamar.andruian.viewlink.data;

/**
 * A provider class of {@link DataDefHelper} instances.
 *
 * The default provided class is {@link DataDefHelperImpl}, but other implementations may be used instead.
 * This is useful for mocking during testing.
 */
public class DataDefHelperProvider extends Factory<DataDefHelper> {
    private static DataDefHelperProvider provider = new DataDefHelperProvider();

    public static DataDefHelperProvider getProvider() {
        return provider;
    }

    @Override
    protected DataDefHelper getDefaultInstance() {
        return new DataDefHelperImpl(NetHelperProvider.getProvider().getInstance());
    }
}
