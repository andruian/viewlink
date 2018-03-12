package cz.melkamar.andruian.viewlink.data;

/**
 * Created by Martin Melka on 12.03.2018.
 */

public class NetHelperProvider {
    private static NetHelper netHelper;

    public static NetHelper getNetHelper() {
        if (netHelper == null) {
            netHelper = new NetHelperImpl();
        }
        return netHelper;
    }

    /**
     * Used for mocking in tests.
     * @param netHelper
     */
    public static void setNetHelper(NetHelper netHelper){
        NetHelperProvider.netHelper = netHelper;
    }
}
