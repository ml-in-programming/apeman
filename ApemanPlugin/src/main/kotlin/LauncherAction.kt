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
        val candToFeatures = features.results
        val featureNames = features.featureNames

        val model = ModelProvider()
        model.trainModel(featureNames)
        val proba = model.predictCandidates(candToFeatures, featureNames)

        val probaToCand = proba.zip(candToFeatures.keys).sortedBy { -it.first }

        var info = ""
        for ((proba, cand) in probaToCand) {
            info += "\n\n$cand:\n proba = $proba\n\n"
            for ((metric, featureName) in candToFeatures[cand]!!.zip(featureNames)) {
                info += "$featureName = $metric\n"
            }
        }

        Messages.showInfoMessage(info, "checked")
    }
}
