package com.qworldr.ui;

import com.intellij.openapi.components.ServiceManager;
import com.qworldr.data.PersistentSetting;
import com.qworldr.data.TemplateNode;
import com.qworldr.data.TemplateTree;
import com.qworldr.setting.Context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ModuleComboBoxPannel extends BaseGroupPanel {
    private PersistentSetting persistentSetting;
    public ModuleComboBoxPannel(Set<String> set,String defaul) {
        super(new ArrayList<>(set), defaul==null?"":defaul);
        persistentSetting = Context.persistentSetting;
    }



    @Override
    protected void createGroup(String name) {
        Map<String, TemplateTree> moduleTemplateTree = persistentSetting.getModuleTemplateTree();
        moduleTemplateTree.put(name,new TemplateTree());
        persistentSetting.setSelectedModule(name);
        reset(new ArrayList<>(moduleTemplateTree.keySet()),name);
    }

    @Override
    protected void deleteGroup(String name) {
        Map<String, TemplateTree> moduleTemplateTree = persistentSetting.getModuleTemplateTree();
        moduleTemplateTree.remove(name);
        Set<String> keySet = moduleTemplateTree.keySet();
        Iterator<String> iterator = keySet.iterator();
        String select="";
        if(iterator.hasNext()){
            select= iterator.next();
            persistentSetting.setSelectedModule(select);
        }
        reset(new ArrayList<>(moduleTemplateTree.keySet()),select);
    }

    @Override
    protected void copyGroup(String name) {
        Map<String, TemplateTree> moduleTemplateTree = persistentSetting.getModuleTemplateTree();
        String selectedItem = getSelectedItem();
        moduleTemplateTree.put(name,moduleTemplateTree.get(selectedItem));
        persistentSetting.setSelectedModule(name);
        reset(new ArrayList<>(moduleTemplateTree.keySet()),name);
    }

    @Override
    protected void changeGroup(String name) {
        Context.persistentSetting.setSelectedModule(name);
    }
}
