package org.bihe.livebin.data.db;

public interface DbResponse<T> {

    void onSuccess(T t);

    void onError(Error error);
}
