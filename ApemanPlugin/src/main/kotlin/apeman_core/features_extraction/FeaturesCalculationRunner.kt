package apeman_core.features_extraction

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.features_extraction.metrics.Metric
import apeman_core.utils.CandidateUtils
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.util.ProgressIndicatorBase
import java.util.logging.Logger

class FeaturesCalculationRunner(
        private val candidates: List<ExtractionCandidate>,
        private val metrics: List<Metric>
) {
    val log = Logger.getGlobal()

    fun calculate() {
        val calculators = metrics.flatMap { it.calculators }.distinct()
        val methods = candidates.map { it.sourceMethod }.distinct()

        ProgressManager.getInstance().runProcess({

            for ((index, method) in methods.withIndex()) {
                if (index % 100 == 0)
                    log.info(index.toString())
                val methodCandidates = CandidateUtils.getCandidatesOfMethod(method, candidates)
                for (calculator in calculators)
                    calculator.calculateMethod(method, methodCandidates)
            }

        }, ProgressIndicatorBase ())
    }
}
