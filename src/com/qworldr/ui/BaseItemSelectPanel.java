package com.qworldr.ui;

import com.intellij.codeInsight.template.impl.TemplateSettings;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionToolbarPosition;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.Splitter;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.AnActionButtonRunnable;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.PlatformIcons;
import com.intellij.util.ui.JBUI;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.util.List;

/**
 * 元素选择面板
 *
 * @author makejava
 * @version 1.0.0
 * @since 2018/08/12 22:26
 */
public abstract class BaseItemSelectPanel<T> {

    private JTree myTree;
    private DefaultMutableTreeNode myTreeRoot = new DefaultMutableTreeNode((Object) null);

    /**
     * 左边面板
     */
    private JPanel leftPanel;

    /**
     * 右边面板
     */
    private JPanel rightPanel;


    protected BaseItemSelectPanel() {
    }

    /**
     * 新增元素
     */
    protected abstract void addItem(AnActionButton anActionButton);


    /**
     * 删除多个元素
     */
    protected abstract void deleteItem(AnActionButton anActionButton);

    /**
     * 重命名
     * @param anActionButton
     */
    protected abstract void renameItem(AnActionButton anActionButton);

    /**
     * 选中元素
     *
     * @param item 元素对象
     */
    protected abstract void selectedItem(T item);

    /**
     * 获取面板
     */
    public JComponent getComponent() {
        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        // 左边的选择列表
        this.leftPanel = createTable();

        // 右边面板
        this.rightPanel = new JPanel(new BorderLayout());

        // 左右分割面板并添加至主面板
        Splitter splitter = new Splitter(false, 0.2F);

        splitter.setFirstComponent(leftPanel);
        splitter.setSecondComponent(rightPanel);

        mainPanel.add(splitter, BorderLayout.CENTER);

        mainPanel.setPreferredSize(JBUI.size(400, 300));


        return mainPanel;
    }

    private JPanel createTable() {
        this.myTreeRoot = new DefaultMutableTreeNode((Object) null);
        this.myTree = new Tree(this.myTreeRoot);
        this.myTree.setRootVisible(false);
        this.myTree.setShowsRootHandles(true);
        this.myTree.addTreeSelectionListener(e->{
            Object[] path = e.getPath().getPath();
            DefaultMutableTreeNode o = (DefaultMutableTreeNode)path[path.length - 1];
            selectedItem( (T)o.getUserObject());
        });
        return this.initToolbar().createPanel();
    }

    public void setRightEditTab(JComponent jComponent){
        this.rightPanel.add(jComponent);
    }
    public void setTreeRender(TreeCellRenderer treeRender) {
        myTree.setCellRenderer(treeRender);
    }

    private ToolbarDecorator initToolbar() {
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(this.myTree).setAddAction(new AnActionButtonRunnable() {
            public void run(AnActionButton button) {
                addItem(button);
            }
        }).setRemoveAction(new AnActionButtonRunnable() {
            public void run(AnActionButton anActionButton) {
                deleteItem(anActionButton);
            }
        }).addExtraAction(new AnActionButton("Rename", AllIcons.General.EditItemInSection) {
            public void actionPerformed(@NotNull AnActionEvent e) {
                if (e == null) {
                    return;
                }
               renameItem(this);
            }

            public boolean isEnabled() {
                return super.isEnabled() && getSelectedItem()!=null;
            }
        });
        return decorator.setToolbarPosition(ActionToolbarPosition.TOP);
    }

    /**
     * 输入元素名称
     *
     * @param initValue 初始值
     * @return 获得的名称，为null表示取消输入
     */
    protected String inputItemName(String initValue) {
        return Messages.showInputDialog("template name", "module template", Messages.getQuestionIcon(), initValue, new InputValidator() {
            @Override
            public boolean checkInput(String inputString) {
                return checkName(inputString);
            }
            @Override
            public boolean canClose(String inputString) {
                return this.checkInput(inputString);
            }
        });
    }
    /**
     * 获取选中元素
     *
     * @return 选中元素
     */
    public T getSelectedItem() {
        DefaultMutableTreeNode lastSelectedPathComponent = (DefaultMutableTreeNode) this.myTree.getLastSelectedPathComponent();
        if(lastSelectedPathComponent==null){
            return null;
        }
        return (T) lastSelectedPathComponent.getUserObject();
    }

    public String getSelectedPath() {
        TreePath selectionPath = this.myTree.getSelectionPath();
        StringBuilder stringBuilder = new StringBuilder();
        for (Object o : selectionPath.getPath()) {
            if (StringUtils.isBlank(o.toString())) {
                continue;
            }
            stringBuilder.append(o).append("/");
        }
        if (stringBuilder.length() > 0) {
            return stringBuilder.substring(0, stringBuilder.length() - 1);
        } else {
            return null;
        }
    }
    public void addSibilingNode(T t){
        DefaultMutableTreeNode lastSelectedPathComponent = (DefaultMutableTreeNode) this.myTree.getLastSelectedPathComponent();
        DefaultMutableTreeNode parent =(DefaultMutableTreeNode) lastSelectedPathComponent.getParent();
        addNode(t,parent);
    }
    public DefaultMutableTreeNode addNode(T t,DefaultMutableTreeNode parent){
        DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(t);
        if (parent == null) {
            this.myTreeRoot.add(newChild);
        } else {
            parent.add(newChild);
        }
        this.myTree.setModel(new DefaultTreeModel(this.myTreeRoot));
        this.myTree.expandPath(this.myTree.getSelectionPath());
        this.myTree.updateUI();
        return newChild;
    }
    public void addChildNode(T t) {
        DefaultMutableTreeNode lastSelectedPathComponent = (DefaultMutableTreeNode) this.myTree.getLastSelectedPathComponent();
        addNode(t,lastSelectedPathComponent);
    }

    public DefaultMutableTreeNode getRoot(){
       return this.myTreeRoot;
    }

    public void removeNode(){
        DefaultMutableTreeNode lastSelectedPathComponent = (DefaultMutableTreeNode) this.myTree.getLastSelectedPathComponent();
        lastSelectedPathComponent.removeFromParent();
        this.myTree.updateUI();
    }

    public void expandTree(){
        expandSubTree(this.myTree.getPathForRow(0));
    }

    private void expandSubTree(TreePath path){
        if(path==null){
            return;
        }
        this.myTree.expandPath(path);
        Object lastPathComponent = path.getLastPathComponent();
        int childCount = this.myTree.getModel().getChildCount(lastPathComponent);
        for (int i = 0; i < childCount; i++) {
            expandSubTree(path.pathByAddingChild(this.myTree.getModel().getChild(lastPathComponent,i)));
        }
    }
    public void updateUI(){
        this.myTree.updateUI();
    }

    public abstract boolean checkName(String name);
}
