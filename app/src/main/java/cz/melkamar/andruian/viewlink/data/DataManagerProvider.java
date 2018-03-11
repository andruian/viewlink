package cz.melkamar.andruian.viewlink.data;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public class DataManagerProvider {
    private static DataManager dataManager;

    public static DataManager getDataManager() {
        if (dataManager == null) {
            dataManager = new DataManagerImpl(new NetHelperImpl());
        }
        return dataManager;
    }
}
