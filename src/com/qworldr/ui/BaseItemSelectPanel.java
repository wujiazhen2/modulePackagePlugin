package com.qworldr.ui;

import com.intellij.codeInsight.template.impl.TemplateSettings;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.MessageDialogBuilder;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.Splitter;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.AnActionButtonRunnable;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.border.CustomLineBorder;
import com.intellij.ui.components.JBList;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.PlatformIcons;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    /**
     * 列表面板
     */
    private JBList<String> listPanel;

    protected BaseItemSelectPanel(@NotNull List<T> itemList) {
        this.itemList = itemList;
    }

    /**
     * 新增元素
     *
     * @param name 元素名称
     */
    protected abstract void addItem(String name);

    /**
     * 复制元素
     *
     * @param newName 新名称
     * @param item    元素对象
     */
    protected abstract void copyItem(String newName, T item);

    /**
     * 删除多个元素
     *
     * @param item 元素对象
     */
    protected abstract void deleteItem(T item);

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

        // 存在元素时，默认选中第一个元素
        if (!itemList.isEmpty()) {
            listPanel.setSelectedIndex(0);
        }

        return mainPanel;
    }

    private JPanel createTable() {
        this.myTreeRoot = new DefaultMutableTreeNode((Object) null);
        this.myTree = new Tree(this.myTreeRoot);
        return this.initToolbar().createPanel();
    }

    private ToolbarDecorator initToolbar() {
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(this.myTree).setAddAction(new AnActionButtonRunnable() {
            public void run(AnActionButton button) {
            }
        }).setRemoveAction(new AnActionButtonRunnable() {
            public void run(AnActionButton anActionButton) {
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
    private String inputItemName(String initValue) {
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
     * 创建动作组
     *
     * @return 动作组
     */
    private DefaultActionGroup createActionGroup() {
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        // 新增事件
        actionGroup.add(new AnAction(AllIcons.General.Add) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                //输入元素名称名称
                String itemName = inputItemName("");
                if (itemName == null) {
                    return;
                }
                addItem(itemName);
            }
        });
        // 复制事件
        actionGroup.add(new AnAction(AllIcons.Actions.Copy) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                T selectedItem = getSelectedItem();

            }

            @Override
            public void update(AnActionEvent e) {
                e.getPresentation().setEnabled(getSelectedItem() != null);
            }
        });
        // 删除事件
        actionGroup.add(new AnAction(AllIcons.General.Remove) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                T selectedItem = getSelectedItem();
                // 确认删除？
                if (MessageDialogBuilder.yesNo("module template", String.format("确认删除%s？", "")).isYes()) {
                    deleteItem(selectedItem);
                }
            }

            @Override
            public void update(AnActionEvent e) {
                e.getPresentation().setEnabled(getSelectedItem() != null);
            }
        });

        return actionGroup;
    }

    /**
     * 重置方法
     *
     * @param itemList     元素列表
     * @param selectedIndex 选中的元素下标
     */
    public void reset(@NotNull List<T> itemList, int selectedIndex) {
        this.itemList = itemList;
        listPanel.setModel(new CollectionListModel<>(dataConvert()));

        // 存在元素时，默认选中第一个元素
        if (!itemList.isEmpty()) {
            listPanel.setSelectedIndex(selectedIndex);
        }
    }

    /**
     * 数据转换
     *
     * @return 转换结果
     */
    private List<String> dataConvert() {
        List<String> data = new ArrayList<>();
//        itemList.forEach(item -> data.add(item.getName()));
        return data;
    }

    /**
     * 获取选中元素
     *
     * @return 选中元素
     */
    public T getSelectedItem() {
        String selectedName = listPanel.getSelectedValue();
        if (selectedName == null) {
            return null;
        }
        for (T t : itemList) {
//            if (Objects.equals(t.getName(), selectedName)) {
//                return t;
//            }
        }
        return null;
    }
}
