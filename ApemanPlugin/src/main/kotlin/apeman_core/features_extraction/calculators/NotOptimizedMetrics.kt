package apeman_core.features_extraction.calculators

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiMethod

abstract class NotOptimizedMetrics @JvmOverloads constructor(
        protected val candidates: List<ExtractionCandidate>,
        feature: FeatureType? = null,
        features: List<FeatureType>? = null
) : BaseCalculator() {
    init {
        assert((feature != null) xor (features != null))
    }

    protected val features = features ?: arrayListOf(feature!!)
    protected val firstFeature= this.features[0]
    override val results = Results(this.features, candidates)

    abstract fun createVisitor(methodCandidates: List<ExtractionCandidate>): JavaRecursiveElementVisitor

    override fun calculateMethod(method: PsiMethod, methodCandidates: List<ExtractionCandidate>) {
        method.accept(createVisitor(methodCandidates))
    }
}
