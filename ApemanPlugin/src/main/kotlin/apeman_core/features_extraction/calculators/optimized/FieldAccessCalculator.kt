package apeman_core.features_extraction.calculators.optimized

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.features_extraction.calculators.OptimizedCalculator
import apeman_core.features_extraction.calculators.StatementsMap
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.psi.PsiJavaCodeReferenceElement

class FieldAccessCalculator(candidates: List<ExtractionCandidate>
) : OptimizedCalculator(
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
        var containingClass: PsiClass? = null

        inner class Visitor : StatementsMap.Visitor() {
            override fun visitReferenceElement(reference: PsiJavaCodeReferenceElement?) {
                if (containingClass == null)
                    containingClass = method!!.containingClass

                super.visitReferenceElement(reference)
                val elem = reference?.resolve() ?: return
                if (elem is PsiField && elem.containingClass === containingClass)
                    addElement(elem)
            }
        }

        override fun getVisitor() = Visitor()
    }

    override fun getStatementsMap() = FieldAccessStatementsMap()
}
