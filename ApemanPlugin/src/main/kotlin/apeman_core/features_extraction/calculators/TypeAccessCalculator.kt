package apeman_core.features_extraction.calculators

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import com.intellij.psi.PsiTypeCastExpression
import com.intellij.psi.PsiTypeElement

class TypeAccessCalculator(candidates: List<ExtractionCandidate>
) : BaseCalculator(
        candidates,
        listOf(
                FeatureType.NUM_TYPE_ACCESS,
                FeatureType.CON_TYPE_ACCESS,
                FeatureType.TYPE_ACCESS_COUPLING,
                FeatureType.TYPE_ACCESS_COHESION,
                FeatureType.TYPE_ACCESS_COUPLING_2,
                FeatureType.TYPE_ACCESS_COHESION_2
        )
) {

    inner class TypeAccessStatementsMap : StatementsMap() {
        inner class Visitor : StatementsMap.Visitor() {
            override fun visitTypeElement(type: PsiTypeElement?) {
                super.visitTypeElement(type)
                addElem(type!!)
            }
        }
    }

    override fun getStatementsMap() = TypeAccessStatementsMap()
}
