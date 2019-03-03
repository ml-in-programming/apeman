package apeman_core.features_extraction

import apeman_core.features_extraction.metrics.Metric
import apeman_core.pipes.CandidateWithFeatures
import com.intellij.analysis.AnalysisScope
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.util.ProgressIndicatorBase
import com.intellij.openapi.project.Project
import com.sixrr.metrics.MetricCategory
import com.sixrr.metrics.metricModel.MetricsExecutionContextImpl
import com.sixrr.metrics.metricModel.MetricsResult
import com.sixrr.metrics.metricModel.MetricsRunImpl
import com.sixrr.metrics.metricModel.TimeStamp
import com.sixrr.metrics.profile.MetricInstance
import com.sixrr.metrics.profile.MetricsProfile
import com.sixrr.metrics.profile.MetricsProfileImpl

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
