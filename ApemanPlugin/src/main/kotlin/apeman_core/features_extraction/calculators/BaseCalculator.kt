package apeman_core.features_extraction.calculators

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.utils.CandidateUtils
import com.intellij.psi.PsiMethod

abstract class BaseCalculator(val candidates: List<ExtractionCandidate>, val features: List<FeatureType>
) : SuperBaseCalculator()
{

    val numFeature = features[0]
    val conFeature = features[1]
    val coupling1Feature = features[2]
    val cohesion1Feature = features[3]

    var coupling2Feature: FeatureType?
    var cohesion2Feature: FeatureType?

    init {
        assert(numFeature.name.startsWith("NUM_"))
        assert(conFeature.name.startsWith("CON_"))
        assert(coupling1Feature.name.endsWith("_COUPLING"))
        assert(cohesion1Feature.name.endsWith("_COHESION"))

        if (features.count() == 6) {
            coupling2Feature = features[4]
            cohesion2Feature = features[5]

            assert(coupling2Feature!!.name.endsWith("_COUPLING_2"))
            assert(cohesion2Feature!!.name.endsWith("_COHESION_2"))

        } else {
            coupling2Feature = null
            cohesion2Feature = null
        }
    }

    override val results = Results(features, candidates)
    abstract fun getStatementsMap(): StatementsMap

    override fun calculateMethod(method: PsiMethod) {

        val statementsMap = getStatementsMap()
        statementsMap.addElementsAbstract(method)
        val methodCandidates = CandidateUtils.getCandidatesOfMethod(method, candidates)
        val sourceCandidate = CandidateUtils.getSourceCandidate(method, methodCandidates)
        val numsAndCons = statementsMap.calculateNumAndCon(sourceCandidate, methodCandidates)
        val coupsAndCohs = statementsMap
                .calculateCouplingAndCohesions(sourceCandidate, candidates)
        methodCandidates.withIndex().forEach { (i, cand) ->
            results.set(cand, numFeature, numsAndCons[i].first.toDouble())
            results.set(cand, conFeature, numsAndCons[i].second.toDouble())
            results.set(cand, coupling1Feature, coupsAndCohs[i].first.first)
            results.set(cand, cohesion1Feature, coupsAndCohs[i].second.first)

            if (coupling2Feature != null) {
                results.set(cand, coupling2Feature!!, coupsAndCohs[i].first.second)
                results.set(cand, cohesion2Feature!!, coupsAndCohs[i].second.first)
            }
        }
    }
}