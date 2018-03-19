package cz.melkamar.andruian.viewlink.model.datadef;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(
        entity = DataDef.class,
        parentColumns = "uri",
        childColumns = "parentDatadefUri",
        onDelete = ForeignKey.CASCADE
))
public class PrefLabel {
    @PrimaryKey(autoGenerate = true) private int id;
    private final String parentDatadefUri;

    private final String languageTag;
    private final String value;

    public PrefLabel(String parentDatadefUri, String languageTag, String value) {
        this.parentDatadefUri = parentDatadefUri;
        this.languageTag = languageTag;
        this.value = value;
    }

    public String getParentDatadefUri() {
        return parentDatadefUri;
    }

    public String getLanguageTag() {
        return languageTag;
    }

    public String getValue() {
        return value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
