package apeman_core.features_extraction.calculators.method

import apeman_core.base_entities.FeatureType
import apeman_core.pipes.CandidateWithFeatures
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiConditionalExpression

import java.util.ArrayList

class NumTernaryMethodCalculator(candidates: ArrayList<CandidateWithFeatures>) : NumSimpleElementMethodCalculator(candidates, FeatureType.CON_CONDITIONAL) {

    override fun createVisitor() = Visitor()

    inner class Visitor : NumSimpleElementMethodCalculator.Visitor() {
        override fun visitConditionalExpression(expression: PsiConditionalExpression) {
            super.visitConditionalExpression(expression)
            elementsCounter++
        }
    }
}
