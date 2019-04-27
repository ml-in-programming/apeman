package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import com.intellij.psi.PsiLocalVariable

class NumLocalVarsCandidate(candidates: List<ExtractionCandidate>
) : AbstractNumCandidate(candidates, FeatureType.NUM_LOCAL) {

    override fun createVisitor(methodCandidates: List<ExtractionCandidate>) = Visitor(methodCandidates)

    inner class Visitor(methodCandidates: List<ExtractionCandidate>
    ) : AbstractNumCandidate.CandidateVisitor(methodCandidates) {

        override fun visitLocalVariable(variable: PsiLocalVariable) {
            super.visitLocalVariable(variable)
            if (isInsideMethod) {
                updateCounters()
            }
        }
    }
}
