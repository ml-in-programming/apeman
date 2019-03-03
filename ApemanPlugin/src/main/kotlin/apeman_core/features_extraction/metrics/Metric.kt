package apeman_core.features_extraction.metrics

import apeman_core.features_extraction.calculators.BaseMetricsCalculator
import apeman_core.pipes.CandidateWithFeatures

abstract class Metric(
        open val metrics: List<BaseMetricsCalculator>
) {
    abstract fun fetchResult(candidate: CandidateWithFeatures)
}
