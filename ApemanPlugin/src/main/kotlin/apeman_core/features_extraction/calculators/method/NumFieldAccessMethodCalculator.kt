package apeman_core.features_extraction.calculators.method

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import com.intellij.psi.*

class NumFieldAccessMethodCalculator(candidates: List<ExtractionCandidate>
) : NumSimpleElementMethodCalculator(candidates, FeatureType.CON_FIELD_ACCESS) {

    override fun createVisitor() = Visitor()

    inner class Visitor : NumSimpleElementMethodCalculator.Visitor() {
        internal var currentClass: PsiClass? = null

        override fun visitClass(aClass: PsiClass) {
            currentClass = aClass
            super.visitClass(aClass)
            currentClass = null
        }

        override fun visitReferenceElement(reference: PsiJavaCodeReferenceElement) {
            super.visitReferenceElement(reference)
            val elem = reference.resolve()
            if (elem is PsiField && nestingDepth > 0) {
                elementsCounter++
            }
        }
    }
}
