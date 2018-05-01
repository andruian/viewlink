package cz.melkamar.andruian.viewlink.data;

/**
 * A generic class representing a factory of objects. A factory has a default instance which it returns after
 * it is created, but this instance can be replaced through {@link Factory#setInstance(Object)}.
 *
 * @param <T> The type of the objects that will be created by the factory.
 */
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
