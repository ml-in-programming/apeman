package apeman_core.features_extraction.calculators

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import com.intellij.psi.*

class TypedElementsCalculator(candidates: List<ExtractionCandidate>
) : BaseCalculator(
        candidates,
        listOf(
                FeatureType.NUM_INVOCATION,
                FeatureType.CON_INVOCATION,
                FeatureType.INVOCATION_COUPLING,
                FeatureType.INVOCATION_COHESION
        )
) {

    inner class TypedElementsStatementsMap : StatementsMap() {
        inner class Visitor : StatementsMap.Visitor() {

            override fun visitReferenceElement(reference: PsiJavaCodeReferenceElement?) {
                super.visitReferenceElement(reference)
                val elem = reference!!.resolve()
                when (elem) {
                    is PsiField -> addElem(elem.type)
                    is PsiParameter -> addElem(elem.type)
                    is PsiVariable -> addElem(elem.type)
                    is PsiMethod -> addElem(elem.returnType!!)
                }
            }

            override fun visitExpression(expression: PsiExpression?) {
                super.visitExpression(expression)
                addElem(expression!!.type!!)
            }
        }
    }

    override fun getStatementsMap() = TypedElementsStatementsMap()
}
