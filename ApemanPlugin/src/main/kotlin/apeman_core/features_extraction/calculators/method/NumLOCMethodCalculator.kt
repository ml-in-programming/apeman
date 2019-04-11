package apeman_core.features_extraction.calculators.method

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.utils.BlocksUtils
import apeman_core.utils.MethodUtils
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiStatement

class NumLOCMethodCalculator(candidates: List<ExtractionCandidate>
) : NumSimpleElementMethodCalculator(candidates, FeatureType.CON_LOC) {

    override fun createVisitor(methodCandidates: List<ExtractionCandidate>) = Visitor(methodCandidates)

    inner class Visitor(methodCandidates: List<ExtractionCandidate>
    ) : NumSimpleElementMethodCalculator.Visitor(methodCandidates) {
        override fun initElementsCounter(method: PsiMethod) {
            val methodBlock = BlocksUtils.getBlockFromMethod(method)
            elementsCounter = BlocksUtils.getNumStatementsRecursively(methodBlock)
        }
    }
}
