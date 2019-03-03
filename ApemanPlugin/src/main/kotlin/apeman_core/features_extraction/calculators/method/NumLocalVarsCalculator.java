package apeman_core.features_extraction.calculators.method;

import apeman_core.base_entities.FeatureType;
import apeman_core.pipes.CandidateWithFeatures;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiLocalVariable;

import java.util.ArrayList;

public class NumLocalVarsCalculator extends NumSimpleElementCalculator {

    public NumLocalVarsCalculator(ArrayList<CandidateWithFeatures> candidates) {
        super(candidates, FeatureType.CON_VAR_ACCESS);
    }

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends NumSimpleElementCalculator.Visitor {

        @Override
        public void visitLocalVariable(PsiLocalVariable variable) {
            super.visitLocalVariable(variable);
            elementsCounter++;
        }
    }
}
