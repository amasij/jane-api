package com.jane;

public interface QueryResultTransformer<E, T> {

    T transaform(E e);
}
