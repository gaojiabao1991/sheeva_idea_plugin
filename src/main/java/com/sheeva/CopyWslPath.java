package com.sheeva;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;

import static com.intellij.openapi.actionSystem.CommonDataKeys.PROJECT;
import static com.intellij.openapi.actionSystem.CommonDataKeys.VIRTUAL_FILE;

/**
 * 给我的windows系统里的idea添加拷贝文件/文件夹的wsl路径的功能.
 * 参考这个项目实现的:
 * https://github.com/mub/brainYard/tree/master/jetBrains/idea/plugins/copyNixPath
 *
 * 如何开发idea插件参考:
 * https://www.jianshu.com/p/91d0bdbbe79f
 */
public class CopyWslPath extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {

        final VirtualFile file = event.getData(VIRTUAL_FILE);
        if (file == null) {
            showPopup(event, "<p><b>No active file</b></p>");
            return;
        }

        final String path = file.getCanonicalPath();
        if (path == null) {
            showPopup(event, "<p><b>No path for the current file</b></p>");
            return;
        }

        final String wslPath = convertToWslPath(file.getPath());
        showPopup(event, String.format("<p><b>copy to clipboard: %s</b></p>", wslPath));
        CopyPasteManager.getInstance().setContents(new StringSelection(
                wslPath
        ));
    }

    private String convertToWslPath(String path) {
        String disk = path.substring(0, 1).toLowerCase();
        String relativePath = path.substring(3);
        return String.format("/mnt/%s/%s", disk, relativePath);
    }

    private void showPopup(@NotNull final AnActionEvent event, @NotNull final String html) {

        final StatusBar statusBar = WindowManager.getInstance().getStatusBar(PROJECT.getData(event.getDataContext()));
        if (statusBar == null) return;

        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(html, MessageType.INFO, null)
                .setFadeoutTime(2500)
                .createBalloon()
                .show(RelativePoint.getCenterOf(statusBar.getComponent()), Balloon.Position.atRight);
    }
}
