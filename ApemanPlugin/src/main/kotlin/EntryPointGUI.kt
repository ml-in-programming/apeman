import apeman_core.prediction.ModelProvider
import com.intellij.analysis.AnalysisScope
import com.intellij.analysis.BaseAnalysisAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate
import java.util.logging.Logger

private val log = Logger.getLogger("mainAnalyzeClass")
private val topK = 5

fun analyzeScope(project: Project, scope: AnalysisScope): ArrayList<Pair<ExtractionCandidate, Double>> {

    log.info("scope has ${scope.fileCount} files")

    if (scope.fileCount == 0) {
        log.warning("return from scope")
        return arrayListOf()
    }

    log.info("get candidates")
    val candidates = CandidatesOfScope(project, scope).candidates

    log.info("get features")
    val features = FeaturesForEveryCandidate(project, scope, candidates)
    val candToFeatures = features.results
    val featureNames = features.featureNames

    log.info("train model")
    val model = ModelProvider()
    model.trainModel(featureNames)

    log.info("predict candidates")
    val proba = model.predictCandidates(candToFeatures, featureNames)

    var candToProba = ArrayList<Pair<ExtractionCandidate, Double>>()
    candToProba = candToFeatures.keys.zip(proba).sortedBy { -it.second }.toCollection(candToProba)

    var info = ""
    for ((cand, proba) in candToProba) {
        info += "\n\n$cand:\n proba = $proba\n\n"
        for ((metric, featureName) in candToFeatures[cand]!!.zip(featureNames)) {
            info += "$featureName = $metric\n"
        }
    }

    Messages.showInfoMessage(info, "checked")
    log.info("predicting success!")
    return candToProba
}


class LauncherAction : BaseAnalysisAction("check1", "check2") {
    override fun analyze(project: Project, scope: AnalysisScope) {
        analyzeScope(project, scope)
    }
}
