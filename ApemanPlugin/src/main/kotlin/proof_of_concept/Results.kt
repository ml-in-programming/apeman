package proof_of_concept

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.pipes.CandidatesWithFeaturesAndProba

data class Results(
        val tolerance: Int,
        val apemanSize: Int,
        val oracleSize: Int,
        val precision: Double,
        val recall: Double,
        val oracleCandidates: List<ExtractionCandidate>,
        val candidates: List<CandidatesWithFeaturesAndProba>
) {
    val fMeasure = 2 * precision * recall / (precision + recall)
}