package cz.melkamar.andruian.viewlink.data;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public class DataDefHelperProvider {
    private static DataDefHelper dataDefHelper;

    public static DataDefHelper getDataDefHelper() {
        if (dataDefHelper == null) {
            dataDefHelper = new DataDefHelperImpl(
                    NetHelperProvider.getNetHelper());
            // TODO refactor this, allow mocking - use dagger?
        }
        return dataDefHelper;
    }


    /**
     * Used for mocking in tests.
     */
    public static void setDataDefHelper(DataDefHelper dataDefHelper){
        DataDefHelperProvider.dataDefHelper = dataDefHelper;
    }
}
