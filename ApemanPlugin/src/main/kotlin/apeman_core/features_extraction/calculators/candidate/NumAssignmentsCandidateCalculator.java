package apeman_core.features_extraction.calculators.candidate;

import apeman_core.base_entities.FeatureType;
import apeman_core.pipes.CandidateWithFeatures;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiAssignmentExpression;

import java.util.ArrayList;

public class NumAssignmentsCandidateCalculator extends AbstractNumCandidateCalculator {

    public NumAssignmentsCandidateCalculator(ArrayList<CandidateWithFeatures> candidates) {
        super(candidates, FeatureType.NUM_ASSIGN);
    }

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
        return new NumAssignmentsCandidateCalculator.Visitor();
    }

    private class Visitor extends CandidateVisitor {

        @Override
        public void visitAssignmentExpression(PsiAssignmentExpression expression) {
            super.visitAssignmentExpression(expression);

            if (isInsideMethod) {
                updateCounters();
            }
        }
    }
}
