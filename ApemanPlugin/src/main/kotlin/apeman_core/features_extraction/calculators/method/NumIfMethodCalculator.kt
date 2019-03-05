package apeman_core.features_extraction.calculators.method

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.pipes.CandidateWithFeatures
import com.intellij.psi.PsiIfStatement

import java.util.ArrayList

class NumIfMethodCalculator(candidates: List<ExtractionCandidate>
) : NumSimpleElementMethodCalculator(candidates, FeatureType.CON_IF) {

    override fun createVisitor() = Visitor()

    inner class Visitor : NumSimpleElementMethodCalculator.Visitor() {

        override fun visitIfStatement(statement: PsiIfStatement) {
            super.visitIfStatement(statement)
            elementsCounter++
        }
    }
}
