package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import com.intellij.psi.*

class NumFieldAccessCandidateCalculator(candidates: List<ExtractionCandidate>
) : AbstractNumCandidateCalculator(candidates, FeatureType.NUM_FIELD_ACCESS) {

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

            if (elem is PsiField && isInsideMethod) {
                updateCounters()
            }
        }
    }
}
