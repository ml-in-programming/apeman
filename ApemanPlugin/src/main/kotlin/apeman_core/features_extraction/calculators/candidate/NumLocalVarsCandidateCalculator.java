package apeman_core.features_extraction.calculators.candidate;

import apeman_core.base_entities.FeatureType;
import apeman_core.pipes.CandidateWithFeatures;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiLocalVariable;

import java.util.ArrayList;

public class NumLocalVarsCandidateCalculator extends AbstractNumCandidateCalculator {

    public NumLocalVarsCandidateCalculator(ArrayList<CandidateWithFeatures> candidates) {
        super(candidates, FeatureType.NUM_LOCAL);
    }

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
        return new NumLocalVarsCandidateCalculator.Visitor();
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
