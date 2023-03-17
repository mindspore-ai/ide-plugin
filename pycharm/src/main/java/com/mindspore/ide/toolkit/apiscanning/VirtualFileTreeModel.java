package com.mindspore.ide.toolkit.apiscanning;

import com.intellij.openapi.diagnostic.Logger;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class VirtualFileTreeModel implements TreeModel {
    private static final Logger log = Logger.getInstance(VirtualFileTreeModel.class);
    private VirtualFileNode rootVirtualFileNode;
    protected EventListenerList listenerList = new EventListenerList();

    public VirtualFileTreeModel(VirtualFileNode root) {
        rootVirtualFileNode = root;
    }

    @Override
    public Object getRoot() {
        return rootVirtualFileNode;
    }

    @Override
    public Object getChild(Object parent, int index) {
        VirtualFileNode root = (VirtualFileNode) parent;
        return root.getChildAt(index);
    }

    @Override
    public int getChildCount(Object parent) {
        VirtualFileNode root = (VirtualFileNode) parent;
        return root.getChildCount();
    }

    @Override
    public boolean isLeaf(Object node) {
        VirtualFileNode root = (VirtualFileNode) node;
        return root.getChildCount() == 0;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        log.info("*** valueForPathChanged : " + path + " --> " + newValue);
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if (parent == null || child == null) {
            return -1;
        }
        return ((VirtualFileNode) parent).getIndex((VirtualFileNode) child);
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add(TreeModelListener.class, l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        listenerList.remove(TreeModelListener.class, l);
    }
}
