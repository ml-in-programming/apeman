package apeman_core.features_extraction.calculators

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import com.intellij.psi.PsiField
import com.intellij.psi.PsiJavaCodeReferenceElement

class FieldAccessCalculator(candidates: List<ExtractionCandidate>
) : BaseCalculator(
        candidates,
        listOf(
                FeatureType.NUM_FIELD_ACCESS,
                FeatureType.CON_FIELD_ACCESS,
                FeatureType.FIELD_ACCESS_COUPLING,
                FeatureType.FIELD_ACCESS_COHESION,
                FeatureType.FIELD_ACCESS_COUPLING_2,
                FeatureType.FIELD_ACCESS_COHESION_2
        )
) {

    inner class FieldAccessStatementsMap : StatementsMap() {
        inner class Visitor : StatementsMap.Visitor() {
            override fun visitReferenceElement(reference: PsiJavaCodeReferenceElement?) {
                super.visitReferenceElement(reference)
                val elem = reference?.resolve() ?: return
                if (elem is PsiField)
                    addElem(elem)
            }
        }
    }

    override fun getStatementsMap() = FieldAccessStatementsMap()
}
