package com.mindspore.ide.toolkit.apiscanning;

import com.intellij.openapi.vfs.VirtualFile;

import java.util.Vector;

public class VirtualFileNode {

    public VirtualFile virtualFile;

    public VirtualFileNode parent;

    public Vector<VirtualFileNode> children;

    public boolean markedPy;

    public VirtualFileNode(VirtualFile virtualFile) {
        parent= null;
        markedPy = false;
        children = new Vector<>();
        this.virtualFile = virtualFile;
    }

    public void insert(VirtualFileNode newChild, int childIndex) {
        if (newChild == null) {
            throw new IllegalArgumentException("new child is null");
        }
        VirtualFileNode oldParent = newChild.getParent();

        if (oldParent != null) {
            oldParent.remove(newChild);
        }
        newChild.setParent(this);
        if (children == null) {
            children = new Vector<>();
        }
        children.insertElementAt(newChild, childIndex);
    }

    public void remove(VirtualFileNode aChild) {
        if (aChild == null) {
            throw new IllegalArgumentException("argument is null");
        }
        remove(getIndex(aChild));
    }

    public void remove(int childIndex) {
        VirtualFileNode child = getChildAt(childIndex);
        children.removeElementAt(childIndex);
    }

    public VirtualFileNode getChildAt(int index) {
        if (children == null) {
            throw new ArrayIndexOutOfBoundsException("node has no children");
        }
        return children.elementAt(index);
    }

    public int getIndex(VirtualFileNode aChild) {
        if (aChild == null) {
            throw new IllegalArgumentException("argument is null");
        }
        return children.indexOf(aChild);
    }

    public void add(VirtualFileNode newChild) {
        if (newChild != null) {
            insert(newChild, getChildCount());
        }
    }

    public int getChildCount() {
        if (children == null) {
            return 0;
        } else {
            return children.size();
        }
    }

    public String toString() {
        return virtualFile.getName();
    }
    public VirtualFile getVirtualFile() {
        return virtualFile;
    }

    public void setVirtualFile(VirtualFile virtualFile) {
        this.virtualFile = virtualFile;
    }

    public VirtualFileNode getParent() {
        return parent;
    }

    public void setParent(VirtualFileNode parent) {
        this.parent = parent;
    }

    public Vector<VirtualFileNode> getChildren() {
        return children;
    }

    public void setChildren(Vector<VirtualFileNode> children) {
        this.children = children;
    }

    public boolean isMarkedPy() {
        return markedPy;
    }

    public void setMarkedPy(boolean markedPy) {
        this.markedPy = markedPy;
    }
}
