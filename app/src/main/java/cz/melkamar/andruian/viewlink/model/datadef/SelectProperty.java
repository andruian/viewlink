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

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(foreignKeys =
@ForeignKey(entity = DataDef.class,
        parentColumns = "uri",
        childColumns = "parentDatadefUri",
        onDelete = ForeignKey.CASCADE)
)
public class SelectProperty {
    @PrimaryKey(autoGenerate = true) private int id;
    private final String parentDatadefUri;
    private final String name;
    private final PropertyPath path;

    public SelectProperty(String parentDatadefUri, String name, PropertyPath path) {
        this.parentDatadefUri = parentDatadefUri;
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public PropertyPath getPath() {
        return path;
    }

    public int getId() {
        return id;
    }

    public String getParentDatadefUri() {
        return parentDatadefUri;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "SelectProperty{" +
                "id=" + id +
                ", parentDatadefUri='" + parentDatadefUri + '\'' +
                ", name='" + name + '\'' +
                ", path=" + path +
                '}';
    }
}
