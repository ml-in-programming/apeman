package apeman_core.features_extraction.metrics

import apeman_core.features_extraction.calculators.BaseMetricsCalculator
import apeman_core.pipes.CandidateWithFeatures

class CandidateMetric(
        override val name: String,
        override val metric: BaseMetricsCalculator
): Metric(name, metric) {

    override fun calculateResult(candidate: CandidateWithFeatures) {
        metric.features.forEach { candidate.features[it] = metric.results[it][0] }
    }
}
