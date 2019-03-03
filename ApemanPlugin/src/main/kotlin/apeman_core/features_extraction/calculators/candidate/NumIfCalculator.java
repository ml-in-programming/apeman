package apeman_core.features_extraction.calculators.candidate;

import apeman_core.base_entities.FeatureType;
import apeman_core.pipes.CandidateWithFeatures;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiIfStatement;
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate;

import java.util.ArrayList;

public class NumIfCalculator extends AbstractNumCandidateCalculator {

    public NumIfCalculator(ArrayList<CandidateWithFeatures> candidates) {
        super(candidates, FeatureType.NUM_IF);
    }

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
        return new NumIfCalculator.Visitor();
    }

    private class Visitor extends CandidateVisitor {

        @Override
        public void visitIfStatement(PsiIfStatement statement) {
            super.visitIfStatement(statement);
            if (isInsideMethod) {
                updateCounters();
            }
        }
    }
}
