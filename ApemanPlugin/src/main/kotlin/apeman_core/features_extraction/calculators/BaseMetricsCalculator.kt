package apeman_core.features_extraction.calculators

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.pipes.CandidateWithFeatures
import com.intellij.psi.JavaRecursiveElementVisitor

abstract class BaseMetricsCalculator @JvmOverloads constructor(
        protected val candidates: List<ExtractionCandidate>,
        feature: FeatureType? = null,
        features: List<FeatureType>? = null
) {
    init {
        assert((feature != null) xor (features != null))
    }

    protected val features = features ?: arrayListOf(feature!!)
    protected val firstFeature= this.features[0]
    val results = Results(this.features, candidates)

    abstract fun createVisitor(): JavaRecursiveElementVisitor
}
