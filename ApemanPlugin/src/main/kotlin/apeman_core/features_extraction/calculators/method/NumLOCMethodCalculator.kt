package apeman_core.features_extraction.calculators.method

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import com.intellij.psi.PsiStatement

class NumLOCMethodCalculator(candidates: List<ExtractionCandidate>
) : NumSimpleElementMethodCalculator(candidates, FeatureType.CON_LOC) {

    override fun createVisitor(methodCandidates: List<ExtractionCandidate>) = Visitor()

    inner class Visitor : NumSimpleElementMethodCalculator.Visitor() {
        override fun visitStatement(statement: PsiStatement?) {
            super.visitStatement(statement)
            elementsCounter++
        }
    }
}
