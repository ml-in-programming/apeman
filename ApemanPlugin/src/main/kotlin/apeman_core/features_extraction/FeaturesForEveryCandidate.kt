package apeman_core.features_extraction

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.base_entities.CandidateWithFeatures
import apeman_core.features_extraction.calculators.candidate.*
import apeman_core.features_extraction.calculators.method.*
import apeman_core.features_extraction.calculators.optimized.*
import apeman_core.features_extraction.metrics.CandidateMetric
import apeman_core.features_extraction.metrics.ComplementMetric
import apeman_core.features_extraction.metrics.Metric
import apeman_core.features_extraction.metrics.OptimizationMetric

class FeaturesForEveryCandidate(
        private val candidates: List<ExtractionCandidate>
) {
    private val candidatesWithFeatures = ArrayList<CandidateWithFeatures>()
    private val metrics: MutableList<Metric> = arrayListOf()
    private var calcRunner: FeaturesCalculationRunner? = null

    init {
        declareMetrics()
    }

    // wrong con package (6 vs our 10)
    // wrong con typed elements (10 vs our 25)

    // wrong num type access (33 vs our 21)
    // wrong num typed elements (273 vs our 578)
    // wrong num packages (379 vs our 223)

    // wrong var access cohesion (0.407 vs our 0.555)
    // wrong var access cohesion 2 (0.389 vs our 0.037)

    // wrong field access all (1, 0, 0.055, 0 vs our 1, 1, 0.07, 0.018)

    // wrong type access cp2, ch, ch2 (0, 0.555, 0 vs our 1, 0.1, 0.1)
    // wrong typed elements ch (0.4 vs our 0.037)
    // wrong package all (0.87, 0.24 vs our 0.018, 0.055)

    private fun declareMetrics() {

        val candidateCalculators = listOf(
                NumTernaryOperatorsCandidates(candidates),
                NumIfCandidate(candidates),
                NumAssignmentsCandidate(candidates),
                NumSwitchOperatorsCandidates(candidates),
                NumLocalVarsCandidate(candidates),
                NumAssertCandidate(candidates),
                LocCandidate(candidates),

                RatioLocCandidate(candidates),
                MeanNestingDepthCandidate(candidates),
                MeanNestingDepthMethod(candidates),
                NumCommentsCandidate(candidates)
        )

        val complementCalculators = listOf(
                NumTernaryMethod(candidates),
                NumIfMethod(candidates),
                NumAssignmentsMethod(candidates),
                NumSwitchMethod(candidates),
                NumLocalVarsMethod(candidates),
                NumAssertMethod(candidates),
                NumLOCMethod(candidates)
        )

        val candidateMetrics = candidateCalculators.map { CandidateMetric(it) }

        val complementMetrics = complementCalculators
                .mapIndexed { i, calc -> ComplementMetric(listOf(calc, candidateCalculators[i])) }

        val optimizedMetrics = listOf(
                FieldAccessCalculator(candidates),
                InvocationCalculator(candidates),
                PackageAccessCalculator(candidates),
                TypeAccessCalculator(candidates),
                VarAccessCalculator(candidates),
                TypedElementsCalculator(candidates)
        ).map { OptimizationMetric(it) }

        metrics.addAll(listOf(
                candidateMetrics,
                complementMetrics,
                optimizedMetrics
        ).flatten())
    }

    private fun calculate() {
        assert(metrics.isNotEmpty())
        calcRunner = FeaturesCalculationRunner(candidates, metrics)
        calcRunner!!.calculate()

        candidates.forEach { cand ->
            val candWithFeat = CandidateWithFeatures(cand)
            FeatureType.values().forEach { candWithFeat.features[it] = -1.0 }
            candidatesWithFeatures.add(candWithFeat)
        }
        assert(candidatesWithFeatures.all { it.features.count() == FeatureType.values().count() })
        assert(candidatesWithFeatures.all { it.features.all { it.value == -1.0 } })

        metrics.forEach { m -> m.fetchResult(candidatesWithFeatures) }
        assert(candidatesWithFeatures.all { it.features.all { (it.value != -1.0) xor (it.key.name.endsWith("_LITERAL"))} })

    }

    fun getCandidatesWithFeatures(): List<CandidateWithFeatures> {
        calculate()
        return candidatesWithFeatures
    }
}
