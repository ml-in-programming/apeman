package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import com.intellij.psi.PsiConditionalExpression

class NumTernaryOperatorsCandidatesCalculator(candidates: List<ExtractionCandidate>
) : AbstractNumCandidateCalculator(candidates, FeatureType.NUM_CONDITIONAL) {

    override fun createVisitor(methodCandidates: List<ExtractionCandidate>) = Visitor(methodCandidates)

    inner class Visitor(methodCandidates: List<ExtractionCandidate>
    ) : AbstractNumCandidateCalculator.CandidateVisitor(methodCandidates) {

        override fun visitConditionalExpression(expression: PsiConditionalExpression) {
            super.visitConditionalExpression(expression)

            if (!isInsideMethod)
                return
            updateCounters()
        }
    }
}
