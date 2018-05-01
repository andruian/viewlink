package cz.melkamar.andruian.viewlink.data.persistence;

import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.model.datadef.PrefLabel;

import java.util.List;

public class DaoHelper {
    /**
     * Read all saved DataDef instances including their prefLabels.
     *
     * @param database The database to read from.
     * @return A list of {@link DataDef} objects retrieved from the database.
     */
    public static List<DataDef> readAllDatadefs(AppDatabase database){
        List<DataDef> dataDefs = database.dataDefDao().getAll();
        for (DataDef dataDef : dataDefs) {
            List<PrefLabel> labels = database.prefLabelDao().getAllForDataDefUri(dataDef.getUri());
            for (PrefLabel label : labels) {
                dataDef.addLabel(label);
            }
        }

        return dataDefs;
    }
}
