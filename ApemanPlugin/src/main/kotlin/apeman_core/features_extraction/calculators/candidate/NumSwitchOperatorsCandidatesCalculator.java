package apeman_core.features_extraction.calculators.candidate;

import apeman_core.base_entities.FeatureType;
import apeman_core.pipes.CandidateWithFeatures;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiSwitchStatement;

import java.util.ArrayList;

public class NumSwitchOperatorsCandidatesCalculator extends AbstractNumCandidateCalculator {

    public NumSwitchOperatorsCandidatesCalculator(ArrayList<CandidateWithFeatures> candidates) {
        super(candidates, FeatureType.NUM_SWITCH);
    }

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
        return new NumSwitchOperatorsCandidatesCalculator.Visitor();
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
