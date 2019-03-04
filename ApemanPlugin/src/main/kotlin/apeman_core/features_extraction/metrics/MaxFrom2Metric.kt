package apeman_core.features_extraction.metrics

import apeman_core.features_extraction.calculators.BaseMetricsCalculator
import apeman_core.pipes.CandidateWithFeatures

class MaxFrom2Metric(
        override val metrics: List<BaseMetricsCalculator>
): Metric(metrics) {

    override fun fetchResult(candidate: CandidateWithFeatures) {
        assert(metrics.count() == 2)
        val results2 = metrics[1].results.resultForCandidate(candidate)

        metrics[0].results.resultForCandidate(candidate).forEach { (feat, value) ->
            assert(candidate.features[feat] == -1.0)

            val value2 = results2[feat]!!
            candidate.features[feat] = maxOf(value, value2)
        }
    }
}
