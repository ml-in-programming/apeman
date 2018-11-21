import com.intellij.analysis.AnalysisScope
import com.intellij.codeInspection.GlobalInspectionContext
import com.intellij.codeInspection.ex.GlobalInspectionContextImpl
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.util.ProgressIndicatorBase
import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScopesCore
import com.sixrr.metrics.Metric
import com.sixrr.metrics.MetricCategory
import com.sixrr.metrics.metricModel.*
import com.sixrr.metrics.profile.MetricInstanceImpl
import com.sixrr.metrics.profile.MetricsProfile
import com.sixrr.metrics.profile.MetricsProfileImpl
import com.sixrr.stockmetrics.candidateMetrics.*
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate

class FeaturesForEveryCandidate(project: Project, var analysisScope: AnalysisScope,
                                val candidates: ArrayList<ExtractionCandidate>) {

    private val gemsMetrics = listOf<Metric>(

            com.sixrr.stockmetrics.methodMetrics.NumLocalVarsMetric(),
            com.sixrr.stockmetrics.methodMetrics.NumLiteralsMetric(),
            com.sixrr.stockmetrics.methodMetrics.NumIfMetric(),
            com.sixrr.stockmetrics.methodMetrics.NumAssertsMetric(),
            com.sixrr.stockmetrics.methodMetrics.NumAssignmentsMetric(),
            com.sixrr.stockmetrics.methodMetrics.NumUsedTypesMetric(),
            com.sixrr.stockmetrics.methodMetrics.NumUsedPackagesMetric(),
            com.sixrr.stockmetrics.methodMetrics.NumMethodCallsMetric(),
            com.sixrr.stockmetrics.methodMetrics.NumLocalVarsAccessMetric(),

            // candidates metrics
            NumLiteralsMetric(candidates)
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
