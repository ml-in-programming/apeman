package apeman_core.features_extraction

import com.sixrr.metrics.metricModel.MetricsResult
import com.sixrr.metrics.profile.MetricInstance
import com.sixrr.metrics.profile.MetricInstanceImpl
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate

class CandidateMetric(
        override val name: String,
        override val metric: com.sixrr.metrics.Metric
): Metric(name, metric) {

    override fun calculateResult(
            candidate: ExtractionCandidate,
            resultsCandidate: MetricsResult,
            resultsMethod: MetricsResult): Double {
        return resultsCandidate.getValueForMetric(metric, candidate.id)!!
    }
}
