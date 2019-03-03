package apeman_core.features_extraction.calculators.candidate;

import apeman_core.base_entities.FeatureType;
import apeman_core.pipes.CandidateWithFeatures;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiTypeElement;
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate;

import java.util.ArrayList;

public class NumTypedElementsCandidateCalculator extends AbstractNumCandidateCalculator {

    public NumTypedElementsCandidateCalculator(ArrayList<CandidateWithFeatures> candidates) {
        super(candidates, FeatureType.NUM_TYPED_ELEMENTS);
    }

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
        return new NumTypedElementsCandidateCalculator.Visitor();
    }

    private class Visitor extends CandidateVisitor {

        @Override
        public void visitTypeElement(PsiTypeElement type) {
            super.visitTypeElement(type);
            if (isInsideMethod) {
                updateCounters();
            }
        }
    }
}
