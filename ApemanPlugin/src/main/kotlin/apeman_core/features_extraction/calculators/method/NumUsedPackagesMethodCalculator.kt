package apeman_core.features_extraction.calculators.method

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.utils.ClassUtils
import com.intellij.psi.*

class NumUsedPackagesMethodCalculator(candidates: List<ExtractionCandidate>
) : NumSimpleElementMethodCalculator(candidates, FeatureType.CON_PACKAGE) {

    override fun createVisitor(): NumSimpleElementMethodCalculator.Visitor {
        return Visitor()
    }

    private inner class Visitor : NumSimpleElementMethodCalculator.Visitor() {

//        override fun initElementsCounter(method: PsiMethod) {
//            val psiPackage = ClassUtils.findPackage(method)
//            elementsCounter = if (psiPackage == null) 0 else 1
//        }

        override fun visitReferenceElement(reference: PsiJavaCodeReferenceElement) {
            super.visitReferenceElement(reference)

            if (nestingDepth == 0) return
            val element = reference.resolve() ?: return
            ClassUtils.findPackage(element) ?: return

            elementsCounter++
        }
    }
}
