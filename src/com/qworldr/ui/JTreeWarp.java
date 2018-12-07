package com.qworldr.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.ui.tree.TreeModelAdapter;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;

/**
 * @Author wujiazhen
 * @Date 2018/12/7
 */
public class JTreeWarp {
    private JTree jTree;
    private DefaultMutableTreeNode root;
    //定义需要被拖动的TreePath
    private TreePath movePath;
    private Project project;
    public JTreeWarp(JTree jTree, Project project){
        this.jTree=jTree;
        this.project=project;
        this.root = new DefaultMutableTreeNode("template");
        TreeModel treeModel = new DefaultTreeModel(root);
        jTree.setModel(treeModel);
        jTree.setCellRenderer(new TreeRenderer());
        repeatDetectionListener();
        setMovableTree();
        jTree.updateUI();
    }
    public void repeatDetectionListener(){
        jTree.getModel().addTreeModelListener(new TreeModelAdapter() {
            @Override
            public void treeNodesChanged(TreeModelEvent e) {
                TreePath treePath = e.getTreePath();
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
                try {
                    int[] index = e.getChildIndices();
                    node = (DefaultMutableTreeNode) node.getChildAt(index[0]);
                } catch (NullPointerException exc) {
                }
                String name = String.valueOf(node.getUserObject());
                if(StringUtils.isBlank(name)){
                    Messages.showMessageDialog(project, "节点名字不能为null",
                            "非法操作", null);
                    editNode(node);
                }
                Enumeration children = node.getParent().children();
                while (children.hasMoreElements()){
                    DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) children.nextElement();
                    if(!defaultMutableTreeNode.equals(node) && defaultMutableTreeNode.getUserObject().equals(node.getUserObject())){
                        Messages.showMessageDialog(project, "节点名字不能重复",
                                "非法操作", null);
                        editNode(node);
                    }
                }

            }
        });
    }
    public void editNode(DefaultMutableTreeNode node){
        node.setUserObject("");
        getjTree().requestFocus();
        getjTree().startEditingAtPath(new TreePath(node.getPath()));
    }
    public void setMovableTree() {
        jTree.setEditable(true);
        MouseListener ml = new MouseAdapter() {
            //按下鼠标时候获得被拖动的节点
            public void mousePressed(MouseEvent e) {
                //如果需要唯一确定某个节点，必须通过TreePath来获取。
                TreePath tp = jTree.getPathForLocation(e.getX(), e.getY());
                if (tp != null) {
                    movePath = tp;
                }
            }

            //鼠标松开时获得需要拖到哪个父节点
            public void mouseReleased(MouseEvent e) {
                //根据鼠标松开时的TreePath来获取TreePath
                TreePath tp = jTree.getPathForLocation(e.getX(), e.getY());

                if (tp != null && movePath != null) {
                    //阻止向子节点拖动
                    if (movePath.isDescendant(tp) && movePath != tp) {
                        Messages.showMessageDialog(project, "非法移动",
                                "非法操作", null);
                        return;
                    }
                    //既不是向子节点移动，而且鼠标按下、松开的不是同一个节点
                    else if (movePath != tp) {
                        System.out.println(tp.getLastPathComponent());
                        //add方法可以先将原节点从原父节点删除，再添加到新父节点中
                        ((DefaultMutableTreeNode) tp.getLastPathComponent()).add(
                                (DefaultMutableTreeNode) movePath.getLastPathComponent());
                        movePath = null;
                        jTree.updateUI();
                    }
                }
            }
        };
        jTree.addMouseListener(ml);

    }

    public DefaultMutableTreeNode getSelectedNode(){
        TreePath selectionPath = jTree.getSelectionPath();
        if (selectionPath == null) {
            return null;
        }
        return (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
    }
    public JTree getjTree() {
        return jTree;
    }

    public DefaultMutableTreeNode getRoot() {
        return root;
    }

    public Project getProject() {
        return project;
    }
}
