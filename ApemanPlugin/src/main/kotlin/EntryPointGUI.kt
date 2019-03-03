import apeman_core.Launcher
import com.intellij.analysis.AnalysisScope
import com.intellij.analysis.BaseAnalysisAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import java.util.logging.Logger

private val log = Logger.getLogger("EntryPointGUI")

class LauncherAction : BaseAnalysisAction("check1", "check2") {

    override fun analyze(project: Project, scope: AnalysisScope) {
        val launcher = Launcher(project, scope)
        val candidates = launcher.getCandidatesWithProba()

        val sortedCandidates = candidates.sortedBy { -it.probability }
        var info = ""
        for ((cand, features, proba) in sortedCandidates) {
            info += "\n\n$cand:\n proba = $proba\n\n"
            for ((name, value) in features.features) {
                info += "$name = $value\n"
            }
        }

        Messages.showInfoMessage(info, "checked")
    }
}
