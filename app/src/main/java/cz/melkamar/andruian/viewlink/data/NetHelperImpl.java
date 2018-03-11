package cz.melkamar.andruian.viewlink.data;

import android.util.Log;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public class NetHelperImpl implements NetHelper {
    @Override
    public String getHttpFile(String url) {
        Log.i("getHttpFile", url);
        return "foobar";
    }
}
