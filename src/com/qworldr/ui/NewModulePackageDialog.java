package com.qworldr.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.util.ui.tree.TreeModelAdapter;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.*;
import java.io.File;
import java.util.Enumeration;

/**
 * @Author wujiazhen
 * @Date 2018/12/6
 */
public class NewModulePackageDialog extends DialogWrapper {
    private JPanel topPannel;
    private JTextField moduleField;
    private JTree templateTree;
    private JButton addFileButton;
    private JButton addDirectoryButton;
    private JButton deleteButton;
    private JLabel moduleLabel;
    private PsiDirectory psiDirectory;
    private DefaultMutableTreeNode root;
    //定义需要被拖动的TreePath
    private TreePath movePath;
    private Project project;

    public NewModulePackageDialog(@Nullable Project project, PsiDirectory psiDirectory) {
        super(project, false);
        this.psiDirectory = psiDirectory;
        this.project = project;
        Object modulePackage = this.project.getUserData(Key.create("modulePackage"));
        if(modulePackage!=null){

        }
        this.root = new DefaultMutableTreeNode("template");
        TreeModel treeModel = new DefaultTreeModel(root);
        templateTree.setModel(treeModel);
        templateTree.setCellRenderer(new TreeRenderer());
        movableTree();
        templateTree.updateUI();
        addFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreePath selectionPath = templateTree.getSelectionPath();
                if (selectionPath == null) {
                    return;
                }
                DefaultMutableTreeNode lastPathComponent = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
                if (lastPathComponent.getAllowsChildren()) {
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode("New");
                    node.setAllowsChildren(false);
                    lastPathComponent.add(node);
                    templateTree.updateUI();
                    node.setUserObject("");
                    templateTree.requestFocus();
                    templateTree.startEditingAtPath(new TreePath(node.getPath()));
                }
            }
        });
        addDirectoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreePath selectionPath = templateTree.getSelectionPath();
                if (selectionPath == null) {
                    return;
                }
                DefaultMutableTreeNode lastPathComponent = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
                if (lastPathComponent.getAllowsChildren()) {
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode("New");
                    node.setAllowsChildren(true);
                    lastPathComponent.add(node);
                    templateTree.updateUI();
                    node.setUserObject("");
                    templateTree.requestFocus();
                    templateTree.startEditingAtPath(new TreePath(node.getPath()));
                }
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreePath selectionPath = templateTree.getSelectionPath();
                if (selectionPath == null) {
                    return;
                }
                DefaultMutableTreeNode lastPathComponent = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
                lastPathComponent.removeAllChildren();
                lastPathComponent.removeFromParent();
                templateTree.updateUI();
            }
        });
        this.init();
    }

    @Override
    protected void doOKAction() {
        DefaultMutableTreeNode root = this.root;
        Enumeration children = root.children();
        while (children.hasMoreElements()){
            DefaultMutableTreeNode o = (DefaultMutableTreeNode)children.nextElement();
            if(o.getAllowsChildren()){
                Object userObject = o.getUserObject();

            }else{

            }
        }
        super.doOKAction();
    }

    public void movableTree() {
        templateTree.setEditable(true);
        MouseListener ml = new MouseAdapter() {
            //按下鼠标时候获得被拖动的节点
            public void mousePressed(MouseEvent e) {
                //如果需要唯一确定某个节点，必须通过TreePath来获取。
                TreePath tp = templateTree.getPathForLocation(e.getX(), e.getY());
                if (tp != null) {
                    movePath = tp;
                }
            }

            //鼠标松开时获得需要拖到哪个父节点
            public void mouseReleased(MouseEvent e) {
                //根据鼠标松开时的TreePath来获取TreePath
                TreePath tp = templateTree.getPathForLocation(e.getX(), e.getY());

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
                        templateTree.updateUI();
                    }
                }
            }
        };
        templateTree.addMouseListener(ml);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return topPannel;
    }


}
