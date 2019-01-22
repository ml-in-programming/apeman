package apeman_core.features_extraction

import com.sixrr.metrics.metricModel.MetricsResult
import com.sixrr.metrics.utils.MethodUtils
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate

class ComplementMetric(
        override val name: String,
        override val metric: com.sixrr.metrics.Metric,
        private val candidateMetric: CandidateMetric

): Metric(name, metric) {

    override fun calculateResult(
            candidate: ExtractionCandidate,
            resultsCandidate: MetricsResult,
            resultsMethod: MetricsResult
    ): Double {

        val methodSign = MethodUtils.calculateSignature(candidate.sourceMethod)!!
        val candStr = candidate.toString()

        val methodValue = resultsMethod.getValueForMetric(metric, methodSign)!!
        val candidateValue = resultsMethod.getValueForMetric(candidateMetric.metric, candStr)!!

        return methodValue - candidateValue
    }
}