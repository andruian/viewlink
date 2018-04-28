package cz.melkamar.andruian.viewlink.ui.srcmgr;


import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.List;

import cz.melkamar.andruian.viewlink.data.persistence.AppDatabase;
import cz.melkamar.andruian.viewlink.data.persistence.DaoHelper;
import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.model.ui.DataDefAdapter;
import cz.melkamar.andruian.viewlink.ui.base.BasePresenterImpl;
import cz.melkamar.andruian.viewlink.util.AsyncTaskResult;


/**
 * Created by Martin Melka on 11.03.2018.
 */

public class DatasourcesPresenterImpl extends BasePresenterImpl implements DatasourcesPresenter {
    private DatasourcesView view;

    public DatasourcesPresenterImpl(DatasourcesView view) {
        super(view);
        this.view = view;
    }

    @Override
    public void refreshDatadefsShown() {
        Log.i("refreshDatadefsShInDr", "Loading new datadefs from the database.");
//        view.showMessage("Loading new datadefs from the database.");
        new ReadDatadefsTask(view).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onAddDatasourceClicked() {
        view.showAddNewResourceActivity();
    }

    @Override
    public void onDeleteDataDefClicked(int position, DataDef dataDef) {
        new DeleteDatadefTask(view, position).execute(dataDef);
    }

    @Override
    public void onDatasourceColorChanged(DataDef dataDef, DataDefAdapter.DataDefViewHolder viewHolder) {
        view.showLoadingDialog("Changing color", "Please wait");
        new ChangeDdfColorTask(dataDef, view.getViewLinkApplication().getAppDatabase(), this, viewHolder).execute();
    }

    static class ChangeDdfColorTask extends AsyncTask<Void, Void, AsyncTaskResult<Object>> {
        private final DataDef dataDef;
        private final AppDatabase appDatabase;
        private final DatasourcesPresenter presenter;
        private final DataDefAdapter.DataDefViewHolder viewHolder;

        ChangeDdfColorTask(DataDef dataDef, AppDatabase appDatabase, DatasourcesPresenter presenter, DataDefAdapter.DataDefViewHolder viewHolder) {
            this.dataDef = dataDef;
            this.appDatabase = appDatabase;
            this.presenter = presenter;
            this.viewHolder = viewHolder;
        }


        @Override
        protected AsyncTaskResult<Object> doInBackground(Void... voids) {
            appDatabase.dataDefDao().update(dataDef);
            return new AsyncTaskResult<>("ok");
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<Object> result) {
            if (result.hasError()){
                Log.e("updateDdf", result.getError().getMessage(), result.getError());
                return;
            }

            Log.d("postColorChange", "Setting color to "+dataDef.getMarkerColor());
            presenter.getBaseView().dismissLoadingDialog();
            viewHolder.setColorPickerColor(dataDef.getMarkerColor());
        }
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
                List<DataDef> result = DaoHelper.readAllDatadefs(view.get().getViewLinkApplication().getAppDatabase());
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
