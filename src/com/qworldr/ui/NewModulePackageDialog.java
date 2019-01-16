package com.qworldr.ui;

import com.intellij.openapi.actionSystem.ActionToolbarPosition;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.Splitter;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.AnActionButtonRunnable;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.border.CustomLineBorder;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.PlatformIcons;
import com.intellij.util.ui.JBUI;
import com.qworldr.data.NodeType;
import com.qworldr.data.PersistentSetting;
import com.qworldr.data.TemplateNode;
import com.qworldr.data.TemplateTree;
import com.qworldr.setting.Context;
import com.qworldr.utils.StringUtils;
import com.qworldr.utils.TemplateTreeUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.*;
import java.util.List;

/**
 * @Author wujiazhen
 * @Date 2018/12/6
 */
public class NewModulePackageDialog extends DialogWrapper {
    private JPanel mainPanel;
    private JTextField moduleField;
    private JTextField packageField;
    private JTree templateTree;
    private PsiDirectory psiDirectory;
    private Project project;
    private List<PsiElement> psiElementList = new ArrayList<>();
    private JTreeWarp jTreeWarp;
    private JList<String> jList;
    private Properties properties = new Properties();

    public NewModulePackageDialog(@Nullable Project project, PsiDirectory psiDirectory) {
//        AllIcons.General.Add
        super(project, false);
        setTitle("new module package");
        this.psiDirectory = psiDirectory;
        this.project = project;
        Context.persistentSetting = ServiceManager.getService(PersistentSetting.class);
        this.init();
    }

