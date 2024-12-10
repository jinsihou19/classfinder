package org.vito.classfinder;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

/**
 * @author vito
 * @since 11.0
 * Created on 2024/11/13
 */
public class ActionNotifier {

    private static final Logger LOG = Logger.getInstance(ActionNotifier.class);

    public static void notifyAction(@Nullable Project project, String content) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("OpenClass-Notification")
                .createNotification("Open class in output directory", content, NotificationType.WARNING)
                .notify(project);
        LOG.info("CodeTourNotifier: " + content);
    }
}
