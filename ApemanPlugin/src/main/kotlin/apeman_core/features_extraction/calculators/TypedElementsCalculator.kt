package apeman_core.features_extraction.calculators

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import com.intellij.psi.*

class TypedElementsCalculator(candidates: List<ExtractionCandidate>
) : BaseCalculator(
        candidates,
        listOf(
                FeatureType.NUM_TYPED_ELEMENTS,
                FeatureType.CON_TYPED_ELEMENTS,
                FeatureType.TYPED_ELEMENTS_COUPLING,
                FeatureType.TYPED_ELEMENTS_COHESION
        )
) {

    inner class TypedElementsStatementsMap : StatementsMap() {
        inner class Visitor : StatementsMap.Visitor() {

            override fun visitReferenceElement(reference: PsiJavaCodeReferenceElement?) {
                super.visitReferenceElement(reference)
                val elem = reference!!.resolve()
                if (elem is PsiField || elem is PsiParameter || elem is PsiVariable || elem is PsiMethod) {
                    addElem(elem)
                }
            }

            override fun visitExpression(expression: PsiExpression?) {
                super.visitExpression(expression)
                addElem(expression!!)
            }
        }
    }

    override fun getStatementsMap() = TypedElementsStatementsMap()
}
