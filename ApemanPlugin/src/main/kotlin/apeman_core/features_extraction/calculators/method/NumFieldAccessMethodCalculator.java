package apeman_core.features_extraction.calculators.method;

import apeman_core.base_entities.FeatureType;
import apeman_core.pipes.CandidateWithFeatures;
import com.intellij.psi.*;

import java.util.ArrayList;

public class NumFieldAccessMethodCalculator extends NumSimpleElementMethodCalculator {

    public NumFieldAccessMethodCalculator(ArrayList<CandidateWithFeatures> candidates) {
        super(candidates, FeatureType.CON_FIELD_ACCESS);
    }

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
        return new NumFieldAccessMethodCalculator.Visitor();
    }

    private class Visitor extends NumSimpleElementMethodCalculator.Visitor {
        PsiClass currentClass = null;

        @Override
        public void visitClass(PsiClass aClass) {
            currentClass = aClass;
            super.visitClass(aClass);
            currentClass = null;
        }

        @Override
        public void visitReferenceElement(PsiJavaCodeReferenceElement reference) {
            super.visitReferenceElement(reference);
            PsiElement elem = reference.resolve();
            if (elem instanceof PsiField && nestingDepth > 0 &&
                    ((PsiField)elem).getContainingClass() == currentClass
            ) {
                elementsCounter++;
            }
        }
    }
}
