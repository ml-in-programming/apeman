package apeman_core.features_extraction.calculators.candidate;

import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiIfStatement;
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate;

import java.util.ArrayList;

public class NumIfCalculator extends AbstractNumCandidateCalculator {

    public NumIfCalculator(ArrayList<ExtractionCandidate> candidates) {
        super(candidates);
    }

    @Override
    protected PsiElementVisitor createVisitor() {
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
