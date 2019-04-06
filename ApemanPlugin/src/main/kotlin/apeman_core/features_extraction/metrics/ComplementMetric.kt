package apeman_core.features_extraction.metrics

import apeman_core.features_extraction.calculators.BaseMetricsCalculator
import apeman_core.base_entities.CandidateWithFeatures
import kotlin.math.abs

class ComplementMetric(
        override val calculators: List<BaseMetricsCalculator>

): Metric(calculators) {

    override fun fetchResult(candidates: List<CandidateWithFeatures>) {
        assert(calculators.count() == 2)

        for (candidate in candidates) {

            val candResults = calculators[1].results.resultForCandidate(candidate.candidate)
            val methodResults = calculators[0].results.resultForCandidate(candidate.candidate)

            assert(candResults.mapKeys { it.key.complementFeature() }.keys == methodResults.keys)

            methodResults.forEach { (feat, value) ->
                assert(candidate.features[feat] == -1.0)
                val complementFeature = feat.complementFeature()
                val complementValue = candResults[complementFeature] ?: 0.0

                assert(value >= complementValue)
                candidate.features[feat] = abs(value - complementValue)
            }
        }
    }
}
