package apeman_core.features_extraction.calculators.method;

import com.intellij.psi.*;
import com.sixrr.metrics.utils.ClassUtils;
import com.sixrr.metrics.utils.MethodUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NumUsedPackagesCalculator extends MethodCalculator {
    private int methodNestingDepth = 0;
    private Set<PsiPackage> usedPackages = null;

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
        public void visitMethod(PsiMethod method) {
            if (methodNestingDepth == 0) {
                usedPackages = new HashSet<PsiPackage>();
            }

            methodNestingDepth++;

            List<PsiPackage> containingPackages = Arrays.asList(
                    ClassUtils.calculatePackagesRecursive(method)
            );
            usedPackages.addAll(containingPackages);

            super.visitMethod(method);
            methodNestingDepth--;
            if (methodNestingDepth == 0 && !MethodUtils.isAbstract(method)) {
                postMetric(method, usedPackages.size());
            }
        }

        @Override
        public void visitReferenceElement(PsiJavaCodeReferenceElement reference) {
            super.visitReferenceElement(reference);
            if (methodNestingDepth == 0)
                return;

            PsiElement element = reference.resolve();
            if (element == null || element.getContainingFile() == null) // for packages, dirs etc
                return;
            List<PsiPackage> packages = Arrays.asList(ClassUtils.calculatePackagesRecursive(element));
            usedPackages.addAll(packages);
        }
    }
}
