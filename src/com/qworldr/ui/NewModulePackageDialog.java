package com.qworldr.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

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
    private Project project;
    private List<PsiElement> psiElementList=new ArrayList<>();
    private JTreeWarp jTreeWarp;
    public NewModulePackageDialog(@Nullable Project project, PsiDirectory psiDirectory) {
//        AllIcons.General.Add
        super(project, false);
        setTitle("new module package");
        this.psiDirectory = psiDirectory;
        this.project = project;
        jTreeWarp=new JTreeWarp(templateTree,project);
        addFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode selectedNode = jTreeWarp.getSelectedNode();
                if(selectedNode==null){
                    return;
                }
                if (selectedNode.getAllowsChildren()) {
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode("New");
                    node.setAllowsChildren(false);
                    selectedNode.add(node);
                    jTreeWarp.getjTree().updateUI();
                    node.setUserObject("");
                    jTreeWarp.getjTree().requestFocus();
                    jTreeWarp.getjTree().startEditingAtPath(new TreePath(node.getPath()));
                }
            }
        });
        addDirectoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode selectedNode = jTreeWarp.getSelectedNode();
                if(selectedNode==null){
                    return;
                }
                if (selectedNode.getAllowsChildren()) {
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode("New");
                    node.setAllowsChildren(true);
                    selectedNode.add(node);
                    jTreeWarp.getjTree().updateUI();
                    node.setUserObject("");
                    jTreeWarp.getjTree().requestFocus();
                    jTreeWarp.getjTree().startEditingAtPath(new TreePath(node.getPath()));
                }
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode selectedNode = jTreeWarp.getSelectedNode();
                if(selectedNode==null){
                    return;
                }
                selectedNode.removeAllChildren();
                selectedNode.removeFromParent();
                jTreeWarp.getjTree().updateUI();
            }
        });
        this.init();
    }

    @Override
    protected void doOKAction() {
        DefaultMutableTreeNode root = this.jTreeWarp.getRoot();
        Enumeration children = root.children();
        while (children.hasMoreElements()){
            DefaultMutableTreeNode o = (DefaultMutableTreeNode)children.nextElement();
            Object userObject = o.getUserObject();
            if(o.getAllowsChildren()){
                PsiDirectory subdirectory = this.psiDirectory.createSubdirectory(String.valueOf(userObject));
                psiElementList.add(subdirectory);
            }else{
                PsiClass aClass = JavaDirectoryService.getInstance().createClass(this.psiDirectory, String.valueOf(userObject));
                psiElementList.add(psiDirectory);
            }
        }
        super.doOKAction();
    }


    public List<PsiElement> getPsiElementList(){
        return psiElementList;
    }
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return topPannel;
    }


}
