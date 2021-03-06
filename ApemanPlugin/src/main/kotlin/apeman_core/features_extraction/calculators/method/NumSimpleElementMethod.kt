package apeman_core.features_extraction.calculators.method

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.features_extraction.calculators.NotOptimizedCalculator
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiMethod

abstract class NumSimpleElementMethod(
        candidates: List<ExtractionCandidate>,
        neededFeature: FeatureType
) : NotOptimizedCalculator(candidates, neededFeature) {

    override fun createVisitor(methodCandidates: List<ExtractionCandidate>) = Visitor(methodCandidates)

    open inner class Visitor(val methodCandidates: List<ExtractionCandidate>) : JavaRecursiveElementVisitor() {
        var elementsCounter = 0
        var nestingDepth = 0

        override fun visitMethod(method: PsiMethod) {
            if (nestingDepth == 0) {
                initElementsCounter(method)
            }
            nestingDepth++

            super.visitMethod(method)
            nestingDepth--
            if (nestingDepth == 0) {
                for (cand in methodCandidates) {
                    results.set(cand, firstFeature, getResultElementCounter(method))
                }
            }
        }

        protected open fun initElementsCounter(method: PsiMethod) {
            elementsCounter = 0
        }

        protected open fun getResultElementCounter(method: PsiMethod) = elementsCounter.toDouble()
    }
}
