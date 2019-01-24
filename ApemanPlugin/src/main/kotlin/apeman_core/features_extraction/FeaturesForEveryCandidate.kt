package apeman_core.features_extraction

import com.intellij.analysis.AnalysisScope
import com.intellij.openapi.project.Project
import com.sixrr.metrics.metricModel.MetricsResult
import com.sixrr.metrics.utils.MethodUtils
import com.sixrr.stockmetrics.candidateMetrics.NumLiteralsCandidateMetric
import com.sixrr.stockmetrics.candidateMetrics.NumSwitchOperatorsCandidateMetric
import com.sixrr.stockmetrics.candidateMetrics.NumTernaryOperatorsCandidateMetric
import com.sixrr.stockmetrics.methodMetrics.NumLiteralsMetric
import com.sixrr.stockmetrics.methodMetrics.NumTernaryOperatorsMetric
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate


class FeaturesForEveryCandidate(
        private val project: Project,
        private val analysisScope: AnalysisScope,
        private val candidates: ArrayList<ExtractionCandidate>
) {
    private val metrics: MutableList<Metric> = arrayListOf()
    private var calcRunner: FeaturesCalculationRunner? = null

    init {
        declareMetrics()
    }

    private fun declareMetrics() {

        metrics.addAll(listOf(
                CandidateMetric("Num_Literal", NumLiteralsCandidateMetric(candidates)),
                CandidateMetric("Num_Conditional", NumTernaryOperatorsCandidateMetric(candidates)),
                CandidateMetric("Num_Switch", NumSwitchOperatorsCandidateMetric(candidates))
        ))

        val namesToMetrics = metrics.map {
            it.name to it as CandidateMetric
        }.toMap()

        metrics.addAll(listOf(
                ComplementMetric("CON_LITERAL", NumLiteralsMetric(), namesToMetrics["Num_Literal"]!!),
                ComplementMetric("CON_CONDITIONAL", NumTernaryOperatorsMetric(), namesToMetrics["Num_Conditional"]!!)
        ))
    }

    private fun calculate() {
        assert(metrics.isNotEmpty())

        calcRunner = FeaturesCalculationRunner(project, analysisScope, metrics)
        calcRunner!!.calculate()
    }

    fun getCandidatesWithFeatures(): List<CandidatesWithFeatures> {
        calculate()
        val candResults = calcRunner!!.resultsForCandidates!!
        val methodResults = calcRunner!!.resultsForMethods!!
        val candWithFeatures = arrayListOf<CandidatesWithFeatures>()

        for (cand in candidates) {
            val featureVector =  getFeatureVector(
                    cand, candResults, methodResults
            )
            candWithFeatures.add(CandidatesWithFeatures(cand, featureVector))
        }
        return candWithFeatures
    }

    private fun getFeatureVector(
            cand: ExtractionCandidate,
            candResults: MetricsResult,
            methodResults: MetricsResult
    ): List<Feature> {

        val featureVector = arrayListOf<Feature>()
        for (m in metrics) {
            val value = m.calculateResult(cand, candResults, methodResults)
            featureVector.add(Feature(m.name, value))
        }
        return featureVector
    }

//    private val gemsMetrics = mutableListOf<Metric>(
//
//            // method metrics
//            NumStatementsMetric(),
//            NumLocalVarsMetric(),
//            NumLiteralsMetric(),
//            NumTernaryOperatorsMetric(),
//            NumAssertsMetric(),
//            NumAssignmentsMetric(),
//            NumUsedTypesMetric(),
//            NumUsedPackagesMetric(),
//            NumMethodCallsMetric(),
//            NumIfMetric(),
//            NumLocalVarsAccessMetric(),
//
//            // candidate metrics
//            NumLiteralsCandidateMetric(candidates),
//            NumTernaryOperatorsCandidateMetric(candidates),
//            NumSwitchOperatorsCandidateMetric(candidates)
//    )
}