    @Override
    protected void doOKAction() {
        DefaultMutableTreeNode root = this.jTreeWarp.getRoot();
        Enumeration children = root.children();
        List<TemplateNode> list = new ArrayList<>();
        String packageFieldText = this.packageField.getText();
        String moduleFieldText = this.moduleField.getText();
        if(org.apache.commons.lang3.StringUtils.isBlank(packageFieldText)){
            packageField.setText(moduleField.getText());
        }
        if(org.apache.commons.lang3.StringUtils.isBlank(packageFieldText) || org.apache.commons.lang3.StringUtils.isBlank(moduleFieldText)){
            Messages.showMessageDialog("module name or package name can't be null ", "错误提示", PlatformIcons.ERROR_INTRODUCTION_ICON);
        }
        while (children.hasMoreElements()) {
            DefaultMutableTreeNode o = (DefaultMutableTreeNode) children.nextElement();
            list.add((TemplateNode) o.getUserObject());
        }

        properties.put("MODULE_NAME", org.apache.commons.lang3.StringUtils.capitalize(moduleFieldText));
        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiDirectory subdirectory = createPsiDirectory(psiDirectory, packageFieldText);
            psiElementList.add(subdirectory);
            generatorPsiFile(list, subdirectory);
        });
        super.doOKAction();
    }

    public PsiDirectory createPsiDirectory(PsiDirectory parent, String dirname) {
        dirname = dirname.toLowerCase();
        VirtualFile child = parent.getVirtualFile().findChild(dirname);
        PsiDirectory subdirectory = null;
        if (child == null) {
            subdirectory = parent.createSubdirectory(dirname);
        } else {
            subdirectory = PsiManager.getInstance(project).findDirectory(child);
        }
        return subdirectory;
    }

    public void generatorPsiFile(List<TemplateNode> templateNodes, PsiDirectory psiDirectory) {
        for (TemplateNode templateNode : templateNodes) {
            if (templateNode.getType().equals(NodeType.PACKAGE)) {
                PsiDirectory subdirectory = createPsiDirectory(psiDirectory, templateNode.getNameExpression().toLowerCase());
                psiElementList.add(subdirectory);
                if (templateNode.getChilds() != null && templateNode.getChilds().size() > 0) {
                    generatorPsiFile(templateNode.getChilds(), subdirectory);
                }
            } else {
                String replace = StringUtils.replace(templateNode.getNameExpression(), properties);
                VirtualFile child = psiDirectory.getVirtualFile().findChild(replace + templateNode.getType().suffix());
                if (child != null) {
                    Messages.showMessageDialog(String.format("文件已存在%s", replace), "错误提示", PlatformIcons.ERROR_INTRODUCTION_ICON);
                    return;
                }
                PsiClass aClass = JavaDirectoryService.getInstance().createClass(psiDirectory, replace, templateNode.getTempName());
                psiElementList.add(psiDirectory);
            }
        }

    }


    public List<PsiElement> getPsiElementList() {
        return psiElementList;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        this.templateTree = new Tree();
        this.jTreeWarp = new JTreeWarp(templateTree, project);
        this.templateTree.setRootVisible(false);
        this.mainPanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel rightPanel = new JPanel(new BorderLayout());
        topPanel.add(new Label("Module Name:  "));
        this.moduleField = new JBTextField();
        this.moduleField.setPreferredSize(JBUI.size(170, 30));
        topPanel.add(moduleField);
        rightPanel.add(topPanel, BorderLayout.NORTH);
        topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new Label("Package Name:"));
        this.packageField = new JBTextField();
        this.packageField.setPreferredSize(JBUI.size(170, 30));
        topPanel.add(packageField);
        this.moduleField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                packageField.setText(moduleField.getText());
            }
        });
        rightPanel.add(topPanel, BorderLayout.CENTER);
        ToolbarDecorator toolbarDecorator = ToolbarDecorator.createDecorator(templateTree).setRemoveAction(new AnActionButtonRunnable() {
            @Override
            public void run(AnActionButton anActionButton) {
                DefaultMutableTreeNode selectedNode = jTreeWarp.getSelectedNode();
                if (selectedNode == null) {
                    return;
                }
                selectedNode.removeAllChildren();
                selectedNode.removeFromParent();
                jTreeWarp.getjTree().updateUI();
            }
        }).setToolbarPosition(ActionToolbarPosition.RIGHT);

        rightPanel.add(toolbarDecorator.createPanel(), BorderLayout.SOUTH);
        JPanel leftPanel = new JPanel(new BorderLayout());
        this.jList = new JBList();
        // 只能单选
        this.jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.jList.setBorder(new CustomLineBorder(1, 1, 1, 1));
        leftPanel.add(this.jList);
        Set<String> keySet = Context.persistentSetting.getModuleTemplateTree().keySet();
        String[] string = new String[keySet.size()];
        this.jList.setListData(keySet.toArray(string));
        this.jList.addListSelectionListener(e -> {
            JBList<String> source = (JBList<String>) e.getSource();
            String selectedValue = source.getSelectedValue();
            Context.persistentSetting.setSelectedModule(selectedValue);
            initTree();
        });
        // 左右分割面板并添加至主面板
        Splitter splitter = new Splitter(false, 0.3F);
        splitter.setFirstComponent(leftPanel);
        splitter.setSecondComponent(rightPanel);
        mainPanel.add(splitter, BorderLayout.NORTH);
        this.templateTree.setCellRenderer(new TreeRenderer());
        String selectedModule = Context.persistentSetting.getSelectedModule();
        for (int i = 0; i < this.jList.getModel().getSize(); i++) {
            if (this.jList.getModel().getElementAt(i).equals(selectedModule)) {
                this.jList.setSelectedIndex(i);
                break;
            }
        }
        mainPanel.setPreferredSize(JBUI.size(440, 400));
        return mainPanel;
    }

    public void initTree() {
        //初始化树
        TemplateTree moduleTree = Context.persistentSetting.getModuleTree();
        if (moduleTree == null) {
            return;
        }
        //清空树节点
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.templateTree.getModel().getRoot();
        root.removeAllChildren();
        List<TemplateNode> childs = moduleTree.getChilds();
        for (TemplateNode child : childs) {
            DefaultMutableTreeNode defaultMutableTreeNode = TemplateTreeUtils.addNode(templateTree, child, root);
            if (child.getChilds() != null && child.getChilds().size() > 0) {
                buildTree(child, defaultMutableTreeNode);
            }
        }
        TemplateTreeUtils.expandTree(templateTree);
        this.templateTree.updateUI();
    }

    public void buildTree(TemplateNode templateNode, DefaultMutableTreeNode treeNode) {
        List<TemplateNode> childs = templateNode.getChilds();
        for (TemplateNode child : childs) {
            DefaultMutableTreeNode defaultMutableTreeNode = TemplateTreeUtils.addNode(templateTree, child, treeNode);
            if (child.getChilds() != null && child.getChilds().size() > 0) {
                buildTree(child, defaultMutableTreeNode);
            }
        }
    }


}
