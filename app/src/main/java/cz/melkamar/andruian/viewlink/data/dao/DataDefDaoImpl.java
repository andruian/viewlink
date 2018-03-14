package cz.melkamar.andruian.viewlink.data.dao;

import java.util.Collection;
import java.util.List;

import cz.melkamar.andruian.ddfparser.model.DataDef;
import cz.melkamar.andruian.viewlink.exception.NotImplementedException;

public class DataDefDaoImpl implements DataDefDao {
    @Override
    public void saveDataDef(DataDef dataDef) {
        throw new NotImplementedException();
    }

    @Override
    public void saveDataDefs(Collection<DataDef> dataDef) {
        throw new NotImplementedException();
    }

    @Override
    public List<DataDef> loadDataDefs() {
        throw new NotImplementedException();
    }

    @Override
    public DataDef loadDataDef(String uri) {
        throw new NotImplementedException();
    }
}
