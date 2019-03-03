package apeman_core.features_extraction.calculators

import apeman_core.base_entities.FeatureType
import apeman_core.pipes.CandidateWithFeatures

class Results(
        val features: List<FeatureType>,
        val candidates: List<CandidateWithFeatures>
) {
    private val results = HashMap<CandidateWithFeatures, ArrayList<Double>>()
    init {
        candidates.forEach { results[it] = ArrayList(features.count()) }
    }

    fun set(cand: CandidateWithFeatures, feature: FeatureType, value: Double) {
        val featureIndex = features.indexOf(feature)
        assert(featureIndex >= 0)

        results[cand]!![featureIndex] = value
    }

    fun resultForCandidate(cand: CandidateWithFeatures) = features.zip(results[cand]!!).toMap()
}
