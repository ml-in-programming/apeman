package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import com.intellij.psi.PsiIfStatement

class NumIfCandidateCalculator(candidates: List<ExtractionCandidate>
) : AbstractNumCandidateCalculator(candidates, FeatureType.NUM_IF) {

    override fun createVisitor(methodCandidates: List<ExtractionCandidate>) = Visitor(methodCandidates)

    inner class Visitor(methodCandidates: List<ExtractionCandidate>
    ) : AbstractNumCandidateCalculator.CandidateVisitor(methodCandidates) {

        override fun visitIfStatement(statement: PsiIfStatement) {
            super.visitIfStatement(statement)
            if (isInsideMethod) {
                updateCounters()
            }
        }
    }
}
