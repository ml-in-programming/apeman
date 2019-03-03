package apeman_core.features_extraction.metrics

import com.sixrr.metrics.metricModel.MetricsResult
import com.sixrr.metrics.utils.MethodUtils
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate

class MockMethodMetric(
        override val name: String,
        override val metric: com.sixrr.metrics.Metric
): Metric(name, metric) {
    override fun calculateResult(candidate: ExtractionCandidate, resultsCandidate: MetricsResult, resultsMethod: MetricsResult): Double {
        val methodSign = MethodUtils.calculateSignature(candidate.sourceMethod)!!
        val methodValue = resultsMethod.getValueForMetric(metric, methodSign)!!
        return methodValue
    }
}