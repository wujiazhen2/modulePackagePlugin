package com.qworldr.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Transient;
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


    private String moduleTemplateTreeJson;

    /**
     * 模板树  module->tree
     */
    @Transient
    private Map<String, TemplateTree> moduleTemplateTree;

    @Nullable
    @Override
    public PersistentSetting getState() {
        return this;
    }

    @Transient
    private Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().enableComplexMapKeySerialization().create();

    @Override
    public void loadState(@NotNull PersistentSetting persistentSetting) {
        // 覆盖初始配置
        XmlSerializerUtil.copyBean(persistentSetting, this);

        this.moduleTemplateTree = gson.fromJson(moduleTemplateTreeJson, new TypeToken<Map<String, TemplateTree>>() {
        }.getType());

    }

    @Override
    public void noStateLoaded() {
        this.moduleTemplateTree = new HashMap<>();
    }

    public String getSelectedModule() {
        return selectedModule;
    }

    public void setSelectedModule(String selectedModule) {
        this.selectedModule = selectedModule;
    }
    @Transient
    public Map<String, TemplateTree> getModuleTemplateTree() {
        return moduleTemplateTree;
    }

    public void setModuleTemplateTree(Map<String, TemplateTree> moduleTemplateTree) {
        this.moduleTemplateTree = moduleTemplateTree;
    }
    @Transient
    public TemplateTree getModuleTree() {
        Map<String, TemplateTree> moduleTemplateTree = this.getModuleTemplateTree();
        TemplateTree templateTree = moduleTemplateTree.get(this.getSelectedModule());
        for (TemplateNode child : templateTree.getChilds()) {
            child.init(null);
        }
        return templateTree;
    }

    public void serialize() {
        if (this.moduleTemplateTree == null) {
            this.moduleTemplateTreeJson = "{}";
        } else {
            this.moduleTemplateTreeJson = gson.toJson(this.moduleTemplateTree);
        }
    }

    public String getModuleTemplateTreeJson() {
        return moduleTemplateTreeJson;
    }

    public void setModuleTemplateTreeJson(String moduleTemplateTreeJson) {
        this.moduleTemplateTreeJson = moduleTemplateTreeJson;
    }
}
