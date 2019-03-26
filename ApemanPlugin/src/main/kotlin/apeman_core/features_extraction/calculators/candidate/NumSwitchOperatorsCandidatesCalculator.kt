package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import com.intellij.psi.PsiSwitchStatement

class NumSwitchOperatorsCandidatesCalculator(candidates: List<ExtractionCandidate>
) : AbstractNumCandidateCalculator(candidates, FeatureType.NUM_SWITCH) {

    override fun createVisitor(methodCandidates: List<ExtractionCandidate>) = Visitor()

    inner class Visitor : AbstractNumCandidateCalculator.CandidateVisitor() {

        override fun visitSwitchStatement(statement: PsiSwitchStatement) {
            super.visitSwitchStatement(statement)

            if (!isInsideMethod)
                return
            updateCounters()
        }
    }
}
