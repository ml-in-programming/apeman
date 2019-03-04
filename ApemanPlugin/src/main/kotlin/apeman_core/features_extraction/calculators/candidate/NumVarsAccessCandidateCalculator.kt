package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.FeatureType
import apeman_core.pipes.CandidateWithFeatures
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil

import java.util.ArrayList

class NumVarsAccessCandidateCalculator(candidates: ArrayList<CandidateWithFeatures>) : AbstractNumCandidateCalculator(candidates, FeatureType.NUM_VAR_ACCESS) {

    override fun createVisitor() = Visitor()

    inner class Visitor : AbstractNumCandidateCalculator.CandidateVisitor() {

        internal var currentMethod: PsiMethod? = null

        override fun visitMethod(method: PsiMethod) {
            if (methodNestingDepth == 0)
                currentMethod = method

            super.visitMethod(method)
            currentMethod = null
        }

        override fun visitLocalVariable(variable: PsiLocalVariable) {
            super.visitLocalVariable(variable)
            if (isInsideMethod)
                updateCounters()
        }

        override fun visitReferenceElement(reference: PsiJavaCodeReferenceElement) {
            super.visitReferenceElement(reference)
            val elem = reference.resolve()
            if (elem is PsiLocalVariable && isInsideMethod &&
                    PsiTreeUtil.isAncestor(currentMethod, elem, true)) {
                updateCounters()
            }
        }
    }
}
