package apeman_core.features_extraction

import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate

data class CandidatesWithFeatures(
        val candidate: ExtractionCandidate,
        val features: List<Features>
)
