import com.intellij.analysis.AnalysisScope
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.util.ProgressIndicatorBase
import com.intellij.openapi.project.Project
import com.sixrr.metrics.Metric
import com.sixrr.metrics.MetricCategory
import com.sixrr.metrics.metricModel.*
import com.sixrr.metrics.profile.MetricInstanceImpl
import com.sixrr.metrics.profile.MetricsProfile
import com.sixrr.metrics.profile.MetricsProfileImpl
import com.sixrr.stockmetrics.candidateMetrics.*
import com.sixrr.stockmetrics.methodMetrics.*
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate

class FeaturesForEveryCandidate(project: Project, var analysisScope: AnalysisScope,
                                val candidates: ArrayList<ExtractionCandidate>) {

    private val gemsMetrics = listOf<Metric>(

            // method metrics
            NumStatementsMetric(),
            NumLocalVarsMetric(),
            NumLiteralsMetric(),
            NumTernaryOperatorsMetric(),
            NumAssertsMetric(),
            NumAssignmentsMetric(),
            NumUsedTypesMetric(),
            NumUsedPackagesMetric(),
            NumMethodCallsMetric(),
            NumIfMetric(),
            NumLocalVarsAccessMetric(),

            // candidate metrics
            NumLiteralsCandidateMetric(candidates),
            NumTernaryOperatorsCandidateMetric(candidates),
            NumSwitchOperatorsCandidateMetric(candidates)
    )

    private val metricsProfile: MetricsProfile = MetricsProfileImpl("Gems metrics",
            gemsMetrics.map {
                MetricInstanceImpl(it).apply {
                    isEnabled = true
                }
            })

    private var metricsRun = MetricsRunImpl()
    private val metricsExecutionContext = MetricsExecutionContextImpl(project, analysisScope)

    public var resultsForMethods: MetricsResult? = null
    public var resultsForCandidates: MetricsResult? = null

    init {
        ProgressManager.getInstance().runProcess({

            metricsRun.profileName = metricsProfile.name
            metricsRun.timestamp = TimeStamp()
            metricsRun.context = analysisScope

            metricsExecutionContext.calculateMetrics(metricsProfile, metricsRun)
            resultsForMethods = metricsRun.getResultsForCategory(MetricCategory.Method)
            resultsForCandidates = metricsRun.getResultsForCategory(MetricCategory.ExtractionCandidate)

        }, ProgressIndicatorBase ())
    }
}
