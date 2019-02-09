package apeman_core.grouping

import apeman_core.pipes.CandidatesWithFeaturesAndProba
import org.jetbrains.annotations.Contract

public class GettingBestCandidates(
        private val candidates: ArrayList<CandidatesWithFeaturesAndProba>
) {

    private val K = 5

    @Contract(pure = true)
    public fun getTopKCandidates(): ArrayList<CandidatesWithFeaturesAndProba> {

        return ArrayList(candidates
                .groupBy { it.candidate.sourceMethod }
                .values
                .flatMap {it
                        .sortedBy { it.probability }
                        .takeLast(K)
                })
    }
}