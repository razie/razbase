package com.razie.pubstage.data;

/**
 * this represents a structure (like a tree or a graph) imposed on some contents
 * 
 * @author razvanc
 * @param <T>
 */
public interface JStructure<T> {
    T getContents();

    void setContents(T t);

    public static class Impl<T> implements JStructure<T> {
        protected T contents;

        public Impl(T contents) {
            this.contents = contents;
        }

        public T getContents() {
            return contents;
        }

        public void setContents(T t) {
            contents = t;
        }
    }
}
