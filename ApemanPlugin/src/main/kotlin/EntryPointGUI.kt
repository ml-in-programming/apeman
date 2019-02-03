import apeman_core.Launcher
import apeman_core.prediction.CandidatesWithFeaturesAndProba
import com.intellij.analysis.AnalysisScope
import com.intellij.analysis.BaseAnalysisAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate
import java.util.logging.Logger

private val log = Logger.getLogger("mainAnalyzeClass")
private val topK = 5

fun analyzeScope(project: Project, scope: AnalysisScope): ArrayList<CandidatesWithFeaturesAndProba> {

    log.info("scope has ${scope.fileCount} files")

    if (scope.fileCount == 0) {
        log.warning("return from scope")
        return arrayListOf()
    }

    val launcher = Launcher(project, scope)
    val candidates = launcher.getCandidatesWithProba()
    val sortedCandidates = candidates.sortedBy { -it.probability }
    var info = ""
    for ((cand, features, proba) in sortedCandidates) {
        info += "\n\n$cand:\n proba = $proba\n\n"
        for ((name, value) in features) {
            info += "$name = $value\n"
        }
    }

    Messages.showInfoMessage(info, "checked")
    log.info("predicting success!")
    return candidates
}


class LauncherAction : BaseAnalysisAction("check1", "check2") {
    override fun analyze(project: Project, scope: AnalysisScope) {
        analyzeScope(project, scope)
    }
}
