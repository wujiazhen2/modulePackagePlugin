package com.qworldr.ui;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.ui.AnActionButton;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModuleItemSelectPannel extends BaseItemSelectPanel {
    protected ModuleItemSelectPannel(@NotNull List itemList) {
        super(itemList);
    }

    @Override
    protected void addItem(String name) {

    }

    @Override
    protected void copyItem(String newName, Object item) {

    }

    @Override
    protected void deleteItem(Object item) {

    }

    @Override
    protected void selectedItem(Object item) {

    }

    private void addTemplateOrGroup(AnActionButton button) {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new DumbAwareAction("File Template") {
            public void actionPerformed(@NotNull AnActionEvent e) {
                if (e == null) {
                    return;
                }
            }
        });
        group.add(new DumbAwareAction("Module Template") {
            public void actionPerformed(@NotNull AnActionEvent e) {
                if (e == null) {
                    return;
                }

            }
        });
        DataContext context = DataManager.getInstance().getDataContext(button.getContextComponent());
        ListPopup popup = JBPopupFactory.getInstance().createActionGroupPopup((String)null, group, context, JBPopupFactory.ActionSelectionAid.ALPHA_NUMBERING, true, (String)null);
        popup.show(button.getPreferredPopupPoint());
    }
}
