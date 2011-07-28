package com.razie.pubstage.data;

import java.util.ArrayList;
import java.util.List;


/**
 * a tree structure, with nodes and leafs etc
 * 
 * TODO detailed docs
 * 
 * @author razvanc
 * @param <T>
 */
public interface JStrucTree<T> extends JStructure<T> {
    List<JStrucTree<T>> getChildren();

    boolean isLeaf();

    public static class ImplNode<T> extends JStructure.Impl<T> implements JStrucTree<T> {
        protected List<JStrucTree<T>> children = new ArrayList<JStrucTree<T>>();

        public ImplNode(T contents) {
            super(contents);
        }

        @Override
        public List<JStrucTree<T>> getChildren() {
            return children;
        }

        @Override
        public boolean isLeaf() {
            return false;
        }

        public void addLeaf (T t) {
           this.children.add(new JStrucTree.ImplLeaf<T>(t));
        }

        public JStrucTree<T> addNode (T t) {
           JStrucTree<T> newT = new JStrucTree.ImplNode<T>(t);
           this.children.add(newT);
           return newT;
        }
    }
    
    public static class ImplLeaf<T> extends JStructure.Impl<T> implements JStrucTree<T> {
        public ImplLeaf(T contents) {
            super(contents);
        }

        @Override
        public List<JStrucTree<T>> getChildren() {
            return null;
        }

        @Override
        public boolean isLeaf() {
            return true;
        }
    }
}
