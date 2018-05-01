package cz.melkamar.andruian.viewlink.data;

/**
 * A provider class of {@link NetHelper} instances.
 *
 * The default provided class is {@link NetHelperImpl}, but other implementations may be used instead.
 * This is useful for mocking during testing.
 */
public class NetHelperProvider extends Factory<NetHelper> {
    private static NetHelperProvider provider = new NetHelperProvider();

    public static NetHelperProvider getProvider() {
        return provider;
    }

    @Override
    protected NetHelper getDefaultInstance() {
        return new NetHelperImpl();
    }
}
