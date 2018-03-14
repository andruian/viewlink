package cz.melkamar.andruian.viewlink.ui;

import android.os.AsyncTask;
import android.util.Log;

import java.util.List;
import java.util.Random;

import cz.melkamar.andruian.viewlink.ViewLinkApplication;
import cz.melkamar.andruian.viewlink.model.AppDatabase;
import cz.melkamar.andruian.viewlink.model.DataDef;
import cz.melkamar.andruian.viewlink.model.DataDefDao;
import cz.melkamar.andruian.viewlink.model.IndexServer;

/**
 * Created by Martin Melka on 11.03.2018.
 */

public class MainPresenter implements MainMvpPresenter {
    private MainMvpView view;

    public MainPresenter(MainMvpView view) {
        this.view = view;
    }

    @Override
    public void manageDataSources() {
        Log.i("manageDataSources", "foo");
//        view.showMessage("add datasource: "+ DataManagerProvider.getDataManager().getHttpFile("someUrl"));
        view.showManageDatasources();
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {
        view = null;
    }

    @Override
    public void onFabClicked() {
//        view.setKeepMapCentered(true); // TODO enable this
//        testgdao();
        tgd();
//        DataManagerProvider.getDataManager().getDataDefs("https://raw.githubusercontent.com/andruian/datadef-parser/master/src/test/resources/rdf/test-parse-datadef.ttl", new DataManager.GetDataDefsCallback() {
//                    @Override
//                    public void onDataDefsFetched(List<DataDef> dataDefs) {
//                        Log.i("onDataDefsFetched", "Fetched datadefs: "+dataDefs.size());
//                        for (DataDef dataDef : dataDefs) {
//                            Log.d("ondataDefsFetched", dataDef.getUri());
//                        }
//                    }
//
//                    @Override
//                    public void onFetchError(String error, int errorCode) {
//                        // TODO handle error
//                    }
//                }
//        );
    }

    public void tgd() {
        Random rnd = new Random();
        IndexServer indexServer = new IndexServer("someidxuri 1");
        DataDef ddf = new DataDef("arandomuri"+rnd.nextInt(), null, null, indexServer);

        AppDatabase db = ((ViewLinkApplication) view.getActivity().getApplication()).getAppDatabase();
        DataDefDao dao = db.dataDefDao();

        new AsyncTask<DataDef, Void, DataDef>() {
            @Override
            protected DataDef doInBackground(DataDef... entities) {
                dao.insertAll(entities[0]);
                return entities[0];
            }

            @Override
            protected void onPostExecute(DataDef entity) {
                doneSaved(entity);
            }
        }.execute(ddf);
    }

    public void doneSaved(DataDef singleEntity){
        Log.d("tgd", "Inserted an entity with id " + singleEntity.getUri());

        AppDatabase db = ((ViewLinkApplication) view.getActivity().getApplication()).getAppDatabase();
        DataDefDao dao = db.dataDefDao();

        StringBuilder builder = new StringBuilder();

        new AsyncTask<Void, Void, String>(){
            @Override
            protected String doInBackground(Void... voids) {
                List<DataDef> result = dao.getAll();
                builder.append("count: ").append(result.size()).append("\n");
                for (DataDef testSingleEntity : result) {
                    builder.append(testSingleEntity.toString()).append("\n");
                }

                return builder.toString();
            }

            @Override
            protected void onPostExecute(String s) {
                view.showMessage(s);
            }
        }.execute();

    }
}
