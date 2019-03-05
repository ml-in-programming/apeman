package apeman_core.features_extraction.calculators.method

import apeman_core.base_entities.FeatureType
import apeman_core.features_extraction.calculators.BaseMetricsCalculator
import apeman_core.pipes.CandidateWithFeatures
import apeman_core.utils.CandidateUtils
import apeman_core.utils.MethodUtils
import apeman_core.utils.TypeUtils
import com.intellij.psi.*

import java.util.ArrayList
import java.util.HashSet

class NumUsedTypesMethodCalculator(candidates: ArrayList<CandidateWithFeatures>
) : BaseMetricsCalculator(candidates, FeatureType.CON_TYPE_ACCESS) {

    private var methodNestingDepth = 0
    private var typeSet: MutableSet<PsiType>? = null

    override fun createVisitor(): JavaRecursiveElementVisitor {
        return Visitor()
    }

    private inner class Visitor : JavaRecursiveElementVisitor() {

        override fun visitMethod(method: PsiMethod) {
            if (methodNestingDepth == 0) {
                typeSet = HashSet()
            }
            TypeUtils.addTypesFromMethodTo(typeSet!!, method)

            methodNestingDepth++
            super.visitMethod(method)
            methodNestingDepth--
            if (methodNestingDepth == 0 && !MethodUtils.isAbstract(method)) {
                val res = typeSet!!.count().toDouble()
                CandidateUtils
                        .getCandidatesOfMethod(method, candidates)
                        .forEach { results.set(it, firstFeature, res) }
            }
        }

        override fun visitElement(element: PsiElement) {
            super.visitElement(element)
            TypeUtils.tryAddTypeOfElementTo(typeSet!!, element)
        }
    }
}
