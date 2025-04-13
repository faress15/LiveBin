package org.bihe.livebin.utils;

public interface ResultListener<T> {

    void onResult(T t);

    void onError(Throwable throwable);

}

