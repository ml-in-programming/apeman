package apeman_core.features_extraction.calculators.method

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import com.intellij.psi.PsiMethodCallExpression

class NumInvocationMethodCalculator(candidates: List<ExtractionCandidate>
) : NumSimpleElementMethodCalculator(candidates, FeatureType.CON_INVOCATION) {

    override fun createVisitor() = Visitor()

    inner class Visitor : NumSimpleElementMethodCalculator.Visitor() {

        override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
            super.visitMethodCallExpression(expression)
            elementsCounter++
        }

//        override fun visitNewExpression(exp: PsiNewExpression) {
//            super.visitNewExpression(exp)
//            if (exp.arrayDimensions.isEmpty() && exp.arrayInitializer == null) {
//                elementsCounter++
//            }
//        }

//        override fun visitTypeCastExpression(expression: PsiTypeCastExpression?) {
//            super.visitTypeCastExpression(expression)
//            elementsCounter++
//        }
    }
}
