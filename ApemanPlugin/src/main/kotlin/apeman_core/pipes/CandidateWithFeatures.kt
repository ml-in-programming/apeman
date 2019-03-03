package apeman_core.pipes

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.base_entities.Features

data class CandidateWithFeatures(
        val candidate: ExtractionCandidate,
        val features: Features = Features(FeatureType::class.java)
)
