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

package cz.melkamar.andruian.viewlink.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

/**
 * 
 */
@Entity(foreignKeys =
@ForeignKey(entity = DataDef.class,
        parentColumns = "uri",
        childColumns = "parentDatadefUri",
        onDelete = ForeignKey.CASCADE)
)
public class ClassToLocPath {
    @PrimaryKey(autoGenerate = true) private int id;
    private final String parentDatadefUri;

    private final PropertyPath latCoord;
    private final PropertyPath longCoord;
    private final String forClassUri;

    public ClassToLocPath(String parentDatadefUri, PropertyPath latCoord, PropertyPath longCoord, String forClassUri) {
        this.parentDatadefUri = parentDatadefUri;
        this.latCoord = latCoord;
        this.longCoord = longCoord;
        this.forClassUri = forClassUri;
    }

    public PropertyPath getLatCoord() {
        return latCoord;
    }

    public PropertyPath getLongCoord() {
        return longCoord;
    }

    public String getForClassUri() {
        return forClassUri;
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
        return "ClassToLocPath{" +
                "id=" + id +
                ", parentDatadefUri='" + parentDatadefUri + '\'' +
                ", latCoord=" + latCoord +
                ", longCoord=" + longCoord +
                ", forClassUri='" + forClassUri + '\'' +
                '}';
    }
}
