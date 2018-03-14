package cz.melkamar.andruian.viewlink.ui;

import android.util.Log;

import org.greenrobot.greendao.query.Query;

import java.util.List;
import java.util.Random;

import cz.melkamar.andruian.viewlink.ViewLinkApplication;
import cz.melkamar.andruian.viewlink.model.DaoSession;
import cz.melkamar.andruian.viewlink.model.TestChildEntity;
import cz.melkamar.andruian.viewlink.model.TestEntity;
import cz.melkamar.andruian.viewlink.model.TestEntityDao;
import cz.melkamar.andruian.viewlink.model.TestSingleEntity;
import cz.melkamar.andruian.viewlink.model.TestSingleEntityDao;

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

    public void testgdao() {
        Random rnd = new Random();
        TestEntity entity = new TestEntity(rnd.nextInt());
        for (int i = 0; i < rnd.nextInt(5) + 2; i++) {
            TestChildEntity childEntity = new TestChildEntity(entity.getId(), "bla " + rnd.nextInt() + " bla");
            entity.getChildren().add(childEntity);
        }

        DaoSession session = ((ViewLinkApplication) view.getActivity().getApplication()).getDaoSession();
        TestEntityDao dao = session.getTestEntityDao();
        dao.insert(entity);

        Log.d("testgdao", dao.count() + "");
        for (TestEntity testEntity : dao.loadAll()) {
            Log.d("testgdao", testEntity.toString());
        }

        view.showMessage(dao.count() + "");
    }

    public void tgd() {
        Random rnd = new Random();
        TestSingleEntity singleEntity = new TestSingleEntity();
        singleEntity.setSomeNumber(rnd.nextInt());
        DaoSession session = ((ViewLinkApplication) view.getActivity().getApplication()).getDaoSession();
        TestSingleEntityDao dao = session.getTestSingleEntityDao();
        dao.insert(singleEntity);
        Log.d("tgd", "Inserted an entity with id " + singleEntity.getId());
        session.clear();

        Query<TestSingleEntity> query = dao.queryBuilder().orderAsc(TestSingleEntityDao.Properties.SomeNumber).build();
        StringBuilder builder = new StringBuilder();

        List<TestSingleEntity> result = query.list();
        builder.append("count: ").append(result.size()).append("\n");
        for (TestSingleEntity testSingleEntity : result) {
            builder.append(testSingleEntity.toString()).append("\n");
        }
        view.showMessage(builder.toString());
    }
}
