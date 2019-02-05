package apeman_core.features_extraction

import com.sixrr.metrics.metricModel.MetricsResult
import com.sixrr.metrics.profile.MetricInstance
import com.sixrr.metrics.profile.MetricInstanceImpl
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate

abstract class Metric(
        open val name: String,
        open val metric: com.sixrr.metrics.Metric
) {

    open fun createMetricInstance(): List<MetricInstance> {
        return listOf(MetricInstanceImpl(metric).apply {
            isEnabled = true
        })
    }

    abstract fun calculateResult(
            candidate: ExtractionCandidate,
            resultsCandidate: MetricsResult,
            resultsMethod: MetricsResult
    ): Double
}
