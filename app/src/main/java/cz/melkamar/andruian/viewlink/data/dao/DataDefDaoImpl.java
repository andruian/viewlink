package cz.melkamar.andruian.viewlink.data.dao;

import android.content.Context;

import java.util.Collection;
import java.util.List;

import cz.melkamar.andruian.ddfparser.model.DataDef;
import cz.melkamar.andruian.viewlink.exception.NotImplementedException;

public class DataDefDaoImpl implements DataDefDao {
    private static final String PREFNAME_DATADEFS = "cz.melkamar.andruian.DATADEFS";

    @Override
    public void saveDataDef(Context context, DataDef dataDef) {
        throw new NotImplementedException();
    }

    @Override
    public void saveDataDefs(Context context, Collection<DataDef> dataDef) {
    }

    @Override
    public List<DataDef> loadDataDefs(Context context) {
        throw new NotImplementedException();
    }

    @Override
    public DataDef loadDataDef(Context context, String uri) {
        throw new NotImplementedException();
    }
}
