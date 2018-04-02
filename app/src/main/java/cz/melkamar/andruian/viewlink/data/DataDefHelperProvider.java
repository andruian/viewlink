package cz.melkamar.andruian.viewlink.data;

/**
 * Created by Martin Melka on 11.03.2018.
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
