package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import com.intellij.psi.PsiLiteralExpression

class NumLiteralsCandidate(candidates: List<ExtractionCandidate>
) : AbstractNumCandidate(candidates, FeatureType.NUM_LITERAL) {

    override fun createVisitor(methodCandidates: List<ExtractionCandidate>) = Visitor(methodCandidates)

    inner class Visitor(methodCandidates: List<ExtractionCandidate>
    ) : AbstractNumCandidate.CandidateVisitor(methodCandidates) {

        override fun visitLiteralExpression(literal: PsiLiteralExpression) {
            super.visitLiteralExpression(literal)

            if (!isInsideMethod)
                return
            updateCounters()
        }
    }
}
