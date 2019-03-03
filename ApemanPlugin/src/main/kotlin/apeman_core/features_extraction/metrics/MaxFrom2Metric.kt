package apeman_core.features_extraction.metrics

import com.sixrr.metrics.metricModel.MetricsResult
import com.sixrr.metrics.profile.MetricInstance
import com.sixrr.metrics.profile.MetricInstanceImpl
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate

class MaxFrom2Metric(
        override val name: String,
        override val metric: com.sixrr.metrics.Metric,
        private val metric2: com.sixrr.metrics.Metric
): Metric(name, metric) {

    override fun createMetricInstance(): List<MetricInstance> {
        return listOf(metric, metric2)
                .map {
                    MetricInstanceImpl(it).apply {
                        isEnabled = true
                    }
                }.toList()
    }

    override fun calculateResult(
            candidate: ExtractionCandidate,
            resultsCandidate: MetricsResult,
            resultsMethod: MetricsResult
    ): Double {
        val candId = candidate.id
        val res1 = resultsCandidate.getValueForMetric(metric, candId)!!
        val res2 = resultsCandidate.getValueForMetric(metric2, candId)!!

        return Math.max(res1, res2)
    }
}
