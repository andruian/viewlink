package cz.melkamar.andruian.viewlink.data;

/**
 * Created by Martin Melka on 12.03.2018.
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
