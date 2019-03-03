package apeman_core.features_extraction.metrics

import apeman_core.features_extraction.calculators.BaseMetricsCalculator
import apeman_core.pipes.CandidateWithFeatures
import kotlin.math.abs

class ComplementMetric(
        override val metrics: List<BaseMetricsCalculator>

): Metric(metrics) {

    override fun fetchResult(candidate: CandidateWithFeatures) {
        assert(metrics.count() == 2)

        val candResults = metrics[1].results.resultForCandidate(candidate)
        val methodResults = metrics[0].results.resultForCandidate(candidate)

        methodResults.forEach { (feat, value) ->
            assert(candidate.features[feat] == -1.0)
            val complementFeature = feat.complementFeature()
            val complementValue = candResults[complementFeature] ?: 0.0

            candidate.features[feat] = abs(value - complementValue)
        }
    }
}
