package apeman_core.features_extraction.calculators.method

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import com.intellij.psi.PsiConditionalExpression

class NumTernaryMethodCalculator(candidates: List<ExtractionCandidate>
) : NumSimpleElementMethodCalculator(candidates, FeatureType.CON_CONDITIONAL) {

    override fun createVisitor(methodCandidates: List<ExtractionCandidate>) = Visitor(methodCandidates)

    inner class Visitor(methodCandidates: List<ExtractionCandidate>
    ) : NumSimpleElementMethodCalculator.Visitor(methodCandidates) {
        override fun visitConditionalExpression(expression: PsiConditionalExpression) {
            super.visitConditionalExpression(expression)
            elementsCounter++
        }
    }
}
