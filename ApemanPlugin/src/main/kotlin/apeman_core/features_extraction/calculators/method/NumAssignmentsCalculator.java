package apeman_core.features_extraction.calculators.method;

import apeman_core.base_entities.FeatureType;
import apeman_core.pipes.CandidateWithFeatures;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiAssignmentExpression;

import java.util.ArrayList;

public class NumAssignmentsCalculator extends NumSimpleElementCalculator {

    public NumAssignmentsCalculator(ArrayList<CandidateWithFeatures> candidates) {
        super(candidates, FeatureType.CON_ASSIGN);
    }

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends NumSimpleElementCalculator.Visitor {

        @Override
        public void visitAssignmentExpression(PsiAssignmentExpression expression) {
            super.visitAssignmentExpression(expression);
            elementsCounter++;
        }
    }
}
