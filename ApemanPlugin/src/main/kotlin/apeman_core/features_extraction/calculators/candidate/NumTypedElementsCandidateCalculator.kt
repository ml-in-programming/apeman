package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.FeatureType
import apeman_core.pipes.CandidateWithFeatures
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiTypeElement

import java.util.ArrayList

class NumTypedElementsCandidateCalculator(candidates: ArrayList<CandidateWithFeatures>) : AbstractNumCandidateCalculator(candidates, FeatureType.NUM_TYPED_ELEMENTS) {

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
