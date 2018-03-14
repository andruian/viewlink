package cz.melkamar.andruian.viewlink.ui;

import java.util.List;

import cz.melkamar.andruian.viewlink.model.DataDef;
import cz.melkamar.andruian.viewlink.ui.base.BaseView;

/**
 * Created by Martin on 11.03.2018.
 */

public interface MainMvpView extends BaseView {
    void showManageDatasourcesActivity();
    void setKeepMapCentered(boolean keepCentered);
    void showDataDefsInDrawer(List<DataDef> dataDefList);
}
