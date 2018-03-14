package cz.melkamar.andruian.viewlink.data.dao;

import java.util.Collection;
import java.util.List;

import cz.melkamar.andruian.ddfparser.model.DataDef;

public interface DataDefDao {
    void saveDataDef(DataDef dataDef);
    void saveDataDefs(Collection<DataDef> dataDef);
    List<DataDef> loadDataDefs();
    DataDef loadDataDef(String uri);
}
