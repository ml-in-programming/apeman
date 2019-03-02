package apeman_core.features_extraction.calculators.candidate;

import com.intellij.psi.PsiConditionalExpression;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate;

import java.util.ArrayList;

public class NumTernaryOperatorsCalculator extends AbstractNumCandidateCalculator {

    public NumTernaryOperatorsCalculator(ArrayList<ExtractionCandidate> candidates) {
        super(candidates);
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new NumTernaryOperatorsCalculator.Visitor();
    }

    private class Visitor extends CandidateVisitor {

        @Override
        public void visitConditionalExpression(PsiConditionalExpression expression) {
            super.visitConditionalExpression(expression);

            if (!isInsideMethod)
                return;
            updateCounters();
        }
    }
}
