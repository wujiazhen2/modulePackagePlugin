package com.qworldr.utils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * @author wujiazhen
 * @Date 2019/1/15
 */
public class TemplateTreeUtils {

    public static <T> DefaultMutableTreeNode addNode(JTree tree, T t, DefaultMutableTreeNode parent) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
        DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(t);
        if (parent == null) {
            root.add(newChild);
        } else {
            parent.add(newChild);
        }
        tree.setModel(new DefaultTreeModel(root));
        if (parent != null) {
            expandTree(tree);
        }
        tree.updateUI();
        return newChild;
    }

    public static void expandTree(JTree myTree) {
        expandSubTree(myTree, myTree.getPathForRow(0));
    }

    private static void expandSubTree(JTree myTree, TreePath path) {
        if (path == null) {
            return;
        }
        myTree.expandPath(path);
        Object lastPathComponent = path.getLastPathComponent();
        int childCount = myTree.getModel().getChildCount(lastPathComponent);
        for (int i = 0; i < childCount; i++) {
            expandSubTree(myTree, path.pathByAddingChild(myTree.getModel().getChild(lastPathComponent, i)));
        }
    }
}
