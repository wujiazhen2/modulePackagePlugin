package com.qworldr.ui;

import com.qworldr.data.PersistentSetting;
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
        persistentSetting.modified();
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
        persistentSetting.modified();
        reset(new ArrayList<>(moduleTemplateTree.keySet()),select);
    }

    @Override
    protected void copyGroup(String name) {
        Map<String, TemplateTree> moduleTemplateTree = persistentSetting.getModuleTemplateTree();
        String selectedItem = getSelectedItem();
        TemplateTree templateTree = moduleTemplateTree.get(selectedItem);
        //深拷贝
        try {
            templateTree=templateTree.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        moduleTemplateTree.put(name,templateTree);
        persistentSetting.setSelectedModule(name);
        persistentSetting.modified();
        reset(new ArrayList<>(moduleTemplateTree.keySet()),name);
    }

    @Override
    protected void changeGroup(String name) {
        Context.persistentSetting.setSelectedModule(name);
        Context.persistentSetting.modified();
    }
}
