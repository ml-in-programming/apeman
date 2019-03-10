package apeman_core.features_extraction

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.pipes.CandidateWithFeatures
import apeman_core.features_extraction.calculators.candidate.*
import apeman_core.features_extraction.calculators.method.*
import apeman_core.features_extraction.metrics.CandidateMetric
import apeman_core.features_extraction.metrics.ComplementMetric
import apeman_core.features_extraction.metrics.MaxFrom2Metric
import apeman_core.features_extraction.metrics.Metric
import com.intellij.psi.*

class FeaturesForEveryCandidate(
        private val candidates: List<ExtractionCandidate>
) {
    private val candidatesWithFeatures = ArrayList<CandidateWithFeatures>()
    private val metrics: MutableList<Metric> = arrayListOf()
    private var calcRunner: FeaturesCalculationRunner? = null

    init {
        declareMetrics()
    }

    // как будто бы type access и type elements поменялись местами. Еще там должно быть не set, а просто
    // и package и type access это количество, а не сеты

    // wrong con package (6 vs our 44)
    // wrong con typed elements (10 vs our 25)

    // !wrong num literal (1 vs our 24)

    // !wrong num invocation (114 vs our 131)
    // wrong num type access (33 vs our 21)
    // wrong num typed elements (273 vs our 578)
    // wrong num packages (379 vs our 900)

    // wrong var access cohesion (0.407 vs our 0.555)
    // wrong var access cohesion 2 (0.389 vs our 0.037)
    // wrong field access all (1, 0, 0.055, 0 vs our 0)
    // wrong invocation cohesion (0.07 vs our 0.018)
    // wrong type access cp, ch (1, 0, 0.555, 0 vs our 1, 1, 0.1, 0.1)
    // wrong typed elements ch (0.4 vs our 0.037)
    // wrong package all (0.87, 0.24 vs our 0.018, 0.055)

    private fun declareMetrics() {

        val candidateCalculators = listOf(
                NumLiteralsCandidateCalculator(candidates),
                NumTernaryOperatorsCandidatesCalculator(candidates),
                NumTypedElementsCandidateCalculator(candidates),
                NumInvocationsCandidateCalculator(candidates),
                NumIfCandidateCalculator(candidates),
                NumAssignmentsCandidateCalculator(candidates),
                NumSwitchOperatorsCandidatesCalculator(candidates),
                NumLocalVarsCandidateCalculator(candidates),
                NumFieldAccessCandidateCalculator(candidates),
                NumVarsAccessCandidateCalculator(candidates),
                NumTypeAccessCandidateCalculator(candidates),
                NumPackageAccessesCandidateCalculator(candidates),
                LocCandidateCalculator(candidates),
                RatioLocCandidateCalculator(candidates),

                AbstractCouplingCohesionCandidateCalculator(candidates, FeatureType.VAR_ACCESS_COUPLING, true, true, PsiVariable::class.java),
                AbstractCouplingCohesionCandidateCalculator(candidates, FeatureType.VAR_ACCESS_COUPLING_2, true, false, PsiVariable::class.java),
                AbstractCouplingCohesionCandidateCalculator(candidates, FeatureType.VAR_ACCESS_COHESION, false, true, PsiVariable::class.java),
                AbstractCouplingCohesionCandidateCalculator(candidates, FeatureType.VAR_ACCESS_COHESION_2, false, false, PsiVariable::class.java),

                AbstractCouplingCohesionCandidateCalculator(candidates, FeatureType.FIELD_ACCESS_COUPLING, true, true, PsiField::class.java),
                AbstractCouplingCohesionCandidateCalculator(candidates, FeatureType.FIELD_ACCESS_COUPLING_2, true, false, PsiField::class.java),
                AbstractCouplingCohesionCandidateCalculator(candidates, FeatureType.FIELD_ACCESS_COHESION, false, true, PsiField::class.java),
                AbstractCouplingCohesionCandidateCalculator(candidates, FeatureType.FIELD_ACCESS_COHESION_2, false, false, PsiField::class.java),

                TypedElementsCouplingCohesionCandidateCalculator(candidates, FeatureType.TYPED_ELEMENTS_COUPLING, true, true),
                TypedElementsCouplingCohesionCandidateCalculator(candidates, FeatureType.TYPED_ELEMENTS_COHESION, false, true),

                AbstractCouplingCohesionCandidateCalculator(candidates, FeatureType.TYPE_ACCESS_COUPLING, true, true, PsiTypeElement::class.java),
                AbstractCouplingCohesionCandidateCalculator(candidates, FeatureType.TYPE_ACCESS_COUPLING_2, true, false, PsiTypeElement::class.java),
                AbstractCouplingCohesionCandidateCalculator(candidates, FeatureType.TYPE_ACCESS_COHESION, false, true, PsiTypeElement::class.java),
                AbstractCouplingCohesionCandidateCalculator(candidates, FeatureType.TYPE_ACCESS_COHESION_2, false, false, PsiTypeElement::class.java),

                PackageAccessCouplingCohesionCandidateCalculator(candidates, FeatureType.PACKAGE_COUPLING, true, true),
                PackageAccessCouplingCohesionCandidateCalculator(candidates, FeatureType.PACKAGE_COUPLING_2, true, false),
                PackageAccessCouplingCohesionCandidateCalculator(candidates, FeatureType.PACKAGE_COHESION, false, true),
                PackageAccessCouplingCohesionCandidateCalculator(candidates, FeatureType.PACKAGE_COHESION_2, false, false)
        )

        val complementCalculators = listOf(
                NumLiteralsMethodCalculator(candidates),
                NumTernaryMethodCalculator(candidates),
                NumTypedElementsMethodCalculator(candidates),
                NumInvocationMethodCalculator(candidates),
                NumIfMethodCalculator(candidates),
                NumAssignmentsMethodCalculator(candidates),
                NumSwitchMethodCalculator(candidates),
                NumLocalVarsMethodCalculator(candidates),
                NumFieldAccessMethodCalculator(candidates),
                NumLocalVarsAccessMethodCalculator(candidates),
                NumTypeAccessesMethodCalculator(candidates),
                NumUsedPackagesMethodCalculator(candidates)
        )

        val invocationMetricCoupling = MaxFrom2Metric(listOf(
                AbstractCouplingCohesionCandidateCalculator(candidates, FeatureType.INVOCATION_COUPLING, true, true, PsiCallExpression::class.java),
                AbstractCouplingCohesionCandidateCalculator(candidates, FeatureType.INVOCATION_COUPLING, true, true, PsiNewExpression::class.java)
        ))

        val invocationMetricCohesion = MaxFrom2Metric(listOf(
                AbstractCouplingCohesionCandidateCalculator(candidates, FeatureType.INVOCATION_COHESION, false, true, PsiCallExpression::class.java),
                AbstractCouplingCohesionCandidateCalculator(candidates, FeatureType.INVOCATION_COHESION, false, true, PsiNewExpression::class.java)
        ))

        val candidateMetrics = candidateCalculators.map { CandidateMetric(it) }

        val complementMetrics = complementCalculators
                .mapIndexed { i, calc -> ComplementMetric(listOf(calc, candidateCalculators[i])) }

        metrics.addAll(listOf(
                candidateMetrics,
                complementMetrics,
                listOf(invocationMetricCoupling, invocationMetricCohesion)
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

        candidatesWithFeatures.forEach { cand -> metrics.forEach { m -> m.fetchResult(cand) } }
        assert(candidatesWithFeatures.all { it.features.all { it.value != -1.0 || it.key.name.startsWith("TYPE_ACCESS_") } })
    }

    fun getCandidatesWithFeatures(): List<CandidateWithFeatures> {
        calculate()
        return candidatesWithFeatures
    }
}
