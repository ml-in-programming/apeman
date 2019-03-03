package apeman_core.features_extraction.calculators.candidate;

import apeman_core.base_entities.FeatureType;
import apeman_core.pipes.CandidateWithFeatures;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiIfStatement;

import java.util.ArrayList;

public class NumIfCandidateCalculator extends AbstractNumCandidateCalculator {

    public NumIfCandidateCalculator(ArrayList<CandidateWithFeatures> candidates) {
        super(candidates, FeatureType.NUM_IF);
    }

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
        return new NumIfCandidateCalculator.Visitor();
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
