package com.qworldr.ui;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.qworldr.data.PersistentSetting;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ModuleTemplatePanel extends JPanel implements Disposable {

    private final JPanel myDetailsPanel = new JPanel(new CardLayout());
    private final JLabel myEmptyCardLabel = new JLabel();
    private final ModuleComboBoxPannel myExpandByDefaultPanel;
    private ModuleItemSelectPannel moduleItemSelectPannel ;
    public ModuleTemplatePanel() {
        super(new BorderLayout());
        this.myDetailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        this.myEmptyCardLabel.setHorizontalAlignment(0);
        PersistentSetting persistentSetting = ServiceManager.getService(PersistentSetting.class);
        myExpandByDefaultPanel=new ModuleComboBoxPannel(persistentSetting.getModuleTemplateTree().keySet(),persistentSetting.getSelectedModule());
        this.add(this.myExpandByDefaultPanel, BorderLayout.NORTH);
        moduleItemSelectPannel = new ModuleItemSelectPannel(new ArrayList());
        this.add(moduleItemSelectPannel.getComponent(), BorderLayout.CENTER);
    }


    @Override
    public void dispose() {

    }

    public boolean modified() {
        return true;
    }
}

