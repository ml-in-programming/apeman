package apeman_core.features_extraction.calculators.method;

import apeman_core.base_entities.FeatureType;
import apeman_core.pipes.CandidateWithFeatures;
import apeman_core.utils.MethodUtils;
import apeman_core.utils.TypeUtils;
import com.intellij.psi.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class NumUsedTypesMethodCalculator extends MethodCalculator {

    public NumUsedTypesMethodCalculator(ArrayList<CandidateWithFeatures> candidates) {
        super(candidates, FeatureType.CON_TYPE_ACCESS);
    }

    private int methodNestingDepth = 0;
    private Set<PsiType> typeSet = null;

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
        public void visitMethod(PsiMethod method) {
            if (methodNestingDepth == 0) {
                typeSet = new HashSet<PsiType>();
            }
            TypeUtils.addTypesFromMethodTo(typeSet, method);

            methodNestingDepth++;
            super.visitMethod(method);
            methodNestingDepth--;
            if (methodNestingDepth == 0 && !MethodUtils.isAbstract(method)) {
//                postMetric(method, typeSet.size());
            }
        }

        @Override
        public void visitElement(PsiElement element) {
            super.visitElement(element);
            TypeUtils.tryAddTypeOfElementTo(typeSet, element);
        }
    }
}
