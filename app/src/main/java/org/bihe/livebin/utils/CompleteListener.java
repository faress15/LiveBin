package org.bihe.livebin.utils;

import androidx.annotation.Nullable;

public interface CompleteListener<T> {

    void onComplete(@Nullable T t);

}

