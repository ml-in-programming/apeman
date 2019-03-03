package apeman_core.features_extraction.calculators.method;

import apeman_core.base_entities.FeatureType;
import apeman_core.features_extraction.calculators.BaseMetricsCalculator;
import apeman_core.pipes.CandidateWithFeatures;

import java.util.List;

public abstract class MethodCalculator extends BaseMetricsCalculator {
    public MethodCalculator(List<CandidateWithFeatures> candidates, FeatureType neededFeature) {
        super(candidates, neededFeature);
    }
}
