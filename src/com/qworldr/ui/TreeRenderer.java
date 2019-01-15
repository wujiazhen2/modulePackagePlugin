package com.qworldr.ui;

import com.intellij.icons.AllIcons;
import com.qworldr.data.NodeType;
import com.qworldr.data.TemplateNode;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * @Author wujiazhen
 * @Date 2018/12/6
 */
public class TreeRenderer extends DefaultTreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        //执行父类原型操作
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
                row, hasFocus);
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        //得到每个节点的TreeNode
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object userObject1 = node.getUserObject();
        if (userObject1 == null) {
            return this;
        }
        TemplateNode userObject = (TemplateNode) userObject1;
        NodeType type = userObject.getType();
        setIcon(type.getIcon());
        return this;
    }
}
