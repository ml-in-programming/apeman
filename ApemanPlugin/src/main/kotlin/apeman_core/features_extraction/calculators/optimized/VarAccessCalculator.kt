package apeman_core.features_extraction.calculators.optimized

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.features_extraction.calculators.OptimizedCalculator
import apeman_core.features_extraction.calculators.StatementsMap
import com.intellij.psi.PsiJavaCodeReferenceElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiVariable
import com.intellij.psi.util.PsiTreeUtil

class VarAccessCalculator(candidates: List<ExtractionCandidate>
) : OptimizedCalculator(
        candidates,
        listOf(
                FeatureType.NUM_VAR_ACCESS,
                FeatureType.CON_VAR_ACCESS,
                FeatureType.VAR_ACCESS_COUPLING,
                FeatureType.VAR_ACCESS_COHESION,
                FeatureType.VAR_ACCESS_COUPLING_2,
                FeatureType.VAR_ACCESS_COHESION_2
        )
) {

    inner class VarAccessStatementsMap : StatementsMap() {
        inner class Visitor : StatementsMap.Visitor() {

            var nestingDepth = 0
            var currentMethod: PsiMethod? = null

            override fun visitMethod(method: PsiMethod?) {
                nestingDepth++
                if (nestingDepth == 1)
                    currentMethod = method
                super.visitMethod(method)
                if (nestingDepth == 1)
                    currentMethod = null
                nestingDepth--
            }

            override fun visitVariable(variable: PsiVariable?) {
                super.visitVariable(variable)
                addElement(variable!!)
            }

            override fun visitReferenceElement(reference: PsiJavaCodeReferenceElement?) {
                super.visitReferenceElement(reference)
                val elem = reference!!.resolve() ?: return
                if (elem is PsiVariable && PsiTreeUtil.isAncestor(currentMethod, elem, true)) {
                    addElement(elem)
                }
            }
        }

        override fun getVisitor() = Visitor()
    }

    override fun getStatementsMap() = VarAccessStatementsMap()
}
