package apeman_core.features_extraction.calculators.candidate;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiLiteralExpression;
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate;

import java.util.ArrayList;

public class NumLiteralsCandidateCalculator extends AbstractNumCandidateCalculator {

    public NumLiteralsCandidateCalculator(ArrayList<ExtractionCandidate> candidates) {
        super(candidates);
    }

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
        return new NumLiteralsCandidateCalculator.Visitor();
    }

    private class Visitor extends CandidateVisitor {

        @Override
        public void visitLiteralExpression(PsiLiteralExpression literal) {
            super.visitLiteralExpression(literal);

            if (!isInsideMethod)
                return;
            updateCounters();
        }
    }
}
