package apeman_core.pipes

import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate

data class CandidatesWithFeaturesAndProba(
        val candidate: ExtractionCandidate,
        val features: Features,
        val probability: Double
)