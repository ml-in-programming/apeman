package apeman_core.features_extraction.calculators.candidate;

import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiSwitchStatement;
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate;

import java.util.ArrayList;

public class NumSwitchOperatorsCalculator extends AbstractNumCandidateCalculator {

    public NumSwitchOperatorsCalculator(ArrayList<ExtractionCandidate> candidates) {
        super(candidates);
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new NumSwitchOperatorsCalculator.Visitor();
    }

    private class Visitor extends CandidateVisitor {

        @Override
        public void visitSwitchStatement(PsiSwitchStatement statement) {
            super.visitSwitchStatement(statement);

            if (!isInsideMethod)
                return;
            updateCounters();
        }
    }
}
