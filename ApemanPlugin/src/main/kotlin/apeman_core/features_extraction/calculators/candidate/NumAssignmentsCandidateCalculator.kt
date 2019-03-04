package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.FeatureType
import apeman_core.pipes.CandidateWithFeatures
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiAssignmentExpression

import java.util.ArrayList

class NumAssignmentsCandidateCalculator(candidates: ArrayList<CandidateWithFeatures>
) : AbstractNumCandidateCalculator(candidates, FeatureType.NUM_ASSIGN) {

    override fun createVisitor() = Visitor()

    inner class Visitor : AbstractNumCandidateCalculator.CandidateVisitor() {
        override fun visitAssignmentExpression(expression: PsiAssignmentExpression) {
            super.visitAssignmentExpression(expression)

            if (isInsideMethod) {
                updateCounters()
            }
        }
    }
}
