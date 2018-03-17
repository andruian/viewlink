package cz.melkamar.andruian.viewlink.ui.base;

public abstract class BasePresenterImpl implements BasePresenter {
    protected BaseView baseView;

    public BasePresenterImpl(BaseView baseView) {
        this.baseView = baseView;
    }

    @Override
    public BaseView getBaseView() {
        return this.baseView;
    }
}
