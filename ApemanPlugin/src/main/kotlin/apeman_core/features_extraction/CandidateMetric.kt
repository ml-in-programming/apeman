package apeman_core.features_extraction

import apeman_core.features_extraction.calculators.BaseMetricsCalculator
import apeman_core.pipes.CandidateWithFeatures
import com.sixrr.metrics.metricModel.MetricsResult
import com.sixrr.metrics.profile.MetricInstance
import com.sixrr.metrics.profile.MetricInstanceImpl
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate

class CandidateMetric(
        override val name: String,
        override val metric: BaseMetricsCalculator
): Metric(name, metric) {

    override fun calculateResult(candidate: CandidateWithFeatures): Double {
        return candidate.
    }
}
