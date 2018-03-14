package cz.melkamar.andruian.viewlink.ui.srcmgr;


import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.List;

import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.util.AsyncTaskResult;


/**
 * Created by Martin Melka on 11.03.2018.
 */

public class DatasourcesPresenterImpl implements DatasourcesPresenter {
    private DatasourcesView view;

    public DatasourcesPresenterImpl(DatasourcesView view) {
        this.view = view;
    }

    @Override
    public void refreshDatadefsShown() {
        // TODO this should get all datadefs from database
        Log.i("refreshDatadefsShown", "Loading new datadefs from the database.");
//        view.showMessage("Loading new datadefs from the database.");
        new ReadDatadefsTask(view).execute();
    }

    @Override
    public void onAddDatasourceClicked() {
        view.showAddNewResourceActivity();
    }

    @Override
    public void onDeleteDataDefClicked(int position, DataDef dataDef) {
        new DeleteDatadefTask(view, position).execute(dataDef);
    }

    static class DeleteDatadefTask extends AsyncTask<DataDef, Void, AsyncTaskResult<Object>> {
        final WeakReference<DatasourcesView> view;
        final int deletedPosition;

        DeleteDatadefTask(DatasourcesView view, int deletedPosition) {
            this.view = new WeakReference<>(view);
            this.deletedPosition = deletedPosition;
        }

        @Override
        protected AsyncTaskResult<Object> doInBackground(DataDef... dataDefs) {
            try {
                Log.d("DeleteDatadefTask", "Deleting " + dataDefs[0]);
                view.get().getViewLinkApplication().getAppDatabase().dataDefDao().delete(dataDefs[0]);
                return new AsyncTaskResult<>(1);
            } catch (Exception e) {
                Log.d("DeleteDatadefTask", "Failed deleting " + dataDefs[0], e);
                return new AsyncTaskResult<>(e);
            }
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<Object> result) {
            if (result.hasError()) {
                // TODO handle error
            } else {
                DatasourcesView _view = view.get();
                if (_view != null) {
                    _view.deleteFromRecycler(deletedPosition);
                }
            }
        }
    }

    static class ReadDatadefsTask extends AsyncTask<Void, Void, AsyncTaskResult<List<DataDef>>> {
        final WeakReference<DatasourcesView> view;

        ReadDatadefsTask(DatasourcesView view) {
            this.view = new WeakReference<>(view);
        }

        @Override
        protected AsyncTaskResult<List<DataDef>> doInBackground(Void... voids) {
            try {
                List<DataDef> result = view.get().getViewLinkApplication().getAppDatabase().dataDefDao().getAll();
                return new AsyncTaskResult<>(result);
            } catch (Exception e) {
                return new AsyncTaskResult<>(e);
            }
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<List<DataDef>> result) {
            if (result.hasError()) {
                // TODO handle error
            } else {
                DatasourcesView _view = view.get();
                if (_view != null) {
                    _view.showDataDefs(result.getResult());
                }
            }
        }
    }
}
