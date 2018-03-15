package cz.melkamar.andruian.viewlink.data.persistence;


import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.Random;

import cz.melkamar.andruian.viewlink.exception.PersistenceException;
import cz.melkamar.andruian.viewlink.model.datadef.ClassToLocPath;
import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.model.datadef.IndexServer;
import cz.melkamar.andruian.viewlink.model.datadef.LocationClassDef;
import cz.melkamar.andruian.viewlink.model.datadef.PropertyPath;
import cz.melkamar.andruian.viewlink.model.datadef.SelectProperty;
import cz.melkamar.andruian.viewlink.model.datadef.SourceClassDef;

public class ParserDatadefPersistor {
    /**
     * Persist a DataDef obtained from the parser as a local bunch of objects.
     * <p>
     * The parser class model creates a tree structure, with a DataDef as root.
     * Due to the way Android Room (used for persistence) works, this tree structure needs
     * to be split to several objects that reference each other via foreign keys.
     *
     * @param parserDataDef
     */
    public static DataDef saveParserDatadef(cz.melkamar.andruian.ddfparser.model.DataDef parserDataDef, AppDatabase appDatabase) throws PersistenceException {
        // TODO save labels for datadef
        Log.d("saveParserDatadef", "Saving datadef " + parserDataDef.getUri());

        DataDef dataDef = transDataDefToLocal(parserDataDef);
        try {
            appDatabase.dataDefDao().insertAll(dataDef);
            appDatabase.selectPropertyDao().insertAll(transSelectPropsToLocal(parserDataDef));
            appDatabase.classToLocPathDao().insertAll(transClassToLocPathToLocal(parserDataDef));
        } catch (SQLiteException e){
            Log.e("saveParserDatadef", "An exception occurred.", e);
            throw new PersistenceException(e.getMessage(), e);
        }
        return dataDef;
    }

    public static ClassToLocPath[] transClassToLocPathToLocal(cz.melkamar.andruian.ddfparser.model.DataDef parserDataDef) {
        ClassToLocPath[] result = new ClassToLocPath[parserDataDef.getLocationClassDef().getPathsToGps().size()];
        Log.v("transClassToLocPath", "Will transform " + result.length + " entries");

        int i = 0;
        for (cz.melkamar.andruian.ddfparser.model.ClassToLocPath classToLocPath : parserDataDef.getLocationClassDef().getPathsToGps().values()) {
            result[i++] = new ClassToLocPath(
                    parserDataDef.getUri(),
                    new PropertyPath(classToLocPath.getLatCoord().getPathElements()),
                    new PropertyPath(classToLocPath.getLongCoord().getPathElements()),
                    classToLocPath.getForClassUri()
            );
        }
        return result;
    }

    public static SelectProperty[] transSelectPropsToLocal(cz.melkamar.andruian.ddfparser.model.DataDef parserDataDef) {
        SelectProperty[] result = new SelectProperty[parserDataDef.getSourceClassDef().getSelectProperties().length];
        Log.v("transSelectProps", "Will transform " + result.length + " entries");

        int i = 0;
        for (cz.melkamar.andruian.ddfparser.model.SelectProperty selectProperty : parserDataDef.getSourceClassDef().getSelectProperties()) {
            result[i++] = new SelectProperty(
                    parserDataDef.getUri(),
                    selectProperty.getName(),
                    new PropertyPath(selectProperty.getPath().getPathElements())
            );
        }
        return result;
    }

    private static Random random = new Random();

    /**
     * Construct a DataDef object from the given DataDef returned by a parser.
     * <p>
     * Only construct classes that are connected to the DataDef. Do not process anything else -
     * that has to be done separately by calling the appropriate method.
     *
     * @param parserDataDef
     * @return
     */
    public static DataDef transDataDefToLocal(cz.melkamar.andruian.ddfparser.model.DataDef parserDataDef) {
        SourceClassDef sourceClassDef = new SourceClassDef(
                parserDataDef.getSourceClassDef().getSparqlEndpoint(),
                parserDataDef.getSourceClassDef().getClassUri(),
                new PropertyPath(parserDataDef.getSourceClassDef().getPathToLocationClass().getPathElements())
        );
        Log.v("transDataDefToLocal", "Finished sourceClassDef: " + sourceClassDef);

        LocationClassDef locationClassDef = new LocationClassDef(
                parserDataDef.getLocationClassDef().getSparqlEndpoint(),
                parserDataDef.getLocationClassDef().getClassUri()
        );
        Log.v("transDataDefToLocal", "Finished locationClassDef: " + locationClassDef);


        IndexServer indexServer = null;
        if (parserDataDef.getIndexServer() != null) {
            indexServer = new IndexServer(
                    parserDataDef.getIndexServer().getUri(),
                    parserDataDef.getIndexServer().getVersion(),
                    parserDataDef.getIndexServer().isVersionSet()
            );
        }
        Log.v("transDataDefToLocal", "Finished indexServer: " + indexServer);

        return new DataDef(parserDataDef.getUri(), locationClassDef, sourceClassDef, indexServer, random.nextFloat()*360);
    }
}
