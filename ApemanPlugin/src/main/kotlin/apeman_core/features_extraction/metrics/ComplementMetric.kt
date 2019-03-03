package apeman_core.features_extraction.metrics

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
        val candId = candidate.id

        val methodValue = resultsMethod.getValueForMetric(metric, methodSign)!!
        val candidateValue = resultsCandidate.getValueForMetric(candidateMetric.metric, candId)!!

        return methodValue - candidateValue
    }
}