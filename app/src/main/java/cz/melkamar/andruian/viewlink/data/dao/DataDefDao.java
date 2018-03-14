package cz.melkamar.andruian.viewlink.data.dao;

import android.content.Context;

import java.util.Collection;
import java.util.List;

import cz.melkamar.andruian.ddfparser.model.DataDef;

public interface DataDefDao {
    void saveDataDef(Context context, DataDef dataDef);
    void saveDataDefs(Context context, Collection<DataDef> dataDef);
    List<DataDef> loadDataDefs(Context context);
    DataDef loadDataDef(Context context, String uri);
}
