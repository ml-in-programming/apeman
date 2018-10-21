import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.ui.Messages

class LauncherAction : AnAction ("Check current file"){

    override fun actionPerformed(e: AnActionEvent?) {
        val project = e?.project
        Messages.showMessageDialog(project, "Hello from intellij actions!", "Checking methods", Messages.getInformationIcon())
    }
}