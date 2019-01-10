package com.qworldr.ui;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.intellij.codeInsight.CodeInsightBundle;
import com.intellij.codeInsight.template.impl.TemplateExpandShortcutPanel;
import com.intellij.codeInsight.template.impl.TemplateSettings;
import com.intellij.icons.AllIcons;
import com.intellij.ide.DataManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionToolbarPosition;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Splitter;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
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
import java.util.ArrayList;

public class ModuleTemplatePanel extends JPanel implements Disposable {


    private final JPanel myDetailsPanel = new JPanel(new CardLayout());
    private final JLabel myEmptyCardLabel = new JLabel();
    private final ModuleComboBoxPannel myExpandByDefaultPanel = new ModuleComboBoxPannel();
    private ModuleItemSelectPannel moduleItemSelectPannel=new ModuleItemSelectPannel(new ArrayList());

    public ModuleTemplatePanel() {
        super(new BorderLayout());
        this.myDetailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        this.myEmptyCardLabel.setHorizontalAlignment(0);
        this.add(this.myExpandByDefaultPanel,  BorderLayout.NORTH);
         moduleItemSelectPannel = new ModuleItemSelectPannel(new ArrayList());
        this.add(moduleItemSelectPannel.getComponent(), BorderLayout.CENTER);
    }


    @Override
    public void dispose() {

    }
}

