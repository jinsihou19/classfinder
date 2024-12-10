package org.vito.classfinder;

import com.intellij.ide.actions.RevealFileAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

import static org.vito.classfinder.ActionNotifier.notifyAction;

/**
 * 打开class路径
 *
 * @author vito
 * @since 11.0
 * Created on 2024/11/11
 */
public class OpenClassAction extends AnAction {

    public static final String JAVA_EXTENSION = "java";

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) {
            Messages.showMessageDialog("Can not find the project for the file",
                    "Error", Messages.getErrorIcon());
            return;
        }

        VirtualFile selectedFile = event.getData(CommonDataKeys.VIRTUAL_FILE);
        if (selectedFile == null || !JAVA_EXTENSION.equals(selectedFile.getExtension())) {
            notifyAction(project, "Please select a Java file");
            return;
        }

        @Nullable Module module = ModuleUtilCore.findModuleForFile(selectedFile, project);
        if (module == null) {
            notifyAction(project, "The module to which the file belongs could not be found");
            return;
        }

        VirtualFile outputDir = CompilerModuleExtension.getInstance(module).getCompilerOutputPath();
        if (outputDir == null) {
            notifyAction(project, "Can not find compile output directory");
            return;
        }

        File classFile = new File(outputDir.getPath() + "/" + getPathWithPackage(project, selectedFile) + ".class");
        if (!classFile.exists()) {
            notifyAction(project, "The class file could not be found and may not have been compiled");
        }
        RevealFileAction.openFile(classFile);
    }


    private static @NotNull String getPathWithPackage(Project project, VirtualFile selectedFile) {
        String classPathWithPackage = "";

        VirtualFile[] sourceRoots = ProjectRootManager.getInstance(project).getContentSourceRoots();

        for (VirtualFile root : sourceRoots) {
            String rootPath = root.getPath();
            String filePath = selectedFile.getPath();

            if (filePath.startsWith(rootPath)) {
                classPathWithPackage = filePath
                        .substring(rootPath.length() + 1)
                        .replaceAll("\\.java$", "");
            }
        }
        return classPathWithPackage;
    }
}