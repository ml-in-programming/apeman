package apeman_core.features_extraction.calculators

import apeman_core.base_entities.FeatureType
import apeman_core.pipes.CandidateWithFeatures
import com.intellij.psi.JavaRecursiveElementVisitor

abstract class BaseMetricsCalculator @JvmOverloads constructor(
        protected val candidates: ArrayList<CandidateWithFeatures>,
        feature: FeatureType? = null,
        features: ArrayList<FeatureType>? = null
) {
    protected val features = features ?: arrayListOf(feature)
    abstract fun createVisitor(): JavaRecursiveElementVisitor
}
