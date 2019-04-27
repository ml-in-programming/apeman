package apeman_core.features_extraction.calculators.optimized

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.features_extraction.calculators.OptimizedMetric
import apeman_core.features_extraction.calculators.StatementsMap
import com.intellij.psi.PsiMethodCallExpression

class InvocationCalculator(candidates: List<ExtractionCandidate>
) : OptimizedMetric(
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

        override fun getVisitor() = Visitor()
    }

    override fun getStatementsMap() = InvocationStatementsMap()
}
