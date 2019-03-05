package apeman_core.features_extraction.calculators.method

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.features_extraction.calculators.BaseMetricsCalculator
import apeman_core.pipes.CandidateWithFeatures
import apeman_core.utils.CandidateUtils
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiMethod

abstract class NumSimpleElementMethodCalculator(
        candidates: List<ExtractionCandidate>,
        neededFeature: FeatureType
) : BaseMetricsCalculator(candidates, neededFeature) {

    override fun createVisitor() = Visitor()

    open inner class Visitor : JavaRecursiveElementVisitor() {
        var elementsCounter = 0
        var nestingDepth = 0

        override fun visitMethod(method: PsiMethod) {
            if (nestingDepth == 0) {
                elementsCounter = 0
            }
            nestingDepth++
            super.visitMethod(method)
            nestingDepth--
            if (nestingDepth == 0) {
                for (cand in CandidateUtils.getCandidatesOfMethod(method, candidates)) {
                    results.set(cand, firstFeature, elementsCounter.toDouble())
                }
            }
        }
    }
}
