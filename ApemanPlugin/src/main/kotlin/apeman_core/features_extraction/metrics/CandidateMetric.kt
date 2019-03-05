package apeman_core.features_extraction.metrics

import apeman_core.features_extraction.calculators.BaseMetricsCalculator
import apeman_core.pipes.CandidateWithFeatures

class CandidateMetric(metric: BaseMetricsCalculator
): Metric(listOf(metric)) {

    override fun fetchResult(candidate: CandidateWithFeatures) {
        assert(metrics.count() == 1)

        metrics[0].results.resultForCandidate(candidate.candidate).forEach { (feat, value) ->
            assert(candidate.features[feat] == -1.0)
            candidate.features[feat] = value
        }
    }
}
