package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.BlockOfMethod
import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.features_extraction.calculators.BaseMetricsCalculator
import apeman_core.utils.BlocksUtils
import apeman_core.utils.CandidateUtils
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiMethod

import java.util.HashMap

open class AbstractCouplingCohesionCandidateCalculator<T> (
        candidates: List<ExtractionCandidate>,
        featureName: String,
        private val aClass: Class<T>
) : BaseMetricsCalculator(candidates, features = FeatureType.couplingCohesionGroup(featureName))
{
    private val cp1Feat: FeatureType
    private val cp2Feat: FeatureType?
    private val ch1Feat: FeatureType
    private val ch2Feat: FeatureType?

    init {
        val features = FeatureType.couplingCohesionGroup(featureName)
        assert((features.count() == 2) xor (features.count() == 4))
        if (features.count() == 2) {
            ch1Feat = features[0]
            ch2Feat = null
            cp1Feat = features[1]
            cp2Feat = null
        } else { //if (features.count() == 4) {
            ch1Feat = features[0]
            ch2Feat = features[1]
            cp1Feat = features[2]
            cp2Feat = features[3]
        }
    }

    private var coupling1 = 0.0
    private var coupling2 = 0.0
    private var cohesion1 = 0.0
    private var cohesion2 = 0.0

    override fun createVisitor() = CandidateVisitor()

    open inner class CandidateVisitor : JavaRecursiveElementVisitor() {
        private var methodCandidates: List<ExtractionCandidate>? = null

        override fun visitMethod(method: PsiMethod) {
            super.visitMethod(method)
            methodCandidates = CandidateUtils.getCandidatesOfMethod(method, candidates)
            for (candidate in methodCandidates!!) {
                calculateCouplingCohesion(candidate)

                results.set(candidate, cp1Feat, coupling1)

                if (cp2Feat != null)
                    results.set(candidate, cp2Feat, coupling2)

                results.set(candidate, ch1Feat, cohesion1)

                if (ch2Feat != null)
                    results.set(candidate, ch2Feat, cohesion2)
            }
        }
    }

    private fun calculateCouplingCohesion(candidate: ExtractionCandidate) {
        coupling1 = 0.0
        coupling2 = 0.0
        cohesion1 = 0.0
        cohesion2 = 0.0

        val sourceMethod = candidate.sourceMethod
        val sourceBlock = BlocksUtils.getBlockFromMethod(sourceMethod)
        val candidateBlock = candidate.block

        val elements = getElementsFromBlock(candidateBlock)
        if (elements.isEmpty()) {
            return
        }

        val ratio = HashMap<T, Double>()

        for (e in elements) {
            val freqCandidate = getFreqOfElementFromBlock(candidateBlock, e)
            val freqMethod = getFreqOfElementFromBlock(sourceBlock, e)
            ratio[e] = freqCandidate / freqMethod
        }

        val (firstElem, secondElem) = get2BestElementsByRatio(ratio)
        coupling1 = ratio[firstElem] ?: 0.0
        coupling2 = ratio[secondElem] ?: 0.0

        val loc = BlocksUtils.getNumStatementsRecursively(candidateBlock)
        val count1 = if (firstElem == null) 0 else getCountOfElementFromBlock(candidateBlock, firstElem)
        val count2 = if (secondElem == null) 0 else getCountOfElementFromBlock(candidateBlock, secondElem)
        cohesion1 = count1.toDouble() / loc
        cohesion2 = count2.toDouble() / loc
    }

    protected open fun getElementsFromBlock(block: BlockOfMethod): Set<T> {
        return BlocksUtils.getElementsOfBlock(block, aClass)
    }

    protected open fun getCountOfElementFromBlock(block: BlockOfMethod, elem: T): Int {
        return BlocksUtils.getCountOfElementFromBlock(block, elem)
    }

    protected open fun getFreqOfElementFromBlock(block: BlockOfMethod, elem: T): Double {
        return BlocksUtils.getFreqOfElementFromBlock(block, elem)
    }

    private fun get2BestElementsByRatio(ratio: HashMap<T, Double>): Pair<T?, T?> {
        val elem = getMaxRatio(ratio)
        if (cp2Feat == null || elem == null) {
            return elem to null
        }
        return elem to getSecondRatio(ratio, firstMax = elem)
    }

    private fun getMaxRatio(ratio: HashMap<T, Double>): T? {
        var maxElem: T? = null
        var maxDouble = -1.0

        for ((key, value) in ratio) {
            if (value > maxDouble) {
                maxElem = key
                maxDouble = value
            }
        }
        return maxElem
    }

    private fun getSecondRatio(ratio: HashMap<T, Double>, firstMax: T): T? {
        var secondElem: T? = null
        var secondDouble = -1.0

        for ((key, value) in ratio) {
            if (value > secondDouble && key != firstMax) {
                secondElem = key
                secondDouble = value
            }
        }
        return secondElem
    }
}
