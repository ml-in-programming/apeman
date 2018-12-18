import com.intellij.analysis.AnalysisScope
import com.intellij.analysis.BaseAnalysisAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages

class LauncherAction : BaseAnalysisAction("check1", "check2") {

    override fun analyze(project: Project, scope: AnalysisScope) {

        if (scope.fileCount == 0) {
            return
        }

        val candidates = CandidatesOfScope(project, scope).candidates
        val features = FeaturesForEveryCandidate(project, scope, candidates)
        val results = features.results

        val model = ModelProvider()
        //model.

        var info = ""
        for ((candidate, metrics) in results) {
            info += "$candidate:\n"
            for ((metric, name) in metrics.zip(features.featureNames))
                info += "$name: $metric\n"
            info += "\n\n\n"
        }

        Messages.showInfoMessage(info, "checked")
    }
}
