package apeman_core.features_extraction.metrics

import apeman_core.features_extraction.calculators.SuperBaseCalculator
import apeman_core.pipes.CandidateWithFeatures

class OptimizationMetric(val calculator: SuperBaseCalculator) : Metric(listOf(calculator)) {
    override fun fetchResult(candidates: List<CandidateWithFeatures>) {
        for (candidate in candidates) {
            calculator.results.resultForCandidate(candidate.candidate).forEach { (key, value) ->
                assert(candidate.features[key] == -1.0)
                candidate.features[key] = value
            }
        }
    }
}