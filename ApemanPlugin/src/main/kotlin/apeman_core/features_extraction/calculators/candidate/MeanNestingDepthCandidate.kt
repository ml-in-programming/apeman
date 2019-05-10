package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.utils.BlocksUtils
import com.intellij.psi.PsiCodeBlock
import com.intellij.psi.PsiStatement

class MeanNestingDepthCandidate(candidates: List<ExtractionCandidate>
) : AbstractNumCandidate(candidates, FeatureType.MEAN_NESTING_DEPTH) {
    override fun createVisitor(methodCandidates: List<ExtractionCandidate>) = object : CandidateVisitor(methodCandidates) {
        var currentNestingDepth = 0

        override fun abstractVisitStatement(statement: PsiStatement) {
            if (statement.parent is PsiCodeBlock) {
                updateCounters()
            }
        }

        override fun visitCodeBlock(block: PsiCodeBlock?) {
            currentNestingDepth++
            super.visitCodeBlock(block)
            currentNestingDepth--
        }

        override fun updateCounter(i: Int) {
            if (methodCandidates[i].isInCandidate)
                counts[i] += currentNestingDepth
        }

        override fun getCounterForCand(i: Int): Double {
            val statements = BlocksUtils.getNumStatementsRecursively(methodCandidates[i].block)
            return counts[i] / statements.toDouble()
        }
    }
}
