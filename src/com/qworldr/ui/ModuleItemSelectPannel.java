package com.qworldr.ui;

import com.intellij.ide.DataManager;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.impl.CustomFileTemplate;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Constraints;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.ui.AnActionButton;
import com.qworldr.Constants;
import com.qworldr.data.NodeType;
import com.qworldr.data.TemplateNode;
import com.qworldr.data.TemplateTree;
import com.qworldr.setting.Context;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModuleItemSelectPannel extends BaseItemSelectPanel<TemplateNode> {


    @Override
    public JComponent getComponent() {
        JComponent component = super.getComponent();
        setTreeRender(new TreeRenderer());
        initTree();
        return component;
    }

    public void initTree() {
        //初始化树
        TemplateTree moduleTree = Context.persistentSetting.getModuleTree();
        if (moduleTree == null) {
            return;
        }
        //清空树节点
        getRoot().removeAllChildren();
        List<TemplateNode> childs = moduleTree.getChilds();
        for (TemplateNode child : childs) {
            DefaultMutableTreeNode defaultMutableTreeNode = addNode(child, getRoot());
            if (child.getChilds() != null && child.getChilds().size() > 0) {
                buildTree(child, defaultMutableTreeNode);
            }
        }
        this.expandTree();
        this.updateUI();
    }

    public void buildTree(TemplateNode templateNode, DefaultMutableTreeNode treeNode) {
        List<TemplateNode> childs = templateNode.getChilds();
        for (TemplateNode child : childs) {
            DefaultMutableTreeNode defaultMutableTreeNode = addNode(child, treeNode);
            if (child.getChilds() != null && child.getChilds().size() > 0) {
                buildTree(child, defaultMutableTreeNode);
            }
        }
    }

    @Override
    protected void addItem(AnActionButton anActionButton) {
        addTemplateOrGroup(anActionButton);
    }


    @Override
    protected void deleteItem(AnActionButton anActionButton) {
        TemplateNode selectedItem = getSelectedItem();
        if (selectedItem.getParent() != null) {
            selectedItem.getParent().removeChild(selectedItem);
        } else {
            Context.persistentSetting.getModuleTree().removeChild(selectedItem);
        }
        Context.persistentSetting.modified();
        removeNode();
    }

    @Override
    protected void renameItem(AnActionButton anActionButton) {
        TemplateNode selectedItem = getSelectedItem();
        String input = inputItemName(selectedItem.getNameExpression());
        if (input == null) {
            return;
        }
        selectedItem.setNameExpression(input);
        Context.persistentSetting.modified();
    }

    @Override
    protected boolean selectedItem(TemplateNode oldItem, TemplateNode item) {
        return true;
    }


    private void addTemplateOrGroup(AnActionButton button) {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new DefaultActionGroup("File Template", true) {
            @Override
            public void update(AnActionEvent e) {
                e.getPresentation().setEnabledAndVisible(true);
                removeAll();
                setDefaultIcon(false);
                setEnabledInModalContext(false);
                Set<String> set = new HashSet<>();
                for (FileTemplate allTemplate : Context.fileTemplateManager.getAllTemplates()) {
                    if (allTemplate instanceof CustomFileTemplate) {
                        addAction(new DumbAwareAction(allTemplate.getName()) {
                            @Override
                            public void actionPerformed(AnActionEvent anActionEvent) {
                                String input = inputItemName(Constants.MODULE_NAME);
                                if (input == null) {
                                    return;
                                }
                                TemplateNode child = TemplateNode.valueOf(this.getTemplatePresentation().getText(), input, NodeType.JAVA);
                                addTreeNode(child);
                                Context.persistentSetting.modified();
                            }
                        }, Constraints.LAST);
                    }
                }
            }
        });
        group.add(new DumbAwareAction("Package Template") {
            public void actionPerformed(@NotNull AnActionEvent e) {
                String input = inputItemName("Unnamed");
                if (input == null) {
                    return;
                }
                TemplateNode packageNode = TemplateNode.createPackageNode(input);
                addTreeNode(packageNode);
                Context.persistentSetting.modified();
            }

        });
        group.add(new DumbAwareAction("Top Package") {
            public void actionPerformed(@NotNull AnActionEvent e) {
                String input = inputItemName("Unnamed");
                if (input == null) {
                    return;
                }
                TemplateNode packageNode = TemplateNode.createPackageNode(input);
                addScendsNode(packageNode);
                Context.persistentSetting.modified();
            }

        });
        DataContext context = DataManager.getInstance().getDataContext(button.getContextComponent());
        ListPopup popup = JBPopupFactory.getInstance().createActionGroupPopup((String) null, group, context, JBPopupFactory.ActionSelectionAid.ALPHA_NUMBERING, true, (String) null);
        popup.show(button.getPreferredPopupPoint());
    }

    @Override
    public boolean checkName(String name) {
        if (StringUtils.isBlank(name)) {
            return false;
        }
        TemplateNode selectedItem = getSelectedItem();
        List<TemplateNode> sibilings;
        if (selectedItem != null && selectedItem.getType().equals(NodeType.PACKAGE)) {
            sibilings = selectedItem.getChilds();
        } else if (selectedItem != null && selectedItem.getParent() != null) {
            sibilings = selectedItem.getParent().getChilds();
        } else {
            sibilings = Context.persistentSetting.getModuleTree().getChilds();
        }
        if (sibilings == null || sibilings.size() == 0) {
            return true;
        }
        for (TemplateNode sibiling : sibilings) {
            if (sibiling.getNameExpression().equals(name)) {
                return false;
            }
        }
        return true;
    }

    public void addTreeNode(TemplateNode child) {
        TemplateTree templateTree = Context.persistentSetting.getModuleTree();
        TemplateNode selectedItem = getSelectedItem();
        if (selectedItem == null) {
            templateTree.addChild(child);
            addChildNode(child);
        } else if (!NodeType.PACKAGE.equals(selectedItem.getType())) {
            TemplateNode parent = selectedItem.getParent();
            if (parent == null) {
                Context.persistentSetting.getModuleTree().addChild(child);
            } else {
                parent.addChild(child);
            }
            addSibilingNode(child);
        } else {
            if (selectedItem != null) {
                selectedItem.addChild(child);
            }
            addChildNode(child);
        }

    }
}
