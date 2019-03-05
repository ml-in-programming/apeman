package apeman_core.features_extraction.calculators.method

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.pipes.CandidateWithFeatures
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiSwitchStatement

import java.util.ArrayList

class NumSwitchMethodCalculator(candidates: List<ExtractionCandidate>
) : NumSimpleElementMethodCalculator(candidates, FeatureType.CON_SWITCH) {

    override fun createVisitor() = Visitor()

    inner class Visitor : NumSimpleElementMethodCalculator.Visitor() {

        override fun visitSwitchStatement(statement: PsiSwitchStatement) {
            super.visitSwitchStatement(statement)
            elementsCounter++
        }
    }
}
