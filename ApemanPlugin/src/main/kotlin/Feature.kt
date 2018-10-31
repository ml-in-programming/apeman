data class Feature(val numInCandidate: Int, val numInComplement: Int) {
    val numInSource = numInCandidate + numInComplement
}