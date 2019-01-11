package com.qworldr.data;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wujiazhen
 * @Date 2019/1/11
 */
@State(name = "generateCodeSetting", storages = @Storage("generate-code-setting.xml"))
public class PersistentSetting implements PersistentStateComponent<PersistentSetting> {

    /**
     * 选择模块
     */
    private String selectedModule;

    /**
     * 模板树  module->tree
     */
    private Map<String,TemplateTree> moduleTemplateTree;

    @Nullable
    @Override
    public PersistentSetting getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull PersistentSetting persistentSetting) {
        // 覆盖初始配置
        XmlSerializerUtil.copyBean(persistentSetting, this);
        if(this.getModuleTemplateTree()==null){
            this.moduleTemplateTree=new HashMap<>();
        }
    }

    @Override
    public void noStateLoaded() {
        this.moduleTemplateTree=new HashMap<>();
    }

    public String getSelectedModule() {
        return selectedModule;
    }

    public void setSelectedModule(String selectedModule) {
        this.selectedModule = selectedModule;
    }

    public Map<String, TemplateTree> getModuleTemplateTree() {
        return moduleTemplateTree;
    }

    public void setModuleTemplateTree(Map<String, TemplateTree> moduleTemplateTree) {
        this.moduleTemplateTree = moduleTemplateTree;
    }
}
