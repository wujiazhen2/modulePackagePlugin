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
import com.qworldr.data.NodeType;
import com.qworldr.data.TemplateNode;
import com.qworldr.data.TemplateTree;
import com.qworldr.setting.Context;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModuleItemSelectPannel extends BaseItemSelectPanel<TemplateNode> {


    public ModuleItemSelectPannel(@NotNull List itemList) {
        super(itemList);
    }

    @Override
    public JComponent getComponent() {
        JComponent component = super.getComponent();
        setTreeRender(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
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
        });
        //初始化树
        TemplateTree moduleTree = Context.persistentSetting.getModuleTree();
        if (moduleTree == null) {
            return component;
        }
        List<TemplateNode> childs = moduleTree.getChilds();
        for (TemplateNode child : childs) {
            DefaultMutableTreeNode defaultMutableTreeNode = addNode(child, getRoot());
            if (child.getChilds() != null && child.getChilds().size() > 0) {
                buildTree(child, defaultMutableTreeNode);
            }
        }
        return component;
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
    protected void copyItem(AnActionButton anActionButton) {

    }

    @Override
    protected void deleteItem(AnActionButton anActionButton) {

    }

    @Override
    protected void selectedItem(TemplateNode item) {

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
                                String input = inputItemName("EMPTY");
                                if (input == null) {
                                    return;
                                }
                                TemplateNode child = TemplateNode.valueOf(this.getTemplatePresentation().getText(), input, NodeType.JAVA);
                                addTreeNode(child);

                            }
                        }, Constraints.LAST);
                    }
                }
            }
        });
        group.add(new DumbAwareAction("Module Template") {
            public void actionPerformed(@NotNull AnActionEvent e) {
                String input = inputItemName("EMPTY");
                if (input == null) {
                    return;
                }
                TemplateNode packageNode = TemplateNode.createPackageNode(input);
                addTreeNode(packageNode);
            }
        });
        DataContext context = DataManager.getInstance().getDataContext(button.getContextComponent());
        ListPopup popup = JBPopupFactory.getInstance().createActionGroupPopup((String) null, group, context, JBPopupFactory.ActionSelectionAid.ALPHA_NUMBERING, true, (String) null);
        popup.show(button.getPreferredPopupPoint());
    }

    public void addTreeNode(TemplateNode child) {
        TemplateTree templateTree = Context.persistentSetting.getModuleTree();
        TemplateNode selectedItem = getSelectedItem();
        if (selectedItem == null) {
            templateTree.addChild(child);
            addChildNode(child);
        } else if (!NodeType.PACKAGE.equals(selectedItem.getType())) {
            selectedItem.getParent().addChild(child);
            addSibilingNode(child);
        } else {
            if (selectedItem != null) {
                selectedItem.addChild(child);
            }
            addChildNode(child);
        }

    }
}
