package apeman_core.features_extraction.calculators.method;

import apeman_core.base_entities.FeatureType;
import apeman_core.pipes.CandidateWithFeatures;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiSwitchStatement;

import java.util.ArrayList;

public class NumSwitchMethodCalculator extends NumSimpleElementMethodCalculator {

    public NumSwitchMethodCalculator(ArrayList<CandidateWithFeatures> candidates) {
        super(candidates, FeatureType.CON_SWITCH);
    }

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
        return new NumSwitchMethodCalculator.Visitor();
    }

    private class Visitor extends NumSimpleElementMethodCalculator.Visitor {

        @Override
        public void visitSwitchStatement(PsiSwitchStatement statement) {
            super.visitSwitchStatement(statement);
            elementsCounter++;
        }
    }
}
