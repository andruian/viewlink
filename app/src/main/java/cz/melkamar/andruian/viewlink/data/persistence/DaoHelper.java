package cz.melkamar.andruian.viewlink.data.persistence;

import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;

import java.util.List;

import cz.melkamar.andruian.viewlink.exception.ReservedNameUsedException;
import cz.melkamar.andruian.viewlink.model.datadef.ClassToLocPath;
import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.model.datadef.PrefLabel;
import cz.melkamar.andruian.viewlink.model.datadef.SelectProperty;
import cz.melkamar.andruian.viewlink.util.AsyncTaskResult;

public class DaoHelper {
//    public static void readAllDatadefs(AppDatabase database, ReadDatadefsTask.Listener callback) {
//        new ReadDatadefsTask(database, callback).execute();
//    }

    /**
     * Read all saved DataDef instances including their prefLabels.
     *
     * @param database
     * @return
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

    public static void readForSparqlQuery(AppDatabase database, String dataDefUri, String locClassUri, ReadForSparqlQueryATask.Listener callback) {
        new ReadForSparqlQueryATask(database, callback).execute(dataDefUri, locClassUri);
    }

    public static class ReadForSparqlQueryATask extends AsyncTask<String, Void, AsyncTaskResult<ReadForSparqlQueryATask.ClassToLocAndSelectPropsResult>> {
        final AppDatabase database;
        final Listener callback;

        ReadForSparqlQueryATask(AppDatabase database, Listener callback) {
            this.database = database;
            this.callback = callback;
        }

        @Override
        protected AsyncTaskResult<ClassToLocAndSelectPropsResult> doInBackground(String... strings) {
            try {
                List<ClassToLocPath> classToLocPaths = database.classToLocPathDao().getAllForDataDefUriAndClass(strings[0], strings[1]);
                List<SelectProperty> selectProperties = database.selectPropertyDao().getAllForDataDefUri(strings[0]);
                return new AsyncTaskResult<>(new ClassToLocAndSelectPropsResult(classToLocPaths, selectProperties));
            } catch (Exception e) {
                return new AsyncTaskResult<>(e);
            }
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<ClassToLocAndSelectPropsResult> result) {
            try {
                callback.onResult(result);
            } catch (ReservedNameUsedException e) {
                e.printStackTrace();
            }
        }

        public interface Listener {
            void onResult(AsyncTaskResult<ClassToLocAndSelectPropsResult> result) throws ReservedNameUsedException;
        }

        public static class ClassToLocAndSelectPropsResult {
            final public List<ClassToLocPath> classToLocPaths;
            final public List<SelectProperty> selectProperties;

            public ClassToLocAndSelectPropsResult(List<ClassToLocPath> classToLocPaths, List<SelectProperty> selectProperties) {
                this.classToLocPaths = classToLocPaths;
                this.selectProperties = selectProperties;
            }
        }
    }

    public static class ReadDatadefsTask extends AsyncTask<Void, Void, AsyncTaskResult<List<DataDef>>> {
        final AppDatabase database;
        final Listener callback;

        ReadDatadefsTask(AppDatabase database, Listener callback) {
            this.database = database;
//            this.view = new WeakReference<>(view);
            this.callback = callback;
        }

        @Override
        protected AsyncTaskResult<List<DataDef>> doInBackground(Void... voids) {
            try {
                List<DataDef> result = DaoHelper.readAllDatadefs(database);
                return new AsyncTaskResult<>(result);
            } catch (SQLiteException e) {
                return new AsyncTaskResult<>(e);
            }
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<List<DataDef>> result) {
            callback.onResult(result);
        }

        public interface Listener {
            void onResult(AsyncTaskResult<List<DataDef>> result);
        }
    }
}
