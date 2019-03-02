package apeman_core.features_extraction.calculators.candidate;

import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiNewExpression;
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate;

import java.util.ArrayList;

public class NumInvocationsCalculator extends AbstractNumCandidateCalculator {

    public NumInvocationsCalculator(ArrayList<ExtractionCandidate> candidates) {
        super(candidates);
    }

    @Override
    protected PsiElementVisitor createVisitor() {
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
