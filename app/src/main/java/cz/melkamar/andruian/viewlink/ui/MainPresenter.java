package cz.melkamar.andruian.viewlink.ui;

import android.os.AsyncTask;
import android.util.Log;

import java.util.List;
import java.util.Random;

import cz.melkamar.andruian.viewlink.ViewLinkApplication;
import cz.melkamar.andruian.viewlink.data.persistence.AppDatabase;
import cz.melkamar.andruian.viewlink.data.persistence.ClassToLocPathDao;
import cz.melkamar.andruian.viewlink.data.persistence.DataDefDao;
import cz.melkamar.andruian.viewlink.data.persistence.SelectPropertyDao;
import cz.melkamar.andruian.viewlink.model.ClassToLocPath;
import cz.melkamar.andruian.viewlink.model.DataDef;
import cz.melkamar.andruian.viewlink.model.IndexServer;
import cz.melkamar.andruian.viewlink.model.LocationClassDef;
import cz.melkamar.andruian.viewlink.model.PropertyPath;
import cz.melkamar.andruian.viewlink.model.SelectProperty;
import cz.melkamar.andruian.viewlink.model.SourceClassDef;

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
        IndexServer indexServer = new IndexServer("someidxuri 1", 123, false);
        DataDef ddf = new DataDef("arandomuri" + rnd.nextInt(),
                new LocationClassDef("locsparql" + rnd.nextInt(10), "loccls " + rnd.nextInt(10)),
                new SourceClassDef("locsparql" + rnd.nextInt(10), "srccls " + rnd.nextInt(10), new PropertyPath("a", "b", "c")),
                indexServer);

        AppDatabase db = ((ViewLinkApplication) view.getActivity().getApplication()).getAppDatabase();
        DataDefDao dao = db.dataDefDao();
        SelectPropertyDao selectPropertyDao = db.selectPropertyDao();
        ClassToLocPathDao classToLocPathDao = db.classToLocPathDao();

        new AsyncTask<DataDef, Void, DataDef>() {
            @Override
            protected DataDef doInBackground(DataDef... entities) {
                dao.insertAll(entities[0]);

                for (int i=0; i<4; i++) {
                    SelectProperty selectProperty = new SelectProperty(entities[0].getUri(),
                            "aName" + rnd.nextInt(100),
                            new PropertyPath("x", "y", "z", rnd.nextInt(100) + ""));
                    selectPropertyDao.insertAll(selectProperty);
                    Log.d("insert selectprop", selectProperty.getName());
                }

                for (int i=0; i<4; i++) {
                    ClassToLocPath classToLocPath = new ClassToLocPath(entities[0].getUri(),
                            new PropertyPath("x", "y"+rnd.nextInt(100)),
                            new PropertyPath("x", "y"+rnd.nextInt(100)),
                            "foruri "+rnd.nextInt(2));
                    classToLocPathDao.insertAll(classToLocPath);
                    Log.d("insert clastolocpath", classToLocPath.getForClassUri());
                }

                return entities[0];
            }

            @Override
            protected void onPostExecute(DataDef entity) {
                doneSaved(entity);
            }
        }.execute(ddf);
    }

    public void doneSaved(DataDef singleEntity) {
        Log.d("tgd", "Inserted an entity with id " + singleEntity.getUri());

        AppDatabase db = ((ViewLinkApplication) view.getActivity().getApplication()).getAppDatabase();
        DataDefDao dao = db.dataDefDao();
        SelectPropertyDao selectPropertyDao = db.selectPropertyDao();
        ClassToLocPathDao classToLocPathDao = db.classToLocPathDao();

        StringBuilder builder = new StringBuilder();

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                List<DataDef> result = dao.getAll();
                builder.append("count: ").append(result.size()).append("\n");
                for (DataDef testSingleEntity : result) {
                    builder.append(testSingleEntity.toString()).append("\n");

                    List<SelectProperty> props = selectPropertyDao.getAllForDataDefUri(testSingleEntity.getUri());
                    builder.append("Properties for "+testSingleEntity.getUri()+": "+props.size()).append("\n");
                    for (SelectProperty prop : props) {
                        builder.append("    ").append(prop).append("\n");
                    }

                    List<ClassToLocPath> locs = classToLocPathDao.getAllForDataDefUri(testSingleEntity.getUri());
                    builder.append("Properties for "+testSingleEntity.getUri()+": "+locs.size()).append("\n");
                    for (ClassToLocPath loc : locs) {
                        builder.append("    ").append(loc).append("\n");
                    }

                    locs = classToLocPathDao.getAllForDataDefUriAndClass(testSingleEntity.getUri(), "foruri 1");
                    builder.append("Properties 'foruri 1'"+testSingleEntity.getUri()+": "+locs.size()).append("\n");
                    for (ClassToLocPath loc : locs) {
                        builder.append("    ").append(loc).append("\n");
                    }
                }

                return builder.toString();
            }

            @Override
            protected void onPostExecute(String s) {
                view.showMessage(s);
                Log.d("postexec", s);
            }
        }.execute();

    }
}
