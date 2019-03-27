package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.features_extraction.calculators.BaseMetricsCalculator
import apeman_core.utils.CandidateUtils
import apeman_core.utils.MethodUtils
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiStatement

import java.util.ArrayList

abstract class AbstractNumCandidateCalculator(candidates: List<ExtractionCandidate>, feature: FeatureType) : BaseMetricsCalculator(candidates, feature) {

    open inner class CandidateVisitor(
            val methodCandidates: List<ExtractionCandidate>
    ) : JavaRecursiveElementVisitor() {
        var methodNestingDepth = 0
        var counts: MutableList<Int> = ArrayList()
        var isInsideMethod = false

        override fun visitMethod(method: PsiMethod) {
            if (methodNestingDepth == 0) {
                initCounters()
                isInsideMethod = true
            }

            methodNestingDepth++
            super.visitMethod(method)
            methodNestingDepth--

            if (methodNestingDepth == 0 && !MethodUtils.isAbstract(method)) {
                for ((i, cand) in methodCandidates.withIndex()) {
                    results.set(cand, firstFeature, getCounterForCand(i).toDouble())
                }
                isInsideMethod = false
            }
        }

        protected open fun initCounters() {
            counts.clear()
            repeat(methodCandidates.size) { counts.add(0) }
        }

        protected open fun getCounterForCand(i: Int) = counts[i]

        protected open fun updateCounters() = methodCandidates.indices.forEach { updateCounter(it) }
        protected open fun updateCounter(i: Int) {
            if (methodCandidates[i].isInCandidate) {
                counts[i]++
            }
        }

        override fun visitStatement(statement: PsiStatement) {
            CandidateUtils.checkStartOfCandidates(statement, methodCandidates)
            super.visitStatement(statement)
            CandidateUtils.checkEndOfCandidates(statement, methodCandidates)
        }
    }
}
