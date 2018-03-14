package cz.melkamar.andruian.viewlink.ui.addsrc;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cz.melkamar.andruian.viewlink.data.DataManager;
import cz.melkamar.andruian.viewlink.data.DataManagerProvider;
import cz.melkamar.andruian.viewlink.data.persistence.ParserDatadefPersistor;
import cz.melkamar.andruian.viewlink.exception.PersistenceException;
import cz.melkamar.andruian.viewlink.model.DataDef;

/**
 * Created by Martin Melka on 12.03.2018.
 */

public class AddEditSourcePresenterImpl implements AddEditSourcePresenter {
    AddEditSourceView view;

    public AddEditSourcePresenterImpl(AddEditSourceView view) {
        this.view = view;
    }

    @Override
    public void onConfirmButtonClicked() {
        String uri = view.getSrcUri();

        view.showLoadingDialog("Fetching data source", "Contacting URI: " + uri);
        DataManagerProvider.getDataManager().getDataDefs(uri, new DataManager.GetDataDefsCallback() {
            @Override
            public void onDataDefsFetched(List<cz.melkamar.andruian.ddfparser.model.DataDef> dataDefs) {
                view.dismissLoadingDialog();
                saveDataDefsAsync(dataDefs);
            }

            @Override
            public void onFetchError(String error, int errorCode) {
                view.dismissLoadingDialog();
                view.showError("An error occurred: " + errorCode, error);
            }
        });
    }

    /**
     * Asynchronously save new DataDefs.
     *
     * @param dataDefs List of {@link DataDef} objects obtained from the {@link cz.melkamar.andruian.ddfparser.DataDefParser}.
     */
    private void saveDataDefsAsync(List<cz.melkamar.andruian.ddfparser.model.DataDef> dataDefs) {
        Log.d("onDataDefsFetched", "Saving " + dataDefs.size() + " entries");

        SaveDatadefsTask task = new SaveDatadefsTask(new WeakReference<>(view));
        task.execute(dataDefs.toArray(new cz.melkamar.andruian.ddfparser.model.DataDef[dataDefs.size()]));
    }

    private static class SaveDatadefsTask extends AsyncTask<cz.melkamar.andruian.ddfparser.model.DataDef, Void, Throwable> {
        private WeakReference<AddEditSourceView> view;

        public SaveDatadefsTask(WeakReference<AddEditSourceView> view) {
            this.view = view;
        }

        @Override
        protected Throwable doInBackground(cz.melkamar.andruian.ddfparser.model.DataDef... dataDefs) {
            List<DataDef> result = new ArrayList<>();
            for (cz.melkamar.andruian.ddfparser.model.DataDef dataDef : dataDefs) {
                Log.d("onDataDefsFetched", "Saving datadef: " + dataDef.getUri());
                try {
                    result.add(ParserDatadefPersistor.saveParserDatadef(dataDef, view.get().getViewLinkApplication().getAppDatabase()));
                } catch (PersistenceException e) {
                    e.printStackTrace();
                    return e;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Throwable error) {
            if (view.get() != null) {
                view.get().dismissLoadingDialog();

                if (error!=null){
                    view.get().showError("An error occurred while saving the Data Definition", error.getMessage());
                } else {
                    // If view/activity still exists, show the new data
                    view.get().returnActivityResult(Activity.RESULT_OK);
                }
            }
        }
    }

}
