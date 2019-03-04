package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.FeatureType
import apeman_core.pipes.CandidateWithFeatures
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiConditionalExpression

import java.util.ArrayList

class NumTernaryOperatorsCandidatesCalculator(candidates: ArrayList<CandidateWithFeatures>) : AbstractNumCandidateCalculator(candidates, FeatureType.NUM_CONDITIONAL) {

    override fun createVisitor() = Visitor()

    inner class Visitor : AbstractNumCandidateCalculator.CandidateVisitor() {

        override fun visitConditionalExpression(expression: PsiConditionalExpression) {
            super.visitConditionalExpression(expression)

            if (!isInsideMethod)
                return
            updateCounters()
        }
    }
}
