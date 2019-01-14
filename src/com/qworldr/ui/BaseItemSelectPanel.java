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
     * 可选面板集合
     */
    private List<T> itemList;

    /**
     * 左边面板
     */
    private JPanel leftPanel;

    /**
     * 右边面板
     */
    private JPanel rightPanel;


    protected BaseItemSelectPanel(@NotNull List<T> itemList) {
        this.itemList = itemList;
    }

    /**
     * 新增元素
     */
    protected abstract void addItem(AnActionButton anActionButton);

    /**
     * 复制元素
     */
    protected abstract void copyItem(AnActionButton anActionButton);

    /**
     * 删除多个元素
     */
    protected abstract void deleteItem(AnActionButton anActionButton);

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
        return this.initToolbar().createPanel();
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
        }).disableDownAction().disableUpAction().addExtraAction(new AnActionButton("Duplicate", PlatformIcons.COPY_ICON) {
            public void actionPerformed(@NotNull AnActionEvent e) {
                if (e == null) {
                    return;
                }
            }

            public void updateButton(AnActionEvent e) {
            }
        }).addExtraAction(new AnActionButton("Restore deleted defaults", AllIcons.General.TodoDefault) {
            public void actionPerformed(@NotNull AnActionEvent e) {
                if (e == null) {
                    return;
                }
                TemplateSettings.getInstance().reset();
            }

            public boolean isEnabled() {
                return super.isEnabled() && !TemplateSettings.getInstance().getDeletedTemplates().isEmpty();
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

                return true;
            }

            @Override
            public boolean canClose(String inputString) {
                return this.checkInput(inputString);
            }
        });
    }


    /**
     * 重置方法
     *
     * @param itemList      元素列表
     * @param selectedIndex 选中的元素下标
     */
    public void reset(@NotNull List<T> itemList, int selectedIndex) {
        this.itemList = itemList;
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
}
