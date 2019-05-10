package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.features_extraction.calculators.NotOptimizedCalculator
import apeman_core.utils.CandidateUtils
import apeman_core.utils.MethodUtils
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiStatement

import java.util.ArrayList

abstract class AbstractNumCandidate(candidates: List<ExtractionCandidate>, feature: FeatureType) : NotOptimizedCalculator(candidates, feature) {

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
                    results.set(cand, firstFeature, getCounterForCand(i))
                }
                isInsideMethod = false
            }
        }

        override fun visitStatement(statement: PsiStatement) {
            CandidateUtils.checkStartOfCandidates(statement, methodCandidates)
            super.visitStatement(statement)
            abstractVisitStatement(statement)
            CandidateUtils.checkEndOfCandidates(statement, methodCandidates)
        }

        protected open fun abstractVisitStatement(statement: PsiStatement) {}

        protected open fun initCounters() {
            counts.clear()
            repeat(methodCandidates.size) { counts.add(0) }
        }

        protected open fun getCounterForCand(i: Int): Double = counts[i].toDouble()
        protected open fun updateCounters() = methodCandidates.indices.forEach { updateCounter(it) }

        protected open fun updateCounter(i: Int) {
            if (methodCandidates[i].isInCandidate) {
                counts[i]++
            }
        }
    }
}
