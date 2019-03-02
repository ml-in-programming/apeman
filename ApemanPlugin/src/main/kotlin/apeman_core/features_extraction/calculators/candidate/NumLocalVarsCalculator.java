package apeman_core.features_extraction.calculators.candidate;

import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiLocalVariable;
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate;

import java.util.ArrayList;

public class NumLocalVarsCalculator extends AbstractNumCandidateCalculator {

    public NumLocalVarsCalculator(ArrayList<ExtractionCandidate> candidates) {
        super(candidates);
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new NumLocalVarsCalculator.Visitor();
    }

    private class Visitor extends CandidateVisitor {

        @Override
        public void visitLocalVariable(PsiLocalVariable variable) {
            super.visitLocalVariable(variable);
            if (isInsideMethod) {
                updateCounters();
            }
        }
    }
}
