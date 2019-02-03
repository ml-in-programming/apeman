package apeman_core.features_extraction

import com.sixrr.metrics.metricModel.MetricsResult
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate

class MaxFrom2Metric(
        override val name: String,
        override val metric: com.sixrr.metrics.Metric,
        private val metric2: com.sixrr.metrics.Metric
): Metric(name, metric) {
    override fun calculateResult(
            candidate: ExtractionCandidate,
            resultsCandidate: MetricsResult,
            resultsMethod: MetricsResult
    ): Double {
        val candStr = candidate.toString()
        val res1 = resultsCandidate.getValueForMetric(metric, candStr)!!
        val res2 = resultsCandidate.getValueForMetric(metric2, candStr)!!

        return Math.max(res1, res2)
    }
}
