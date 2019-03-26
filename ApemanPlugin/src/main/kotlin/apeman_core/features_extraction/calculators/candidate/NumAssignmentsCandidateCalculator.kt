package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import com.intellij.psi.PsiAssignmentExpression

class NumAssignmentsCandidateCalculator(candidates: List<ExtractionCandidate>
) : AbstractNumCandidateCalculator(candidates, FeatureType.NUM_ASSIGN) {

    override fun createVisitor() = Visitor()

    inner class Visitor : AbstractNumCandidateCalculator.CandidateVisitor() {
        override fun visitAssignmentExpression(expression: PsiAssignmentExpression) {
            super.visitAssignmentExpression(expression)

            if (isInsideMethod) {
                updateCounters()
            }
        }
    }
}
