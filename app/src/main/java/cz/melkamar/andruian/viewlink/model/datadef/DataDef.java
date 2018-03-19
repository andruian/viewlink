/*
 * MIT License
 *
 * Copyright (c) 2018 Martin Melka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package cz.melkamar.andruian.viewlink.model.datadef;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Entity
public class DataDef implements Serializable {
    @PrimaryKey
    @NonNull
    private final String uri;

    @Embedded
    private final LocationClassDef locationClassDef;
    @Embedded
    private final SourceClassDef sourceClassDef;
    @Embedded
    private final IndexServer indexServer;
    private float markerColor;
    private boolean enabled;

    @Ignore
    private final Map<String, String> labels;

    public DataDef(String uri,
                   LocationClassDef locationClassDef,
                   SourceClassDef sourceClassDef,
                   IndexServer indexServer, float markerColor, boolean enabled) {
        this.uri = uri;
        this.locationClassDef = locationClassDef;
        this.sourceClassDef = sourceClassDef;
        this.indexServer = indexServer;
        this.markerColor = markerColor;
        this.enabled = enabled;
        this.labels = new HashMap<>();
    }

    public void addLabel(PrefLabel prefLabel) {
        labels.put(prefLabel.getLanguageTag(), prefLabel.getValue());
    }

    /**
     * Return the best label for the DataDef, in order of precedence:
     * <ol>
     * <li>Label of the given language</li>
     * <li>Label of no language</li>
     * <li>The IRI of the object</li>
     * </ol>
     *
     * @param language The language code, such as "en", "cs".
     */
    public String getLabel(String language) {
        String label = labels.get(language);
        if (label == null) label = labels.get("");
        if (label == null) label = uri;
        return label;
    }

    public String getUri() {
        return uri;
    }

    public LocationClassDef getLocationClassDef() {
        return locationClassDef;
    }

    public SourceClassDef getSourceClassDef() {
        return sourceClassDef;
    }

    public IndexServer getIndexServer() {
        return indexServer;
    }

    public float getMarkerColor() {
        return markerColor;
    }

    public void setMarkerColor(float markerColor) {
        this.markerColor = markerColor % 360;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "DataDef{" +
                "uri='" + uri + '\'' +
                ", locationClassDef=" + locationClassDef +
                ", sourceClassDef=" + sourceClassDef +
                ", indexServer=" + indexServer +
                '}';
    }
}
