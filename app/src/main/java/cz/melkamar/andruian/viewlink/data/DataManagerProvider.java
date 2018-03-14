package cz.melkamar.andruian.viewlink.data;

import cz.melkamar.andruian.viewlink.data.dao.DataDefDaoImpl;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public class DataManagerProvider {
    private static DataManager dataManager;

    public static DataManager getDataManager() {
        if (dataManager == null) {
            dataManager = new DataManagerImpl(
                    NetHelperProvider.getNetHelper(),
                    new DataDefDaoImpl());
            // TODO refactor this, allow mocking - use dagger?
        }
        return dataManager;
    }


    /**
     * Used for mocking in tests.
     */
    public static void setDataManager(DataManager dataManager){
        DataManagerProvider.dataManager = dataManager;
    }
}
