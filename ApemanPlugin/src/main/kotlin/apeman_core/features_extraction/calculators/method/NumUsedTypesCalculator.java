package apeman_core.features_extraction.calculators.method;

import com.intellij.psi.*;
import com.sixrr.metrics.utils.MethodUtils;
import com.sixrr.stockmetrics.methodCalculators.MethodCalculator;
import com.sixrr.stockmetrics.utils.TypeUtils;

import java.util.HashSet;
import java.util.Set;

public class NumUsedTypesCalculator extends MethodCalculator {
    private int methodNestingDepth = 0;
    private Set<PsiType> typeSet = null;

    @Override
    protected PsiElementVisitor createVisitor() {
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
                postMetric(method, typeSet.size());
            }
        }

        @Override
        public void visitElement(PsiElement element) {
            super.visitElement(element);
            TypeUtils.tryAddTypeOfElementTo(typeSet, element);
        }
    }
}
