package apeman_core.features_extraction.calculators

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType

class Results(
        val features: List<FeatureType>,
        val candidates: List<ExtractionCandidate>
) {
    private val results = HashMap<ExtractionCandidate, HashMap<FeatureType, Double>>()
    init {
        candidates.forEach { cand ->
            results[cand] = HashMap()
            features.forEach { feat -> results[cand]!![feat] = -1.0 }
        }
    }

    fun set(cand: ExtractionCandidate, feature: FeatureType, value: Double) {
        assert(results[cand]!![feature] == -1.0) {"$cand, $feature"}
        results[cand]!![feature] = value
    }

    fun resultForCandidate(cand: ExtractionCandidate): Map<FeatureType, Double> {
        assert(results[cand]!!.all { it.value != -1.0 }) {"$cand, ${results[cand]!!.filter { it.value == -1.0 }.map {it.key.name}.joinToString { it }}"}
        return results[cand]!!.toMap()
    }
}
