package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.pipes.CandidateWithFeatures
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.PsiNewExpression
import com.intellij.psi.PsiTypeCastExpression

import java.util.ArrayList

class NumInvocationsCandidateCalculator(candidates: List<ExtractionCandidate>
) : AbstractNumCandidateCalculator(candidates, FeatureType.NUM_INVOCATION) {

    override fun createVisitor() = Visitor()

    inner class Visitor : AbstractNumCandidateCalculator.CandidateVisitor() {

        override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
            super.visitMethodCallExpression(expression)
            if (isInsideMethod)
                updateCounters()
        }

        override fun visitNewExpression(exp: PsiNewExpression) {
            super.visitNewExpression(exp)
            if (exp.arrayDimensions.size == 0 &&
                    exp.arrayInitializer == null && isInsideMethod) {
                updateCounters()
            }
        }

        override fun visitTypeCastExpression(expression: PsiTypeCastExpression?) {
            super.visitTypeCastExpression(expression)
            if (isInsideMethod)
                updateCounters()
        }
    }
}
