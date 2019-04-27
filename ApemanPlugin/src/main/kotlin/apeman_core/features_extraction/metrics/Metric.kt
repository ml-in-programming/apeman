package apeman_core.features_extraction.metrics

import apeman_core.features_extraction.calculators.BaseCalculator
import apeman_core.base_entities.CandidateWithFeatures

abstract class Metric(
        open val calculators: List<BaseCalculator>
) {
    abstract fun fetchResult(candidates: List<CandidateWithFeatures>)
}
