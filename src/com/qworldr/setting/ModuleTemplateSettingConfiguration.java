package com.qworldr.setting;

import com.intellij.codeInsight.template.impl.TemplateListPanel;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.qworldr.ui.ModuleTemplatePanel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ModuleTemplateSettingConfiguration implements SearchableConfigurable {
    private boolean modified;
    private ModuleTemplatePanel panel;

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

    /**
     * 配置界面的绘制，负责用户输入信息的接受
     *
     * @return
     */
    @Nullable
    @Override
    public JComponent createComponent() {
        this.panel = new ModuleTemplatePanel();
        return panel;
    }

    @Override
    public boolean isModified() {
        return modified;
    }

    /**
     * 用户点击“OK”或“Apply”按钮后会调用该方法，通常用于完成配置信息持久化
     *
     * @throws ConfigurationException
     */
    @Override
    public void apply() throws ConfigurationException {

    }
}
