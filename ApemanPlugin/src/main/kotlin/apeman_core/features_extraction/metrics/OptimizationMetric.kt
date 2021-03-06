package apeman_core.features_extraction.metrics

import apeman_core.features_extraction.calculators.BaseCalculator
import apeman_core.base_entities.CandidateWithFeatures

class OptimizationMetric(val calculator: BaseCalculator) : Metric(listOf(calculator)) {
    override fun fetchResult(candidates: List<CandidateWithFeatures>) {
        for (candidate in candidates) {
            calculator.results.resultForCandidate(candidate.candidate).forEach { (key, value) ->
                assert(candidate.features[key] == -1.0)
                candidate.features[key] = value
            }
        }
    }
}
