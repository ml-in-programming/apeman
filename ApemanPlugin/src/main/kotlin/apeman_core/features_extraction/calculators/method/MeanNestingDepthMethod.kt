package apeman_core.features_extraction.calculators.method

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.utils.BlocksUtils
import com.intellij.psi.PsiCodeBlock
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiStatement
import org.bouncycastle.crypto.tls.CipherType.block

class MeanNestingDepthMethod(candidates: List<ExtractionCandidate>
) : NumSimpleElementMethod(candidates, FeatureType.METHOD_MEAN_NESTING_DEPTH) {

    override fun createVisitor(methodCandidates: List<ExtractionCandidate>) = Visitor(methodCandidates)

    inner class Visitor(methodCandidates: List<ExtractionCandidate>
    ) : NumSimpleElementMethod.Visitor(methodCandidates) {
        var currentStatementNestingDepth = 0
        var numStatementsOverall = 0

        override fun visitStatement(statement: PsiStatement?) {
            super.visitStatement(statement)
            elementsCounter += currentStatementNestingDepth
        }

        override fun visitCodeBlock(block: PsiCodeBlock?) {
            currentStatementNestingDepth++
            super.visitCodeBlock(block)
            currentStatementNestingDepth--
        }

        override fun initElementsCounter(method: PsiMethod) {
            super.initElementsCounter(method)
            val block = BlocksUtils.getBlockFromMethod(method)
            numStatementsOverall = BlocksUtils.getNumStatementsRecursively(block)
        }

        override fun getResultElementCounter(method: PsiMethod): Double {
            return elementsCounter.toDouble() / numStatementsOverall
        }
    }
}
