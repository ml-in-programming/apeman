package apeman_core.features_extraction.calculators.candidate;

import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiTypeElement;
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate;

import java.util.ArrayList;

public class NumTypedElementsCandidateCalculator extends AbstractNumCandidateCalculator {

    public NumTypedElementsCandidateCalculator(ArrayList<ExtractionCandidate> candidates) {
        super(candidates);
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new NumTypedElementsCandidateCalculator.Visitor();
    }

    private class Visitor extends CandidateVisitor {

        @Override
        public void visitTypeElement(PsiTypeElement type) {
            super.visitTypeElement(type);
            if (isInsideMethod) {
                updateCounters();
            }
        }
    }
}
