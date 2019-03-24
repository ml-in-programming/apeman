package apeman_core.features_extraction.calculators

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.PsiMethodReferenceExpression
import com.intellij.psi.PsiVariable

class InvocationCalculator(candidates: List<ExtractionCandidate>
) : BaseCalculator(
        candidates,
        listOf(
                FeatureType.NUM_INVOCATION,
                FeatureType.CON_INVOCATION,
                FeatureType.INVOCATION_COUPLING,
                FeatureType.INVOCATION_COHESION
        )
) {

    inner class InvocationStatementsMap : StatementsMap() {
        inner class Visitor : StatementsMap.Visitor() {
            override fun visitMethodCallExpression(expression: PsiMethodCallExpression?) {
                super.visitMethodCallExpression(expression)
                val method = expression!!.methodExpression.resolve() ?: return
                addElem(method)
            }
        }
    }

    override fun getStatementsMap() = InvocationStatementsMap()
}
