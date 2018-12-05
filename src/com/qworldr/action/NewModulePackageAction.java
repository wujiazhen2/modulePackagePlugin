package com.qworldr.action;

import com.intellij.ide.IdeView;
import com.intellij.ide.actions.CreateInDirectoryActionBase;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.qowrldr.ui.NewModulePackageDialog;

/**
 * @Author wujiazhen
 * @Date 2018/12/5
 */
public class NewModulePackageAction extends CreateInDirectoryActionBase {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        IdeView view = (IdeView) e.getData(LangDataKeys.IDE_VIEW);
        if (view != null) {
            Project project = e.getProject();
            PsiDirectory dir = view.getOrChooseDirectory();
            if (dir != null) {
                NewModulePackageDialog newModulePackageDialog = new NewModulePackageDialog(project);
                newModulePackageDialog.show();
            }
        }
    }



}
