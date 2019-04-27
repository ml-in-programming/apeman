package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import com.intellij.psi.PsiSwitchStatement

class NumSwitchOperatorsCandidates(candidates: List<ExtractionCandidate>
) : AbstractNumCandidate(candidates, FeatureType.NUM_SWITCH) {

    override fun createVisitor(methodCandidates: List<ExtractionCandidate>) = Visitor(methodCandidates)

    inner class Visitor(methodCandidates: List<ExtractionCandidate>
    ) : AbstractNumCandidate.CandidateVisitor(methodCandidates) {

        override fun visitSwitchStatement(statement: PsiSwitchStatement) {
            super.visitSwitchStatement(statement)

            if (!isInsideMethod)
                return
            updateCounters()
        }
    }
}
