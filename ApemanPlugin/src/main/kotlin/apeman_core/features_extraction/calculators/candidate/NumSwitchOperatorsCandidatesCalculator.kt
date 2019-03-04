package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.FeatureType
import apeman_core.pipes.CandidateWithFeatures
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiSwitchStatement

import java.util.ArrayList

class NumSwitchOperatorsCandidatesCalculator(candidates: ArrayList<CandidateWithFeatures>) : AbstractNumCandidateCalculator(candidates, FeatureType.NUM_SWITCH) {

    override fun createVisitor() = Visitor()

    inner class Visitor : AbstractNumCandidateCalculator.CandidateVisitor() {

        override fun visitSwitchStatement(statement: PsiSwitchStatement) {
            super.visitSwitchStatement(statement)

            if (!isInsideMethod)
                return
            updateCounters()
        }
    }
}
