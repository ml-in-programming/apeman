package apeman_core.features_extraction.calculators.candidate;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiConditionalExpression;
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate;

import java.util.ArrayList;

public class NumTernaryOperatorsCalculator extends AbstractNumCandidateCalculator {

    public NumTernaryOperatorsCalculator(ArrayList<ExtractionCandidate> candidates) {
        super(candidates);
    }

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
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
