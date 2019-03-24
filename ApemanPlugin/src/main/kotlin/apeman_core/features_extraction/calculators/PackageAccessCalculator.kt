package apeman_core.features_extraction.calculators

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.utils.ClassUtils
import com.intellij.psi.PsiJavaCodeReferenceElement
import com.intellij.psi.PsiPackage
import com.intellij.psi.PsiPackageAccessibilityStatement
import com.intellij.psi.PsiVariable

class PackageAccessCalculator(candidates: List<ExtractionCandidate>
) : BaseCalculator(
        candidates,
        listOf(
                FeatureType.NUM_PACKAGE,
                FeatureType.CON_PACKAGE,
                FeatureType.PACKAGE_COUPLING,
                FeatureType.PACKAGE_COHESION,
                FeatureType.PACKAGE_COUPLING_2,
                FeatureType.PACKAGE_COHESION_2
        )
) {

    inner class PackageAccessStatementsMap : StatementsMap() {
        inner class Visitor : StatementsMap.Visitor() {
            override fun visitReferenceElement(reference: PsiJavaCodeReferenceElement?) {
                super.visitReferenceElement(reference)
                val elem = reference!!.resolve()
                val pack = ClassUtils.findPackage(elem)
                if (pack != null)
                    addElem(pack)
            }
        }
    }

    override fun getStatementsMap() = PackageAccessStatementsMap()
}
