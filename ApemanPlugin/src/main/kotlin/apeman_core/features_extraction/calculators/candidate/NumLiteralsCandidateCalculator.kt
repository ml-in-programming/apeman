package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.FeatureType
import apeman_core.pipes.CandidateWithFeatures
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiLiteralExpression

import java.util.ArrayList

class NumLiteralsCandidateCalculator(candidates: ArrayList<CandidateWithFeatures>) : AbstractNumCandidateCalculator(candidates, FeatureType.NUM_LITERAL) {

    override fun createVisitor() = Visitor()

    inner class Visitor : AbstractNumCandidateCalculator.CandidateVisitor() {

        override fun visitLiteralExpression(literal: PsiLiteralExpression) {
            super.visitLiteralExpression(literal)

            if (!isInsideMethod)
                return
            updateCounters()
        }
    }
}
