package cz.melkamar.andruian.viewlink.data.persistence;

import android.os.AsyncTask;

import java.util.List;

import cz.melkamar.andruian.viewlink.model.DataDef;
import cz.melkamar.andruian.viewlink.util.AsyncTaskResult;

public class DaoHelper {
    // TODO use this everywhere where DAOs are accessed to avoid clutter with asynctasks

    public static void readAllDatadefs(AppDatabase database, ReadDatadefsTask.Listener callback){
        new ReadDatadefsTask(database, callback).execute();
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
                List<DataDef> result = database.dataDefDao().getAll();
                return new AsyncTaskResult<>(result);
            } catch (Exception e) {
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
