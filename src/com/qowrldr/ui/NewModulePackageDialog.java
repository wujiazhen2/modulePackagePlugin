package com.qowrldr.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @Author wujiazhen
 * @Date 2018/12/5
 */
public class NewModulePackageDialog extends DialogWrapper {
    public NewModulePackageDialog(@Nullable Project project) {
        super(project, false);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return null;
    }
}
