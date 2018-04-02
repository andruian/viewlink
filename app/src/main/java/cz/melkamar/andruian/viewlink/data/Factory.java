package cz.melkamar.andruian.viewlink.data;

public abstract class Factory<T> {
    private T instance;

    public T getInstance() {
        if (instance == null) instance = getDefaultInstance();
        return instance;
    }

    public void setInstance(T instance) {
        this.instance = instance;
    }

    public void setDefaultInstance(){
        this.instance = getDefaultInstance();
    }

    protected abstract T getDefaultInstance();
}
