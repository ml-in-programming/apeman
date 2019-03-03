package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.BlockOfMethod
import apeman_core.base_entities.FeatureType
import apeman_core.features_extraction.calculators.BaseMetricsCalculator
import apeman_core.pipes.CandidateWithFeatures
import apeman_core.utils.BlocksUtils
import apeman_core.utils.CandidateUtils
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiMethod

import java.util.ArrayList
import java.util.HashMap

open class AbstractCouplingCohesionCandidateCalculator<T> (
        candidates: ArrayList<CandidateWithFeatures>,
        neededFeature: FeatureType,
        private val isCouplingMethod: Boolean,
        private val isFirstPlace: Boolean,
        private val aClass: Class<T>
) : BaseMetricsCalculator(candidates, neededFeature)
{

    private var coupling = 0.0
    private var cohesion = 0.0
    private fun result() = if (isCouplingMethod) coupling else cohesion

    override fun createVisitor() = CandidateVisitor()

    open inner class CandidateVisitor : JavaRecursiveElementVisitor() {
        private var methodCandidates: ArrayList<CandidateWithFeatures>? = null

        override fun visitMethod(method: PsiMethod) {
            super.visitMethod(method)
            methodCandidates = CandidateUtils.getCandidatesOfMethod(method, candidates)
            for (candidate in methodCandidates!!) {
                calculateCouplingCohesion(candidate)
                candidate.features.features[features[0]!!] = result()
            }
        }
    }

    private fun calculateCouplingCohesion(candidate: CandidateWithFeatures) {
        coupling = 0.0
        cohesion = 0.0

        val sourceMethod = candidate.candidate.sourceMethod
        val sourceBlock = BlocksUtils.getBlockFromMethod(sourceMethod)
        val candidateBlock = candidate.candidate.block

        val elements = getElementsFromBlock(candidateBlock)
        if (elements.isEmpty() || elements.size == 1 && !isFirstPlace) {
            return
        }

        val ratio = HashMap<T, Double>()

        for (e in elements) {
            val freqCandidate = getFreqOfElementFromBlock(candidateBlock, e)
            val freqMethod = getFreqOfElementFromBlock(sourceBlock, e)
            ratio[e] = freqCandidate / freqMethod
        }

        val bestElem = getElementFromRatio(ratio)
        coupling = ratio[bestElem]!!

        val loc = BlocksUtils.getNumStatementsRecursively(candidateBlock)
        val count = getCountOfElementFromBlock(candidateBlock, bestElem)
        cohesion = count.toDouble() / loc
    }

    protected open fun getElementsFromBlock(block: BlockOfMethod): Set<T> {
        return BlocksUtils.getElementsOfBlock(block, aClass)
    }

    protected open fun getCountOfElementFromBlock(block: BlockOfMethod, elem: T?): Int {
        return BlocksUtils.getCountOfElementFromBlock<T>(block, elem)
    }

    protected open fun getFreqOfElementFromBlock(block: BlockOfMethod, elem: T): Double {
        return BlocksUtils.getFreqOfElementFromBlock(block, elem)
    }

    private fun getElementFromRatio(ratio: HashMap<T, Double>): T? {
        val elem = getMaxRatio(ratio)
        if (isFirstPlace)
            return elem

        ratio.remove(elem)
        return getMaxRatio(ratio)
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
}
