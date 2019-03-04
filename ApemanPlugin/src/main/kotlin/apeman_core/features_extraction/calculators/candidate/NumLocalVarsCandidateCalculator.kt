package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.FeatureType
import apeman_core.pipes.CandidateWithFeatures
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiLocalVariable

import java.util.ArrayList

class NumLocalVarsCandidateCalculator(candidates: ArrayList<CandidateWithFeatures>) : AbstractNumCandidateCalculator(candidates, FeatureType.NUM_LOCAL) {

    override fun createVisitor() = Visitor()

    inner class Visitor : AbstractNumCandidateCalculator.CandidateVisitor() {

        override fun visitLocalVariable(variable: PsiLocalVariable) {
            super.visitLocalVariable(variable)
            if (isInsideMethod) {
                updateCounters()
            }
        }
    }
}
