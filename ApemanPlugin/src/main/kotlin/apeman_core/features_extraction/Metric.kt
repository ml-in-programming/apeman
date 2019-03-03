package apeman_core.features_extraction

import apeman_core.features_extraction.calculators.BaseMetricsCalculator
import apeman_core.pipes.CandidateWithFeatures

abstract class Metric(
        open val name: String,
        open val metric: BaseMetricsCalculator
) {
    abstract fun calculateResult(candidate: CandidateWithFeatures): Double
}
