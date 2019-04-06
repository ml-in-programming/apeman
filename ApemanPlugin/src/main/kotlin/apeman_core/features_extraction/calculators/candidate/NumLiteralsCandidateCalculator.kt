package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import com.intellij.psi.PsiLiteralExpression

class NumLiteralsCandidateCalculator(candidates: List<ExtractionCandidate>
) : AbstractNumCandidateCalculator(candidates, FeatureType.NUM_LITERAL) {

    override fun createVisitor(methodCandidates: List<ExtractionCandidate>) = Visitor(methodCandidates)

    inner class Visitor(methodCandidates: List<ExtractionCandidate>
    ) : AbstractNumCandidateCalculator.CandidateVisitor(methodCandidates) {

        override fun visitLiteralExpression(literal: PsiLiteralExpression) {
            super.visitLiteralExpression(literal)

            if (!isInsideMethod)
                return
            updateCounters()
        }
    }
}
