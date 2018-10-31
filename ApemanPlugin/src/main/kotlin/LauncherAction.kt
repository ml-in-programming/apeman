import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.ui.Messages

class LauncherAction : AnAction ("Check current file"){

    override fun actionPerformed(e: AnActionEvent?) {
        val project = e?.project
        val file = e?.getData(LangDataKeys.PSI_FILE)

        if (file == null || project == null) return

        val candidates = CandidatesOfFile(file).candidates

        Messages.showMessageDialog(project, candidates.joinToString(separator = "\n"), "Checking methods", Messages.getInformationIcon())
    }
}