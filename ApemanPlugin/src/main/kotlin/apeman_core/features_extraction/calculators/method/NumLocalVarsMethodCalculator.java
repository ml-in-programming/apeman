package apeman_core.features_extraction.calculators.method;

import apeman_core.base_entities.FeatureType;
import apeman_core.pipes.CandidateWithFeatures;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiLocalVariable;

import java.util.ArrayList;

public class NumLocalVarsMethodCalculator extends NumSimpleElementMethodCalculator {

    public NumLocalVarsMethodCalculator(ArrayList<CandidateWithFeatures> candidates) {
        super(candidates, FeatureType.CON_VAR_ACCESS);
    }

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends NumSimpleElementMethodCalculator.Visitor {

        @Override
        public void visitLocalVariable(PsiLocalVariable variable) {
            super.visitLocalVariable(variable);
            elementsCounter++;
        }
    }
}
