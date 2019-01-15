package com.qworldr.data;

import com.intellij.icons.AllIcons;

import javax.swing.*;

/**
 * @author wujiazhen
 * @Date 2019/1/11
 */
public enum NodeType {
    PACKAGE(){
        @Override
        public Icon getIcon() {
            return AllIcons.Modules.SourceFolder;
        }
    },
    JAVA(){
        @Override
        public Icon getIcon() {
            return AllIcons.FileTypes.Java;
        }

        @Override
        public String suffix() {
            return ".java";
        }
    },
    ;

    public abstract Icon getIcon();

    public  String suffix(){
        return "";
    };
}
