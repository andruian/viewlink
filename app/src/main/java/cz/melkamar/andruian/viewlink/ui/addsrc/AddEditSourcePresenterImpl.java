package cz.melkamar.andruian.viewlink.ui.addsrc;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cz.melkamar.andruian.viewlink.data.DataDefHelper;
import cz.melkamar.andruian.viewlink.data.DataDefHelperProvider;
import cz.melkamar.andruian.viewlink.data.persistence.ParserDatadefPersistor;
import cz.melkamar.andruian.viewlink.exception.PersistenceException;
import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.ui.base.BasePresenterImpl;
import cz.melkamar.andruian.viewlink.util.AsyncTaskResult;

/**
 * Created by Martin Melka on 12.03.2018.
 */

public class AddEditSourcePresenterImpl extends BasePresenterImpl implements AddEditSourcePresenter {
    AddEditSourceView view;

    public AddEditSourcePresenterImpl(AddEditSourceView view) {
        super(view);
        this.view = view;
    }

    @Override
    public void onConfirmButtonClicked() {
        String uri = view.getSrcUri();

        view.showLoadingDialog("Fetching data source", "Contacting URI: " + uri);
        new FetchDataDefsTask(new WeakReference<>(view), uri).execute();
    }

    /**
     * Asynctask that will fetch a remote RDF file and parse data definitions from it using the ddfparser library.
     */
    private static class FetchDataDefsTask extends AsyncTask<Void, Void, AsyncTaskResult<List<DataDef>>> {
        private WeakReference<AddEditSourceView> view;
        private DataDefHelper dataDefHelper;
        private final String dataDefUrl;

        /**
         * @param view The View corresponding to this presenter.
         * @param dataDefUrl The URL where a file of data definitions can be downloaded.
         */
        public FetchDataDefsTask(WeakReference<AddEditSourceView> view, String dataDefUrl) {
            this.view = view;
            this.dataDefUrl = dataDefUrl;
            this.dataDefHelper = DataDefHelperProvider.getProvider().getInstance();
        }

        @Override
        protected AsyncTaskResult<List<DataDef>> doInBackground(Void... voids) {
            AsyncTaskResult<List<cz.melkamar.andruian.ddfparser.model.DataDef>> result = dataDefHelper.getDataDefs(dataDefUrl);
            if (result.hasError()) {
                return new AsyncTaskResult<>(result.getError());
            }

            List<DataDef> transformedDatadefs = new ArrayList<>(result.getResult().size());
            for (cz.melkamar.andruian.ddfparser.model.DataDef dataDef : result.getResult()) {
                Log.d("onDataDefsFetched", "Saving datadef: " + dataDef.getUri());
                try {
                    transformedDatadefs.add(ParserDatadefPersistor.saveParserDatadef(dataDef, view.get().getViewLinkApplication().getAppDatabase()));
                } catch (PersistenceException e) {
                    Log.i("FetchDataDefsTask", e.getMessage(), e);
                    e.printStackTrace();
                    return new AsyncTaskResult<>(e);
                }
            }
            return new AsyncTaskResult<>(transformedDatadefs);
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<List<DataDef>> result) {
            AddEditSourceView _view = view.get();
            if (_view == null) {
                Log.e("FetchDataDefsTask", "view is null!");
            }

            if (result.hasError()) {
                Log.i("addDatadef", result.getError().getMessage(), result.getError());
                if (_view != null) {
                    _view.dismissLoadingDialog();
                    _view.showError("An error occurred.", result.getError().getMessage());
                }
            } else {
                if (_view != null) {
                    _view.dismissLoadingDialog();
                    _view.returnActivityResult(Activity.RESULT_OK);
                }
            }

        }
    }

}
