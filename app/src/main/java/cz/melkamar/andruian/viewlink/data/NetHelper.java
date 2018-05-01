package cz.melkamar.andruian.viewlink.data;

import cz.melkamar.andruian.viewlink.util.AsyncTaskResult;
import cz.melkamar.andruian.viewlink.util.KeyVal;

/**
 * An interface for functionality related to network communication.
 */
public interface NetHelper {
    /**
     * Perform a HTTP GET request and return the result wrapped in an {@link AsyncTaskResult} so that it may
     * be easily consumed when running from inside an asynctask.
     */
    AsyncTaskResult<String> httpGet(String url, KeyVal[] data, KeyVal... headers);

    /**
     * Perform a HTTP POST request and return the result wrapped in an {@link AsyncTaskResult} so that it may
     * be easily consumed when running from inside an asynctask.
     */
    AsyncTaskResult<String> httpPost(String url, KeyVal[] data, KeyVal... headers);
}
