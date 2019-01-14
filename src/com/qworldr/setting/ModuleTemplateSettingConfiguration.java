package com.qworldr.setting;

import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.impl.FileTemplateConfigurable;
import com.intellij.ide.fileTemplates.impl.FileTemplateManagerImpl;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.qworldr.data.PersistentSetting;
import com.qworldr.data.TemplateNode;
import com.qworldr.ui.ModuleComboBoxPannel;
import com.qworldr.ui.ModuleItemSelectPannel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ModuleTemplateSettingConfiguration implements SearchableConfigurable {
    private JPanel panel;
    private FileTemplateManager fileTemplateManager;

    private Project project;

    public ModuleTemplateSettingConfiguration(Project project) {
        this.project = project;
        this.fileTemplateManager = FileTemplateManager.getInstance(project);
        Context.fileTemplateManager = this.fileTemplateManager;
        Context.project = this.project;
        Context.persistentSetting = ServiceManager.getService(PersistentSetting.class);
    }

    @NotNull
    @Override
    public String getId() {
        return "module template";
    }

    /**
     * 控制在配置面板中左侧窗口的显示名称
     */
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "module template";
    }

    private ModuleComboBoxPannel myExpandByDefaultPanel;
    private ModuleItemSelectPannel moduleItemSelectPannel;
    private FileTemplateConfigurable fileTemplateConfigurable;
    private JComponent fileTemplateComponent;

    private boolean init;
    /**
     * 配置界面的绘制，负责用户输入信息的接受
     *
     * @return
     */
    @Nullable
    @Override
    public JComponent createComponent() {
        this.panel = new JPanel(new BorderLayout());
        PersistentSetting persistentSetting = ServiceManager.getService(PersistentSetting.class);
        myExpandByDefaultPanel = new ModuleComboBoxPannel(persistentSetting.getModuleTemplateTree().keySet(), persistentSetting.getSelectedModule());
        this.panel.add(this.myExpandByDefaultPanel, BorderLayout.NORTH);
        fileTemplateConfigurable=new FileTemplateConfigurable(project);
        fileTemplateComponent = fileTemplateConfigurable.createComponent();
        fileTemplateConfigurable.focusToNameField();
        moduleItemSelectPannel = new ModuleItemSelectPannel(new ArrayList()){
            @Override
            protected void selectedItem(TemplateNode item) {
                super.selectedItem(item);
                fileTemplateConfigurable.setTemplate(fileTemplateManager.getTemplate(item.getTempName()), FileTemplateManagerImpl.getInstanceImpl(project).getDefaultTemplateDescription());
            }
        };
        JComponent component = moduleItemSelectPannel.getComponent();
        moduleItemSelectPannel.setRightEditTab(fileTemplateComponent);
        this.panel.add(component, BorderLayout.CENTER);
        init=true;
        return panel;
    }

    @Override
    public boolean isModified() {
        if(init) {
            return fileTemplateConfigurable.isModified();
        }
        return true;
    }

    /**
     * 用户点击“OK”或“Apply”按钮后会调用该方法，通常用于完成配置信息持久化
     *
     * @throws ConfigurationException
     */
    @Override
    public void apply() throws ConfigurationException {
        Context.persistentSetting.serialize();
        fileTemplateConfigurable.apply();
    }
}
