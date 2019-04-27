package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import com.intellij.psi.PsiAssertStatement

class NumAssertCandidate(candidates: List<ExtractionCandidate>
) : AbstractNumCandidate(candidates, FeatureType.NUM_ASSERT) {

    override fun createVisitor(methodCandidates: List<ExtractionCandidate>) = Visitor(methodCandidates)

    inner class Visitor(methodCandidates: List<ExtractionCandidate>
    ) : AbstractNumCandidate.CandidateVisitor(methodCandidates) {

        override fun visitAssertStatement(statement: PsiAssertStatement?) {
            super.visitAssertStatement(statement)
            if (isInsideMethod) {
                updateCounters()
            }
        }
    }
}
