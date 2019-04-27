package apeman_core.features_extraction.calculators.method

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import com.intellij.psi.PsiLiteralExpression

class NumLiteralsMethod(candidates: List<ExtractionCandidate>
) : NumSimpleElementMethod(candidates, FeatureType.CON_LITERAL) {

    override fun createVisitor(methodCandidates: List<ExtractionCandidate>) = Visitor(methodCandidates)

    inner class Visitor(methodCandidates: List<ExtractionCandidate>
    ) : NumSimpleElementMethod.Visitor(methodCandidates) {

        override fun visitLiteralExpression(literal: PsiLiteralExpression) {
            super.visitLiteralExpression(literal)
            elementsCounter++
        }
    }
}
