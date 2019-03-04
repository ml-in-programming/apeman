package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.FeatureType
import apeman_core.pipes.CandidateWithFeatures
import com.intellij.psi.*

import java.util.ArrayList

class NumFieldAccessCandidateCalculator(candidates: ArrayList<CandidateWithFeatures>) : AbstractNumCandidateCalculator(candidates, FeatureType.NUM_FIELD_ACCESS) {

    override fun createVisitor() = Visitor()

    inner class Visitor : AbstractNumCandidateCalculator.CandidateVisitor() {

        private var currentClass: PsiClass? = null

        override fun visitClass(aClass: PsiClass) {
            currentClass = aClass
            super.visitClass(aClass)
            currentClass = null
        }

        override fun visitReferenceElement(reference: PsiJavaCodeReferenceElement) {
            super.visitReferenceElement(reference)
            val elem = reference.resolve()

            if (elem is PsiField && isInsideMethod &&
                    elem.containingClass === currentClass) {
                updateCounters()
            }
        }
    }
}
