package com.qworldr.data;

import com.google.gson.annotations.Expose;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.qworldr.setting.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author wujiazhen
 * @Date 2018/12/6
 */
public class TemplateNode {
    @Expose
    private NodeType type;
    @Expose
    private String tempName;
    @Expose
    private String nameExpression;
    @Expose
    private List<TemplateNode> childs;

    private TemplateNode parent;

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }

    public String getTempName() {
        return tempName;
    }

    public void setTempName(String tempName) {
        this.tempName = tempName;
    }

    public List<TemplateNode> getChilds() {
        return childs;
    }

    public void setChilds(List<TemplateNode> childs) {
        this.childs = childs;
    }

    public String getNameExpression() {
        return nameExpression;
    }

    public void setNameExpression(String nameExpression) {
        this.nameExpression = nameExpression;
    }

    public void addChild(TemplateNode child){
        this.childs.add(child);
        child.setParent(this);
    }

    public void removeChild(TemplateNode child){
        this.childs.remove(child);
    }

    public TemplateNode getParent() {
        return parent;
    }

    public void setParent(TemplateNode parent) {
        this.parent = parent;
    }

    public static TemplateNode valueOf(String tempName,String name,NodeType nodeType){
        TemplateNode tm=new TemplateNode();
        tm.tempName=tempName;
        tm.nameExpression=name;
        tm.type=nodeType;
        return tm;
    }

    public static TemplateNode createPackageNode(String name){
        TemplateNode tm=new TemplateNode();
        tm.nameExpression=name;
        tm.type=NodeType.PACKAGE;
        return tm;
    }

    private FileTemplate getFileTemplate(){
        return Context.fileTemplateManager.getTemplate(this.tempName);
    }
    @Override
    public String toString() {
        return nameExpression;
    }

    public void init(TemplateNode parent) {
        setParent(parent);
        if(NodeType.PACKAGE.equals(this.type) && childs==null){
            childs=new ArrayList<>();
            return;
        }
        for (TemplateNode child : getChilds()) {
            child.init(this);
        }
    }
}
