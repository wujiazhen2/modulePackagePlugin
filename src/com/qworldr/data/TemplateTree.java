package com.qworldr.data;

import com.google.gson.annotations.Expose;
import com.intellij.util.xmlb.XmlSerializerUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author wujiazhen
 * @Date 2018/12/6
 */
public class TemplateTree implements Cloneable{
    public static final String PATH_SEPARATOR = "/";
    @Expose
    private List<TemplateNode> childs=new ArrayList<>();

    public List<TemplateNode> getChilds() {
        return childs;
    }

    public void setChilds(List<TemplateNode> childs) {
        this.childs = childs;
    }


    public void addChild(TemplateNode child) {
        this.childs.add(child);

    }

    public void removeChild(TemplateNode child) {
        this.childs.remove(child);
    }

    public TemplateNode findTemplateNode(String path) {
        // user/path
        if(path.startsWith("/")){
            path=path.substring(1);
        }
        String[] split = path.split(PATH_SEPARATOR);
        int count = 0;
        List<TemplateNode> templateNodes = childs;
        TemplateNode templateNode;
        for (String s : split) {
            count++;
            for (int i = 0; i < templateNodes.size(); i++) {
                templateNode = templateNodes.get(i);
                if (templateNode.getNameExpression().equals(s)) {
                    if (count == split.length) {
                        return templateNode;
                    } else {
                        templateNodes = templateNode.getChilds();
                        i=0;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public TemplateTree clone() throws CloneNotSupportedException {
        TemplateTree templateTree = new TemplateTree();
        this.childs.forEach(e->{
            try {
                templateTree.getChilds().add(e.clone());
            } catch (CloneNotSupportedException e1) {
                e1.printStackTrace();
            }
        });
        return templateTree;
    }
}
