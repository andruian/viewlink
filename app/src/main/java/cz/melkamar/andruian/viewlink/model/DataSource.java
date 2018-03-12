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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataSource that = (DataSource) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        return content != null ? content.equals(that.content) : that.content == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }
}
