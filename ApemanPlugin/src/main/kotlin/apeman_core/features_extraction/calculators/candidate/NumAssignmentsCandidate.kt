package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import com.intellij.psi.PsiAssignmentExpression

class NumAssignmentsCandidate(candidates: List<ExtractionCandidate>
) : AbstractNumCandidate(candidates, FeatureType.NUM_ASSIGN) {

    override fun createVisitor(methodCandidates: List<ExtractionCandidate>) = Visitor(methodCandidates)

    inner class Visitor(methodCandidates: List<ExtractionCandidate>
    ) : AbstractNumCandidate.CandidateVisitor(methodCandidates) {
        override fun visitAssignmentExpression(expression: PsiAssignmentExpression) {
            super.visitAssignmentExpression(expression)

            if (isInsideMethod) {
                updateCounters()
            }
        }
    }
}
