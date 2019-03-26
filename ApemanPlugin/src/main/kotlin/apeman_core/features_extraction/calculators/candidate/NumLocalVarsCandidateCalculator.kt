package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import com.intellij.psi.PsiLocalVariable

class NumLocalVarsCandidateCalculator(candidates: List<ExtractionCandidate>
) : AbstractNumCandidateCalculator(candidates, FeatureType.NUM_LOCAL) {

    override fun createVisitor(methodCandidates: List<ExtractionCandidate>) = Visitor()

    inner class Visitor : AbstractNumCandidateCalculator.CandidateVisitor() {

        override fun visitLocalVariable(variable: PsiLocalVariable) {
            super.visitLocalVariable(variable)
            if (isInsideMethod) {
                updateCounters()
            }
        }
    }
}
