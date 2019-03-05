package apeman_core.features_extraction.calculators.method

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.features_extraction.calculators.BaseMetricsCalculator
import apeman_core.pipes.CandidateWithFeatures
import apeman_core.utils.CandidateUtils
import apeman_core.utils.ClassUtils
import apeman_core.utils.MethodUtils
import com.intellij.psi.*

import java.util.*

class NumUsedPackagesMethodCalculator(candidates: List<ExtractionCandidate>
) : BaseMetricsCalculator(candidates, FeatureType.CON_PACKAGE) {

    private var methodNestingDepth = 0
    private var usedPackages: MutableSet<PsiPackage>? = null

    override fun createVisitor(): JavaRecursiveElementVisitor {
        return Visitor()
    }

    private inner class Visitor : JavaRecursiveElementVisitor() {

        override fun visitMethod(method: PsiMethod) {
            if (methodNestingDepth == 0) {
                usedPackages = HashSet()
            }

            methodNestingDepth++

            val containingPackages = ClassUtils
                    .calculatePackagesRecursive(method).toList()
            usedPackages!!.addAll(containingPackages)

            super.visitMethod(method)
            methodNestingDepth--
            if (methodNestingDepth == 0 && !MethodUtils.isAbstract(method)) {
                val res = usedPackages!!.count().toDouble()
                CandidateUtils
                        .getCandidatesOfMethod(method, candidates)
                        .forEach { results.set(it, firstFeature, res) }
            }
        }

        override fun visitReferenceElement(reference: PsiJavaCodeReferenceElement) {
            super.visitReferenceElement(reference)
            if (methodNestingDepth == 0)
                return

            val element = reference.resolve()
            if (element == null || element.containingFile == null) // for packages, dirs etc
                return
            val packages = ClassUtils.calculatePackagesRecursive(element).toList()
            usedPackages!!.addAll(packages)
        }
    }
}
