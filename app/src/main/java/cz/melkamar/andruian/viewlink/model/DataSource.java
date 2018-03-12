package cz.melkamar.andruian.viewlink.model;

import java.io.Serializable;

/**
 * Created by Martin Melka on 12.03.2018.
 */

public class DataSource implements Serializable {
    private String name;
    private String url; // TODO this will link to datadef
    private String content;

    public DataSource(String name, String url, String content) {
        this.name = name;
        this.url = url;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getContent() {
        return content;
    }
}
