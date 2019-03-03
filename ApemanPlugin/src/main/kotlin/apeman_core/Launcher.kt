package apeman_core

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.candidates_generation.CandidatesOfScope
import apeman_core.pipes.CandidateWithFeatures
import apeman_core.features_extraction.FeaturesForEveryCandidate
import apeman_core.grouping.GettingBestCandidates
import apeman_core.pipes.CandidatesWithFeaturesAndProba
import apeman_core.prediction.ModelProvider
import com.intellij.analysis.AnalysisScope
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiMethod
import java.util.logging.Logger

private val log = Logger.getLogger("Launcher")

class Launcher(
        private val project: Project,
        private val analysisScope: AnalysisScope,
        private val analysisMethods: List<PsiMethod> = arrayListOf()
) {
    private var candidatesOfScope: CandidatesOfScope? = null
    private var featuresOfEveryCandidate: FeaturesForEveryCandidate? = null
    private var model: ModelProvider? = null

    fun getCandidatesWithProba(): ArrayList<CandidatesWithFeaturesAndProba> {

        log.fine("scope has ${analysisScope.fileCount} files")

        if (analysisScope.fileCount == 0 && analysisMethods.isEmpty()) {
            log.info("return from scope")
            return arrayListOf()
        }

        log.info("generate candidates")
        val candidates = generateCandidates()

        log.info("calculate features")
        val candidatesWithFeatures = calculateFeatures(candidates)

        log.info("predict candidates")
        val candidatesWithProba = predictCandidates(candidatesWithFeatures)

        log.info("getting top candidates")
        val bestCandidates = gettingBestCandidates(candidatesWithProba)

        log.info("predicting success!")
        return ArrayList(bestCandidates)
    }

    private fun generateCandidates(): List<ExtractionCandidate> {
        candidatesOfScope = if (analysisMethods.isNotEmpty())
            CandidatesOfScope(project, analysisMethods)
        else
            CandidatesOfScope(project, analysisScope)

        return candidatesOfScope!!.getCandidates()
    }

    private fun calculateFeatures(candidates: List<ExtractionCandidate>): List<CandidateWithFeatures> {

        featuresOfEveryCandidate = FeaturesForEveryCandidate(
                project,
                analysisScope,
                ArrayList(candidates)
        )

        return featuresOfEveryCandidate!!.getCandidatesWithFeatures()
    }

    private fun predictCandidates(candidatesWithFeature: List<CandidateWithFeatures>)
            : List<CandidatesWithFeaturesAndProba> {
        model = ModelProvider(candidatesWithFeature)
        model!!.trainModel()
        return model!!.predictCandidates()
    }

    private fun gettingBestCandidates(candToProba: List<CandidatesWithFeaturesAndProba>)
            : List<CandidatesWithFeaturesAndProba> {
        val filter = GettingBestCandidates(ArrayList(candToProba))

        return filter.getTopKCandidates()
    }
}
