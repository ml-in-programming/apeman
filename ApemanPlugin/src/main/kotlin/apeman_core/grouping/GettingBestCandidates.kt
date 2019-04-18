package apeman_core.grouping

import apeman_core.base_entities.CandidatesWithFeaturesAndProba
import apeman_core.utils.BlocksUtils
import org.jetbrains.annotations.Contract

class GettingBestCandidates {

    @Contract(pure = true)
    fun getTopKCandidates(candidates: List<CandidatesWithFeaturesAndProba>, k: Int = 5): ArrayList<CandidatesWithFeaturesAndProba> {

        return ArrayList(candidates
                .groupBy { it.candidate.sourceMethod }
                .values
                .flatMap {it
                        .sortedBy { it.probability }
                        .takeLast(k)
                })
    }

    @Contract(pure = true)
    fun getByBarrier(candidates: List<CandidatesWithFeaturesAndProba>, proba: Double = 0.8): List<CandidatesWithFeaturesAndProba> {
            return candidates.filter {
                    it.probability > proba
            }
    }

    @Contract(pure = true)
    fun getGroupedCandidates(
            candidates: List<CandidatesWithFeaturesAndProba>,
            k: Int = 5,
            overlapCoef: Double = 0.5,
            differenceCoef: Double = 0.5
    ): ArrayList<CandidatesWithFeaturesAndProba> {

        val groupedCandidates = arrayListOf<CandidatesWithFeaturesAndProba>()

        candidates.groupBy { it.candidate.sourceMethod }.forEach {_, candidates ->
            val topCands = ArrayList(candidates.sortedByDescending { it.probability })
            val groupForMethod = arrayListOf<CandidatesWithFeaturesAndProba>()

            while (groupForMethod.count() < k && topCands.count() > 0) {
                val topCand = topCands[0]
                topCands.removeAt(0)

                val statements = BlocksUtils.getStatementsRecursivly(topCand.candidate.block)
                groupForMethod.add(topCand)
                topCands.removeIf {
                    val statements2 = BlocksUtils.getStatementsRecursivly(it.candidate.block)
                    val (maxStatements, minStatements) = if (statements.count() > statements2.count())
                        statements.toHashSet() to statements2.toHashSet()
                    else
                        statements2.toHashSet() to statements.toHashSet()

                    val differenceInSize = (maxStatements.count() - minStatements.count()) / minStatements.count().toDouble()
                    val overlap = maxStatements.minus(minStatements).count() / maxStatements.count().toDouble()

                    overlap > overlapCoef && differenceInSize < differenceCoef
                }
            }
            groupedCandidates.addAll(groupForMethod)
        }
        return groupedCandidates
    }
}
