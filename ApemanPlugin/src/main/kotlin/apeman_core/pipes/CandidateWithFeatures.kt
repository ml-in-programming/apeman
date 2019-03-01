package apeman_core.pipes

import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate

data class CandidateWithFeatures(
        val candidate: ExtractionCandidate,
        val features: Features
)
