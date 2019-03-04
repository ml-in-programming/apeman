package apeman_core.features_extraction

import apeman_core.features_extraction.metrics.Metric
import apeman_core.pipes.CandidateWithFeatures
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.util.ProgressIndicatorBase

class FeaturesCalculationRunner(
        private val candidates: List<CandidateWithFeatures>,
        private val metrics: List<Metric>
) {

    fun calculate() {
        val calculators = metrics.flatMap { it.metrics }.distinct()
        val visitors = calculators.map { it.createVisitor() }
        val methods = candidates.map { it.candidate.sourceMethod }.distinct()

        ProgressManager.getInstance().runProcess({

            for (method in methods)
                for (visitor in visitors)
                    method.accept(visitor)

        }, ProgressIndicatorBase ())
    }
}
