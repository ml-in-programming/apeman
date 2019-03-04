package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.FeatureType
import apeman_core.pipes.CandidateWithFeatures
import apeman_core.utils.ClassUtils
import com.intellij.psi.*

import java.util.ArrayList
import java.util.Arrays
import java.util.HashSet

class NumPackageAccessesCandidateCalculator(candidates: ArrayList<CandidateWithFeatures>) : AbstractNumCandidateCalculator(candidates, FeatureType.NUM_PACKAGE) {

    override fun createVisitor() = Visitor()

    inner class Visitor : AbstractNumCandidateCalculator.CandidateVisitor() {

        internal var usedPackages: ArrayList<HashSet<PsiPackage>>? = null

        override fun initCounters() {
            if (usedPackages == null) {
                usedPackages = ArrayList()
            }
            usedPackages!!.clear()
            repeat(methodCandidates.size) { usedPackages!!.add(HashSet()) }
        }

        override fun getCounterForCand(i: Int) = usedPackages!![i].size

        override fun visitMethod(method: PsiMethod) {
            super.visitMethod(method)
            val containingPackages = ClassUtils.calculatePackagesRecursive(method).toList()

            for (i in methodCandidates.indices) {
                usedPackages!![i].addAll(containingPackages)
            }
        }

        override fun visitReferenceElement(reference: PsiJavaCodeReferenceElement) {
            super.visitReferenceElement(reference)
            if (!isInsideMethod)
                return

            val element = reference.resolve()
            if (element == null || element.containingFile == null)
            // for packages, dirs etc
                return

            val packages = Arrays.asList(*ClassUtils.calculatePackagesRecursive(element))

            for (i in methodCandidates.indices) {
                if (methodCandidates[i].candidate.isInCandidate) {
                    usedPackages!![i].addAll(packages)
                }
            }
        }
    }
}
