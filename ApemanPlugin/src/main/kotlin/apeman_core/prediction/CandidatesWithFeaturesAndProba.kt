package apeman_core.prediction

import apeman_core.features_extraction.Feature
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate

data class CandidatesWithFeaturesAndProba(
        val candidate: ExtractionCandidate,
        val features: List<Feature>,
        val probability: Double
)