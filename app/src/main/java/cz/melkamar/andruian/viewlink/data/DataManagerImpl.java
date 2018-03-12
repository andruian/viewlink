package cz.melkamar.andruian.viewlink.data;

import cz.melkamar.andruian.viewlink.model.DataSource;

import java.util.Random;

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

    @Override
    public DataSource getDataSource(String url) {
        String srcRaw = netHelper.getHttpFile(url);
        // TODO PARSE HERE
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Random random = new Random();
        return new DataSource(random.nextInt()+"", url, srcRaw);
    }
}
