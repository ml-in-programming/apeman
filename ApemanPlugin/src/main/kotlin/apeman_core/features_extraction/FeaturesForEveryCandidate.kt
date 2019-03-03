package apeman_core.features_extraction

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.pipes.CandidateWithFeatures
import apeman_core.base_entities.Features
import apeman_core.features_extraction.calculators.BaseMetricsCalculator
import apeman_core.features_extraction.calculators.candidate.*
import apeman_core.features_extraction.calculators.method.*
import com.intellij.analysis.AnalysisScope
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiCallExpression
import com.intellij.psi.PsiField
import com.intellij.psi.PsiNewExpression
import com.intellij.psi.PsiVariable
import com.sixrr.metrics.metricModel.MetricsResult


class FeaturesForEveryCandidate(
        private val project: Project,
        private val analysisScope: AnalysisScope,
        candidates: ArrayList<ExtractionCandidate>
) {
    private val candidates = ArrayList(candidates.map { CandidateWithFeatures(it) })
    private val metrics: MutableList<BaseMetricsCalculator> = arrayListOf()
    private var calcRunner: FeaturesCalculationRunner? = null

    init {
        for ((_, feat) in this.candidates) {
            for (type in FeatureType.values()) {
                feat[type] = -1.0
            }
        }
        declareMetrics()
    }

    private fun declareMetrics() {

        metrics.addAll(listOf(
                NumLiteralsCandidateCalculator(candidates),
                NumTernaryOperatorsCandidatesCalculator(candidates),
                NumSwitchOperatorsCandidatesCalculator(candidates),
                NumTypeAccessesCandidateCalculator(candidates),
                NumInvocationsCandidateCalculator(candidates),
                NumIfCandidateCalculator(candidates),
                NumAssignmentsCandidateCalculator(candidates),
                NumTypedElementsCandidateCalculator(candidates),
                NumVarsAccessCandidateCalculator(candidates),
                NumFieldAccessCandidateCalculator(candidates),
                NumLocalVarsCandidateCalculator(candidates),
                NumPackageAccessesCandidateCalculator(candidates),

                LocCandidateCalculator(candidates),
                RatioLocCandidateCalculator(candidates),

                NumLiteralsMethodCalculator(candidates),
                NumTernaryMethodCalculator(candidates),
                NumUsedTypesMethodCalculator(candidates),
                NumInvocationMethodCalculator(candidates),
                NumIfMethodCalculator(candidates),
                NumAssignmentsMethodCalculator(candidates),
                NumSwitchMethodCalculator(candidates),
                NumLocalVarsAccessMethodCalculator(candidates),
                NumFieldAccessMethodCalculator(candidates),
                NumLocalVarsMethodCalculator(candidates),
                NumTypedElementsMethodCalculator(candidates),
                NumUsedPackagesMethodCalculator(candidates),

                AbstractCouplingCohesionCandidateCalculator(candidates, FeatureType.VAR_ACCESS_COUPLING, true, true, PsiVariable::class.java),
                AbstractCouplingCohesionCandidateCalculator(candidates, FeatureType.VAR_ACCESS_COUPLING_2, true, false, PsiVariable::class.java),
                AbstractCouplingCohesionCandidateCalculator(candidates, FeatureType.VAR_ACCESS_COHESION, false, true, PsiVariable::class.java),
                AbstractCouplingCohesionCandidateCalculator(candidates, FeatureType.VAR_ACCESS_COHESION_2, false, false, PsiVariable::class.java),

                AbstractCouplingCohesionCandidateCalculator(candidates, FeatureType.FIELD_ACCESS_COUPLING, true, true, PsiField::class.java),
                AbstractCouplingCohesionCandidateCalculator(candidates, FeatureType.FIELD_ACCESS_COUPLING_2, true, false, PsiField::class.java),
                AbstractCouplingCohesionCandidateCalculator(candidates, FeatureType.FIELD_ACCESS_COHESION, false, true, PsiField::class.java),
                AbstractCouplingCohesionCandidateCalculator(candidates, FeatureType.FIELD_ACCESS_COHESION_2, false, false, PsiField::class.java),

                AbstractCouplingCohesionCandidateCalculator(candidates, FeatureType.INVOCATION_COUPLING, false, true, aClasses = arrayListOf<Class<PsiCallExpression>>(PsiCallExpression::class.java, PsiNewExpression::class.java)),
                AbstractCouplingCohesionCandidateCalculator(candidates, FeatureType.INVOCATION_COHESION, false, false, PsiField::class.java),


//                MaxFrom2Metric("Ratio_Invocation", InvocationCouplingCandidateMetric(candidates), InvocationNewCouplingCandidateMetric(candidates)),
//                MaxFrom2Metric("Invocation_Cohesion", InvocationCohesionCandidateMetric(candidates), InvocationNewCohesionCandidateMetric(candidates)),
////                CandidateMetric("Ratio_Type_Access", TypeAccessCouplingCandidateMetric(candidates)),
////                CandidateMetric("Ratio_Type_Access2", TypeAccessCoupling2CandidateMetric(candidates)),
////                CandidateMetric("TypeAc_Cohesion", TypeAccessCohesionCandidateMetric(candidates)),
////                CandidateMetric("TypeAc_Cohesion2", TypeAccessCohesion2CandidateMetric(candidates)),
//                CandidateMetric("Ratio_Typed_Ele", TypeElementCouplingCandidateMetric(candidates)),
//                CandidateMetric("TypedEle_Cohesion", TypeElementCohesionCandidateMetric(candidates)),
//                CandidateMetric("Ratio_Package", PackageAccessCouplingCandidateMetric(candidates)),
//                CandidateMetric("Ratio_Package2", PackageAccessCoupling2CandidateMetric(candidates)),
//                CandidateMetric("Package_Cohesion", PackageAccessCohesionCandidateMetric(candidates)),
//                CandidateMetric("Package_Cohesion2", PackageAccessCohesion2CandidateMetric(candidates))
        ))
    }

    private fun calculate() {
        assert(metrics.isNotEmpty())

        calcRunner = FeaturesCalculationRunner(project, analysisScope, metrics)
        calcRunner!!.calculate()
    }

    fun getCandidatesWithFeatures(): List<CandidateWithFeatures> {
        calculate()
        val candResults = calcRunner!!.resultsForCandidates!!
        val methodResults = calcRunner!!.resultsForMethods!!
        val candWithFeatures = arrayListOf<CandidateWithFeatures>()

        for (cand in candidates) {
            val featureVector = getFeatureVector(
                    cand, candResults, methodResults
            )
            candWithFeatures.add(CandidateWithFeatures(cand, featureVector))
        }
        return candWithFeatures
    }

    private fun getFeatureVector(
            cand: ExtractionCandidate,
            candResults: MetricsResult,
            methodResults: MetricsResult
    ): List<Features> {

        val featureVector = arrayListOf<Features>()
        for (m in metrics) {
            val value = m.calculateResult(cand, candResults, methodResults)
            featureVector.add(Features(m.name, value))
        }
        return featureVector
    }
}
