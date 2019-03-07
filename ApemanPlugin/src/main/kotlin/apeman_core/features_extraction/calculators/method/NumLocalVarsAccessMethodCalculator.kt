package apeman_core.features_extraction.calculators.method

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.pipes.CandidateWithFeatures
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil

import java.util.ArrayList

class NumLocalVarsAccessMethodCalculator(candidates: List<ExtractionCandidate>
) : NumSimpleElementMethodCalculator(candidates, FeatureType.CON_VAR_ACCESS) {

    override fun createVisitor() = Visitor()

    inner class Visitor : NumSimpleElementMethodCalculator.Visitor() {
        private var currentMethod: PsiMethod? = null

        override fun visitMethod(method: PsiMethod) {
            if (nestingDepth == 0)
                currentMethod = method
            super.visitMethod(method)
            currentMethod = null
        }

        override fun visitLocalVariable(variable: PsiLocalVariable) {
            super.visitLocalVariable(variable)
            elementsCounter++
        }

        override fun visitReferenceElement(reference: PsiJavaCodeReferenceElement) {
            super.visitReferenceElement(reference)
            val elem = reference.resolve()
            if (elem is PsiLocalVariable && PsiTreeUtil.isAncestor(currentMethod, elem, true)) {
                elementsCounter++
            }
        }
    }
}