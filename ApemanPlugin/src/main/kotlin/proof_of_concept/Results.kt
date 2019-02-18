package proof_of_concept

data class Results(
        val tolerance: Int,
        val apemanSize: Int,
        val oracleSize: Int,
        val precision: Double,
        val recall: Double
) {
    val fMeasure = 2 * precision * recall / (precision + recall)
}