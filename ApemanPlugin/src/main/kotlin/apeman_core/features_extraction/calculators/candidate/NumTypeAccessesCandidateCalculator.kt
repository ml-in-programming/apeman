package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.pipes.CandidateWithFeatures
import apeman_core.utils.TypeUtils
import com.intellij.psi.*

import java.util.ArrayList
import java.util.HashSet

class NumTypeAccessesCandidateCalculator(candidates: List<ExtractionCandidate>
) : AbstractNumCandidateCalculator(candidates, FeatureType.NUM_TYPE_ACCESS) {

    override fun createVisitor() = Visitor()

    inner class Visitor : AbstractNumCandidateCalculator.CandidateVisitor() {

        internal var usedTypes: ArrayList<HashSet<PsiType>>? = null

        override fun initCounters() {
            if (usedTypes == null) {
                usedTypes = ArrayList()
            }
            usedTypes!!.clear()
            methodCandidates.forEach { usedTypes!!.add(HashSet()) }
        }

        override fun getCounterForCand(i: Int): Int {
            return usedTypes!![i].size
        }

        override fun visitMethod(method: PsiMethod) {
            super.visitMethod(method)
            if (!isInsideMethod)
                return

            for (i in methodCandidates.indices) {
                if (methodCandidates[i].isInCandidate) {
                    TypeUtils.addTypesFromMethodTo(usedTypes!![i], method)
                }
            }
        }

        override fun visitElement(element: PsiElement?) {
            super.visitElement(element)
            if (!isInsideMethod)
                return

            for (i in methodCandidates.indices) {
                if (methodCandidates[i].isInCandidate) {
                    TypeUtils.tryAddTypeOfElementTo(usedTypes!![i], element!!)
                }
            }
        }
    }
}
