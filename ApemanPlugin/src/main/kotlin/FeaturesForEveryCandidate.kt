import com.intellij.analysis.AnalysisScope
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.util.ProgressIndicatorBase
import com.intellij.openapi.project.Project
import com.sixrr.metrics.Metric
import com.sixrr.metrics.MetricCategory
import com.sixrr.metrics.metricModel.MetricsExecutionContextImpl
import com.sixrr.metrics.metricModel.MetricsResult
import com.sixrr.metrics.metricModel.MetricsRunImpl
import com.sixrr.metrics.metricModel.TimeStamp
import com.sixrr.metrics.profile.MetricInstance
import com.sixrr.metrics.profile.MetricInstanceImpl
import com.sixrr.metrics.profile.MetricsProfile
import com.sixrr.metrics.profile.MetricsProfileImpl
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
    val metricNames = mutableListOf<String>()
    private val candidateMetrics = mutableListOf<Metric>()

    private data class ComplementCountMetric(val methodMetric: Metric, val candidateMetric: Metric)
    private val complementMetrics = mutableListOf<ComplementCountMetric>()

    private val metricInstances = mutableListOf<MetricInstance>()

    private var resultsForMethods: MetricsResult? = null
    private var resultsForCandidates: MetricsResult? = null
    var results = HashMap<ExtractionCandidate, ArrayList<Double>>()

    init {
        declareMetrics()
        updateMetricInstances()
        calculateAsync()
        fetchResults()
    }

    private fun declareMetrics() {

        val candMetrics = mutableListOf(
                "Num_Literal" to NumLiteralsCandidateMetric(candidates),
                "Num_Conditional" to NumTernaryOperatorsCandidateMetric(candidates),
                "Num_Switch" to NumSwitchOperatorsCandidateMetric(candidates)
        )

        candMetrics.forEach { declareCandidateMetric(it.first, it.second) }
        val namesToMetrics = candMetrics.toMap()

        declareComplementMetric("CON_LITERAL",  NumLiteralsMetric(), namesToMetrics["Num_Literal"]!!)
        declareComplementMetric("CON_CONDITIONAL", NumTernaryOperatorsMetric(), namesToMetrics["Num_Conditional"]!!)
    }


    private fun declareCandidateMetric(metricName: String, metric: Metric) {
        metricNames.add(metricName)
        candidateMetrics.add(metric)
    }

    private fun declareComplementMetric(metricName: String, methodMetric: Metric, candidateMetric: Metric) {
        metricNames.add(metricName)
        complementMetrics.add(ComplementCountMetric(methodMetric, candidateMetric))
    }

    private fun updateMetricInstances() {
        for (metric in candidateMetrics)
            metricInstances.add(createMetricInstance(metric))

        for (compMetric in complementMetrics)
            metricInstances.add(createMetricInstance(compMetric.methodMetric))
    }

    private fun createMetricInstance(metric: Metric) = MetricInstanceImpl(metric).apply { isEnabled = true }

    private fun calculateAsync() {

        val metricsProfile: MetricsProfile = MetricsProfileImpl("Gems metrics", metricInstances)
        val metricsRun = MetricsRunImpl()
        val metricsExecutionContext = MetricsExecutionContextImpl(project, analysisScope)

        ProgressManager.getInstance().runProcess({

            metricsRun.profileName = metricsProfile.name
            metricsRun.timestamp = TimeStamp()
            metricsRun.context = analysisScope

            metricsExecutionContext.calculateMetrics(metricsProfile, metricsRun)
            resultsForMethods = metricsRun.getResultsForCategory(MetricCategory.Method)
            resultsForCandidates = metricsRun.getResultsForCategory(MetricCategory.ExtractionCandidate)

        }, ProgressIndicatorBase ())
    }

    private fun fetchResults() {
        for (candidate in candidates) {
            results[candidate] = getFeatureVector(candidate)
        }
    }

    private fun getFeatureVector(candidate: ExtractionCandidate): ArrayList<Double> {

        val candidateStr = candidate.toString()
        val featureVector = ArrayList<Double>()

        for (metric in candidateMetrics) {
            val feature = resultsForCandidates!!.getValueForMetric(metric, candidateStr)!!
            featureVector.add(feature)
        }

        val methodSignature = MethodUtils.calculateSignature(candidate.sourceMethod)!!

        for (complement in complementMetrics) {

            val metricFeature = resultsForMethods!!.getValueForMetric(complement.methodMetric, methodSignature)!!
            val candidateFeature = resultsForCandidates!!.getValueForMetric(complement.candidateMetric, candidateStr)!!
            featureVector.add(metricFeature - candidateFeature)
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
