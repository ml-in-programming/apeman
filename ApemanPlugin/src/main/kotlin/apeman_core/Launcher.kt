package apeman_core

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.candidates_generation.CandidatesOfAnalysisMethods
import apeman_core.candidates_generation.CustomCandidates
import apeman_core.base_entities.CandidateWithFeatures
import apeman_core.features_extraction.FeaturesForEveryCandidate
import apeman_core.grouping.GettingBestCandidates
import apeman_core.base_entities.CandidatesWithFeaturesAndProba
import apeman_core.prediction.SciKitModelProvider
import apeman_core.prediction.TensorFlowModelProvider
import apeman_core.utils.scopeToTopMethods
import com.intellij.analysis.AnalysisScope
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.progress.EmptyProgressIndicator
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressIndicatorProvider
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiMethod
import java.util.logging.Logger

private val log = Logger.getGlobal()

class Launcher(
        analysisScope: AnalysisScope? = null,
        private var analysisMethods: List<PsiMethod>? = null,
        private val analysisCandidates: List<Pair<TextRange, PsiFile>>? = null
) {

    var predictedCandidates: List<CandidatesWithFeaturesAndProba>? = null

    init {
        assert((analysisScope != null) xor (analysisMethods != null) xor (analysisCandidates != null))

        if (analysisScope != null) {
            analysisMethods = scopeToTopMethods(analysisScope)
        }

        assert((analysisMethods != null) xor (analysisCandidates != null))
    }

    fun calculateCandidatesWithProbaAsync(project: Project): List<CandidatesWithFeaturesAndProba> {
        return WriteAction.compute<List<CandidatesWithFeaturesAndProba>, Exception> {
//            val indicator = ProgressIndicatorProvider.getGlobalProgressIndicator()!!
            calculateCandidatesWithProba(EmptyProgressIndicator())
        }

//        val task = object : Task.WithResult<List<CandidatesWithFeaturesAndProba>, Exception>(project, "Search For Refactorings", true) {
//            override fun compute(indicator: ProgressIndicator) = calculateCandidatesWithProba(indicator)
//        }
//        task.queue()
//        return task.result
    }

    fun calculateCandidatesWithProba(indicator: ProgressIndicator): List<CandidatesWithFeaturesAndProba> {

        try {
            log.info("generate candidates")
            indicator.text = "generate candidates"
            val candidates = generateCandidates(indicator)

            indicator.fraction = 0.5

            log.info("calculate features")
            indicator.text = "calculate features"
            val candidatesWithFeatures = calculateFeatures(candidates)

            log.info("predict candidates")
            indicator.text = "predict candidates"
            val candidatesWithProba = predictCandidates(candidatesWithFeatures)

            log.info("getting top candidates")
            indicator.text = "getting top candidates"
            val bestCandidates = gettingBestCandidates(candidatesWithProba)

            log.info("predicting success!")
            predictedCandidates = bestCandidates

        } catch (e: Error) {
            val log = Logger.getLogger("error")
            log.severe("Error: $e")
            return listOf()
        } catch (e: Exception) {
            val log = Logger.getLogger("exception")
            log.severe("Exception: $e")
            return listOf()
        }
        return predictedCandidates!!
    }

    private fun generateCandidates(indicator: ProgressIndicator): List<ExtractionCandidate> {
        return if (analysisMethods != null) {
            CandidatesOfAnalysisMethods(analysisMethods!!, indicator).getCandidates()
        } else {
            assert(analysisCandidates != null)
            CustomCandidates(analysisCandidates!!).getCandidates()
        }
    }

    private fun calculateFeatures(candidates: List<ExtractionCandidate>): List<CandidateWithFeatures> {
        val featuresOfEveryCandidate = FeaturesForEveryCandidate(candidates)
        return featuresOfEveryCandidate.getCandidatesWithFeatures()
    }

    private fun predictCandidates(candidatesWithFeature: List<CandidateWithFeatures>)
            : List<CandidatesWithFeaturesAndProba> {
//        val model = SciKitModelProvider(candidatesWithFeature)
        val model = TensorFlowModelProvider(candidatesWithFeature)
        return model.predictCandidates()
    }

    private fun gettingBestCandidates(candToProba: List<CandidatesWithFeaturesAndProba>)
            : List<CandidatesWithFeaturesAndProba> {
        val filter = GettingBestCandidates(ArrayList(candToProba))

        return filter.getTopKCandidates()
    }
}
