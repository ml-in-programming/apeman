package apeman_core.features_extraction.metrics

import apeman_core.features_extraction.calculators.NotOptimizedCalculator
import apeman_core.base_entities.CandidateWithFeatures

class CandidateMetric(metric: NotOptimizedCalculator
): Metric(listOf(metric)) {

    override fun fetchResult(candidates: List<CandidateWithFeatures>) {
        assert(calculators.count() == 1)

        for (candidate in candidates) {
            calculators[0].results.resultForCandidate(candidate.candidate)
                    .forEach { (feat, value) ->
                assert(candidate.features[feat] == -1.0)
                candidate.features[feat] = value
            }
        }
    }
}
