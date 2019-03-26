package apeman_core.features_extraction.calculators.method

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import com.intellij.psi.PsiLiteralExpression

class NumLiteralsMethodCalculator(candidates: List<ExtractionCandidate>
) : NumSimpleElementMethodCalculator(candidates, FeatureType.CON_LITERAL) {

    override fun createVisitor(methodCandidates: List<ExtractionCandidate>) = Visitor()

    inner class Visitor : NumSimpleElementMethodCalculator.Visitor() {

        override fun visitLiteralExpression(literal: PsiLiteralExpression) {
            super.visitLiteralExpression(literal)
            elementsCounter++
        }
    }
}
