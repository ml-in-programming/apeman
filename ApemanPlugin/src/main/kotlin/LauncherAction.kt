import com.intellij.analysis.AnalysisScope
import com.intellij.analysis.BaseAnalysisAction
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.fileChooser.tree.FileNodeVisitor
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modifyModules
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiManager

class LauncherAction : BaseAnalysisAction("check1", "check2") {

    override fun analyze(project: Project, scope: AnalysisScope) {

        if (scope.fileCount == 0) {
            return
        }

        val features = FeaturesForEveryCandidate(project, scope)

        var info = ""
        val measuredObjects = features.resultsForMethods?.measuredObjects ?: return
        val metrics = features.resultsForMethods?.metrics ?: return

        for (obj in measuredObjects) {
            info += "$obj:\n"

            for (metric in metrics) {
                val results = features.resultsForMethods?.getValueForMetric(metric, obj)
                info += "${metric.displayName}: $results\n"
            }

            info += "\n\n"
        }

        Messages.showInfoMessage(info, "checked")
    }
}
