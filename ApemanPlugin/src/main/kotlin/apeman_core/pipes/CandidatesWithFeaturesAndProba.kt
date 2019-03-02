package apeman_core.pipes

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.Features

data class CandidatesWithFeaturesAndProba(
        val candidate: ExtractionCandidate,
        val features: Features,
        val probability: Double
)