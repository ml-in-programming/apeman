package apeman_core.features_extraction.calculators.method

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import com.intellij.psi.PsiAssignmentExpression

class NumAssignmentsMethod(candidates: List<ExtractionCandidate>
) : NumSimpleElementMethod(candidates, FeatureType.CON_ASSIGN) {

    override fun createVisitor(methodCandidates: List<ExtractionCandidate>) = Visitor(methodCandidates)

    inner class Visitor(methodCandidates: List<ExtractionCandidate>
    ) : NumSimpleElementMethod.Visitor(methodCandidates) {

        override fun visitAssignmentExpression(expression: PsiAssignmentExpression) {
            super.visitAssignmentExpression(expression)
            elementsCounter++
        }
    }
}
