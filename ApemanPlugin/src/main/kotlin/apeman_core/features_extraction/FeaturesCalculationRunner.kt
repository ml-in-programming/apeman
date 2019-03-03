package apeman_core.features_extraction

import apeman_core.features_extraction.metrics.Metric
import com.intellij.analysis.AnalysisScope
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.util.ProgressIndicatorBase
import com.intellij.openapi.project.Project
import com.sixrr.metrics.MetricCategory
import com.sixrr.metrics.metricModel.MetricsExecutionContextImpl
import com.sixrr.metrics.metricModel.MetricsResult
import com.sixrr.metrics.metricModel.MetricsRunImpl
import com.sixrr.metrics.metricModel.TimeStamp
import com.sixrr.metrics.profile.MetricInstance
import com.sixrr.metrics.profile.MetricsProfile
import com.sixrr.metrics.profile.MetricsProfileImpl

class FeaturesCalculationRunner(
        private val project: Project,
        private val analysisScope: AnalysisScope,
        metrics: List<Metric>
) {
    private val PROFILE_NAME = "Gems metrics"
    private val metricInstances = ArrayList<MetricInstance>()

    var resultsForMethods: MetricsResult? = null
    var resultsForCandidates: MetricsResult? = null

    init {
        metricInstances.addAll(
                metrics.flatMap { it.createMetricInstance() }
        )
    }

    fun calculate() {

        val metricsProfile: MetricsProfile = MetricsProfileImpl(PROFILE_NAME, metricInstances)
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
}