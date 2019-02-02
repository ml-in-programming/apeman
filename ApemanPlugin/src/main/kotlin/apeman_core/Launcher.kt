package apeman_core

import apeman_core.candidates_generation.CandidatesOfScope
import apeman_core.features_extraction.CandidateWithFeatures
import apeman_core.features_extraction.FeaturesForEveryCandidate
import apeman_core.prediction.CandidatesWithFeaturesAndProba
import apeman_core.prediction.ModelProvider
import com.intellij.analysis.AnalysisScope
import com.intellij.openapi.project.Project
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate

class Launcher(
        private val project: Project,
        private val analysisScope: AnalysisScope
) {
    private var candidatesOfScope: CandidatesOfScope? = null
    private var featuresOfEveryCandidate: FeaturesForEveryCandidate? = null
    private var model: ModelProvider? = null

    fun getCandidatesWithProba(): ArrayList<CandidateWithFeatures> {
        val candidates = generateCandidates()
        val candidatesWithFeatures = calculateFeatures(candidates)


        return ArrayList(candidatesWithFeatures)

    }

    private fun generateCandidates(): List<ExtractionCandidate> {
        candidatesOfScope = CandidatesOfScope(project, analysisScope)
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
        model = ModelProvider()
        model!!.trainModel()
    }
}
