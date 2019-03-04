package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.BlockOfMethod
import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.features_extraction.calculators.BaseMetricsCalculator
import apeman_core.pipes.CandidateWithFeatures
import apeman_core.utils.BlocksUtils
import apeman_core.utils.CandidateUtils
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiMethod

import java.util.ArrayList

class RatioLocCandidateCalculator(candidates: ArrayList<CandidateWithFeatures>) : BaseMetricsCalculator(candidates, FeatureType.LOC_RATIO) {

    override fun createVisitor(): JavaRecursiveElementVisitor {
        return object : JavaRecursiveElementVisitor() {

            override fun visitMethod(method: PsiMethod) {
                super.visitMethod(method)
                if (method.body == null)
                // abstract method or interface
                    return
                val blockOfMethod = BlocksUtils.getBlockFromMethod(method)
                val candidatesOfMethod = CandidateUtils.getCandidatesOfMethod(method, candidates)
                val numStatementsMethod = BlocksUtils.getNumStatementsRecursively(blockOfMethod)

                for (cand in candidatesOfMethod) {
                    val candStatements = BlocksUtils.getNumStatementsRecursively(cand.candidate.block)
                    results.set(cand, firstFeature, candStatements / numStatementsMethod.toDouble())
                }
            }
        }
    }
}
