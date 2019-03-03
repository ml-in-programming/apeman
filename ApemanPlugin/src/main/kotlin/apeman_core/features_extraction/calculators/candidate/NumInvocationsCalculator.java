package apeman_core.features_extraction.calculators.candidate;

import apeman_core.base_entities.FeatureType;
import apeman_core.pipes.CandidateWithFeatures;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiNewExpression;
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate;

import java.util.ArrayList;

public class NumInvocationsCalculator extends AbstractNumCandidateCalculator {

    public NumInvocationsCalculator(ArrayList<CandidateWithFeatures> candidates) {
        super(candidates, FeatureType.NUM_INVOCATION);
    }

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
        return new NumInvocationsCalculator.Visitor();
    }

    private class Visitor extends CandidateVisitor {

        @Override
        public void visitMethodCallExpression(PsiMethodCallExpression expression) {
            super.visitMethodCallExpression(expression);
            if (!isInsideMethod)
                return;
            updateCounters();
        }

        @Override
        public void visitNewExpression(PsiNewExpression exp) {
            super.visitNewExpression(exp);
            if (exp.getArrayDimensions().length == 0 &&
                    exp.getArrayInitializer() == null && isInsideMethod) {
                updateCounters();
            }
        }
    }
}
