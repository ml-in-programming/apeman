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

        var info = ""
        val measuredObjects = features.resultsForMethods?.measuredObjects ?: return
        val metrics = features.resultsForMethods?.metrics ?: return

        val measuredCandidates = features.resultsForCandidates?.measuredObjects ?: return
        val metricsCandidates = features.resultsForCandidates?.metrics ?: return

        for (obj in measuredObjects) {
            info += "$obj:\n"

            for (metric in metrics) {
                val results = features.resultsForMethods?.getValueForMetric(metric, obj) ?: continue
                info += "${metric.displayName}: $results\n"
            }

            info += "\n\n"
        }

        for (obj in measuredCandidates) {
            info += "$obj:\n"

            for (metric in metricsCandidates) {
                val results = features.resultsForCandidates?.getValueForMetric(metric, obj) ?: continue
                info += "${metric.displayName}: $results\n"
            }

            info += "\n\n"
        }

        Messages.showInfoMessage(info, "checked")
    }
}
