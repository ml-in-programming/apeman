package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.FeatureType
import apeman_core.pipes.CandidateWithFeatures
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.PsiNewExpression

import java.util.ArrayList

class NumInvocationsCandidateCalculator(candidates: ArrayList<CandidateWithFeatures>) : AbstractNumCandidateCalculator(candidates, FeatureType.NUM_INVOCATION) {

    override fun createVisitor() = Visitor()

    inner class Visitor : AbstractNumCandidateCalculator.CandidateVisitor() {

        override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
            super.visitMethodCallExpression(expression)
            if (!isInsideMethod)
                return
            updateCounters()
        }

        override fun visitNewExpression(exp: PsiNewExpression) {
            super.visitNewExpression(exp)
            if (exp.arrayDimensions.size == 0 &&
                    exp.arrayInitializer == null && isInsideMethod) {
                updateCounters()
            }
        }
    }
}
