package apeman_core.features_extraction.calculators.method

import apeman_core.base_entities.FeatureType
import apeman_core.pipes.CandidateWithFeatures
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiAssignmentExpression

import java.util.ArrayList

class NumAssignmentsMethodCalculator(candidates: ArrayList<CandidateWithFeatures>) : NumSimpleElementMethodCalculator(candidates, FeatureType.CON_ASSIGN) {

    override fun createVisitor() = Visitor()

    inner class Visitor : NumSimpleElementMethodCalculator.Visitor() {

        override fun visitAssignmentExpression(expression: PsiAssignmentExpression) {
            super.visitAssignmentExpression(expression)
            elementsCounter++
        }
    }
}
