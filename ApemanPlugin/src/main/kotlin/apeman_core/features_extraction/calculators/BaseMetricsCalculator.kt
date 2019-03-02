package apeman_core.features_extraction.calculators

import com.intellij.analysis.AnalysisScope
import com.intellij.psi.JavaRecursiveElementVisitor

abstract class BaseMetricsCalculator {
    abstract fun createVisitor(): JavaRecursiveElementVisitor
}
