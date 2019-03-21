package apeman_core.features_extraction.calculators.method

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.pipes.CandidateWithFeatures
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiAssertStatement
import com.intellij.psi.PsiAssignmentExpression

import java.util.ArrayList

class NumAssertMethodCalculator(candidates: List<ExtractionCandidate>
) : NumSimpleElementMethodCalculator(candidates, FeatureType.CON_ASSERT) {

    override fun createVisitor() = Visitor()

    inner class Visitor : NumSimpleElementMethodCalculator.Visitor() {

        override fun visitAssertStatement(statement: PsiAssertStatement?) {
            super.visitAssertStatement(statement)
            elementsCounter++
        }
    }
}
