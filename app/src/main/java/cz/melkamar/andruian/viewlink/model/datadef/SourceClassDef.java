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

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;

public class SourceClassDef{

    @ColumnInfo(name = "sourcedef_sparqlendpoint")
    protected final String sparqlEndpoint;
    @ColumnInfo(name = "sourcedef_classuri")
    protected final String classUri;
    @Embedded
    private final PropertyPath pathToLocationClass;
//    private final SelectProperty[] selectProperties;

    public SourceClassDef(String sparqlEndpoint,
                          String classUri,
                          PropertyPath pathToLocationClass
//                          SelectProperty[] selectProperties
    ) {
        this.sparqlEndpoint = sparqlEndpoint;
        this.classUri = classUri;
        this.pathToLocationClass = pathToLocationClass;
//        this.selectProperties = selectProperties;
    }

    public String getSparqlEndpoint() {
        return sparqlEndpoint;
    }

    public String getClassUri() {
        return classUri;
    }

    public PropertyPath getPathToLocationClass() {
        return pathToLocationClass;
    }

//    public SelectProperty[] getSelectProperties() {
//        return selectProperties;
//    }

//    public String[] getSelectPropertiesNames() {
//        return Arrays.stream(selectProperties).map(SelectProperty::getName).toArray(String[]::new);
//    }


    @Override
    public String toString() {
        return "SourceClassDef{" +
                "sparqlEndpoint='" + sparqlEndpoint + '\'' +
                ", classUri='" + classUri + '\'' +
                ", pathToLocationClass=" + pathToLocationClass +
                '}';
    }
}
