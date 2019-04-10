package proof_of_concept

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.CandidatesWithFeaturesAndProba

data class Results(
        val tolerance: Int,
        val oracleCandidates: List<ExtractionCandidate>,
        val candidates: List<CandidatesWithFeaturesAndProba>
) {
    val truePositives = calculateTruePositives()
    val apemanSize = candidates.count()
    val oracleSize = oracleCandidates.count()
    val precision = if (apemanSize != 0)
        truePositives.count() / apemanSize.toDouble()
    else
        0.0
    val recall = if (oracleSize != 0)
        truePositives.count() / oracleSize.toDouble()
    else
        0.0
    val fScore = if (precision + recall > 0.001)
        2 * precision * recall / (precision + recall)
    else
        0.0

    private fun calculateTruePositives(): List<CandidatesWithFeaturesAndProba> {
        val truePositives = arrayListOf<CandidatesWithFeaturesAndProba>()

        for (oracleCand in oracleCandidates) {
            val candSameMethod = candidates.filter { (cand, _, _) ->
                cand.sourceMethod == oracleCand.sourceMethod
            }
            val oracleLines = oracleCand.toString().split("\n")

            val sameCand = candSameMethod.firstOrNull { (cand, _, _) ->
                val (same, notSame) = cand.toString()
                        .split("\n")
                        .partition { line -> oracleLines.contains(line) }
                val maxDiff = 2 * tolerance
                return@firstOrNull notSame.count() <= maxDiff && same.count() > oracleLines.count() - maxDiff

            }
            if (sameCand != null)
                truePositives.add(sameCand)
        }
        return truePositives
    }

    override fun toString(): String {
        return "tolerance = $tolerance,\n" +
                "oracle = $oracleSize,\n" +
                "apeman = $apemanSize,\n" +
                "true positives = ${truePositives.count()},\n" +
                "precision = $precision,\n" +
                "recall = $recall,\n" +
                "f-score = $fScore"
    }
}
