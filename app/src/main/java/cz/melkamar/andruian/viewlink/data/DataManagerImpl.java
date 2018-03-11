package cz.melkamar.andruian.viewlink.data;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public class DataManagerImpl implements DataManager {
    private final NetHelper netHelper;

    public DataManagerImpl(NetHelper netHelper) {
        this.netHelper = netHelper;
    }


    @Override
    public String getHttpFile(String url) {
        return netHelper.getHttpFile(url);
    }
}
