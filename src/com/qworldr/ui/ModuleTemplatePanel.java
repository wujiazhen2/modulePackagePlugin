package com.qworldr.ui;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.intellij.codeInsight.template.impl.TemplateSettings;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionToolbarPosition;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Splitter;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.AnActionButtonRunnable;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;

public class ModuleTemplatePanel extends JPanel implements Disposable {

    private JTree myTree;
    private DefaultMutableTreeNode myTreeRoot = new DefaultMutableTreeNode((Object) null);
    private final JPanel myDetailsPanel = new JPanel(new CardLayout());
    private final JLabel myEmptyCardLabel = new JLabel();


    public ModuleTemplatePanel() {
        super(new BorderLayout());
        this.myDetailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        this.myEmptyCardLabel.setHorizontalAlignment(0);
        Splitter splitter = new Splitter(true, 0.9F);
        splitter.setFirstComponent(this.createTable());
        this.add(splitter, "Center");
    }

    @Nullable
    private DefaultMutableTreeNode getNode(int row) {
        JTree tree = this.myTree;
        TreePath path = tree.getPathForRow(row);
        return path != null ? (DefaultMutableTreeNode) path.getLastPathComponent() : null;
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
        return decorator.setToolbarPosition(ActionToolbarPosition.RIGHT);
    }

    @Override
    public void dispose() {

    }
}

