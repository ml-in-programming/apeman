package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.pipes.CandidateWithFeatures
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiTypeElement

import java.util.ArrayList

class NumTypeAccessCandidateCalculator(candidates: List<ExtractionCandidate>
) : AbstractNumCandidateCalculator(candidates, FeatureType.NUM_TYPE_ACCESS) {

    override fun createVisitor() = Visitor()

    inner class Visitor : AbstractNumCandidateCalculator.CandidateVisitor() {

        override fun visitTypeElement(type: PsiTypeElement) {
            super.visitTypeElement(type)
            if (isInsideMethod) {
                updateCounters()
            }
        }
    }
}