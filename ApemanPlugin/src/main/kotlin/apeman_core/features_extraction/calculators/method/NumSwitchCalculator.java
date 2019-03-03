package apeman_core.features_extraction.calculators.method;

import apeman_core.base_entities.FeatureType;
import apeman_core.pipes.CandidateWithFeatures;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiSwitchStatement;

import java.util.ArrayList;

public class NumSwitchCalculator extends NumSimpleElementCalculator {

    public NumSwitchCalculator(ArrayList<CandidateWithFeatures> candidates) {
        super(candidates, FeatureType.CON_SWITCH);
    }

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
        return new NumSwitchCalculator.Visitor();
    }

    private class Visitor extends NumSimpleElementCalculator.Visitor {

        @Override
        public void visitSwitchStatement(PsiSwitchStatement statement) {
            super.visitSwitchStatement(statement);
            elementsCounter++;
        }
    }
}
