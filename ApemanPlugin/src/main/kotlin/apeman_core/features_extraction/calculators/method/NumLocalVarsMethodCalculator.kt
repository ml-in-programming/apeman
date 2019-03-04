package apeman_core.features_extraction.calculators.method

import apeman_core.base_entities.FeatureType
import apeman_core.pipes.CandidateWithFeatures
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiLocalVariable

import java.util.ArrayList

class NumLocalVarsMethodCalculator(candidates: ArrayList<CandidateWithFeatures>) : NumSimpleElementMethodCalculator(candidates, FeatureType.CON_VAR_ACCESS) {

    override fun createVisitor() = Visitor()

    inner class Visitor : NumSimpleElementMethodCalculator.Visitor() {

        override fun visitLocalVariable(variable: PsiLocalVariable) {
            super.visitLocalVariable(variable)
            elementsCounter++
        }
    }
}
