package com.qworldr.data;

import java.util.List;

/**
 * @Author wujiazhen
 * @Date 2018/12/6
 */
public class TemplateTree {

    private List<TemplateNode> childs;

    public List<TemplateNode> getChilds() {
        return childs;
    }

    public void setChilds(List<TemplateNode> childs) {
        this.childs = childs;
    }


    public void addChild(TemplateNode child){
        this.childs.add(child);

    }

    public void removeChild(TemplateNode child){
        this.childs.remove(child);
    }
}
