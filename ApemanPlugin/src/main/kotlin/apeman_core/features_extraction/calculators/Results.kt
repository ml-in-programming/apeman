package apeman_core.features_extraction.calculators

import apeman_core.base_entities.FeatureType
import apeman_core.pipes.CandidateWithFeatures

class Results(
        val features: List<FeatureType>,
        val candidates: List<CandidateWithFeatures>
) {
    private val results = HashMap<CandidateWithFeatures, HashMap<FeatureType, Double>>()
    init {
        candidates.forEach { cand ->
            results[cand] = HashMap()
            features.forEach { feat -> results[cand]!![feat] = -1.0 }
        }
    }

    fun set(cand: CandidateWithFeatures, feature: FeatureType, value: Double) {
        assert(results[cand]!![feature] == -1.0)
        results[cand]!![feature] = value
    }

    fun resultForCandidate(cand: CandidateWithFeatures): Map<FeatureType, Double> {
        assert(results[cand]!!.all { it.value != -1.0 })
        return results[cand]!!.toMap()
    }
}
