package apeman_core.features_extraction.calculators.candidate;

import apeman_core.base_entities.FeatureType;
import apeman_core.pipes.CandidateWithFeatures;
import apeman_core.utils.ClassUtils;
import com.intellij.psi.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class NumPackageAccessesCandidateCalculator extends AbstractNumCandidateCalculator {

    public NumPackageAccessesCandidateCalculator(ArrayList<CandidateWithFeatures> candidates) {
        super(candidates, FeatureType.NUM_PACKAGE);
    }

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
        return new NumPackageAccessesCandidateCalculator.Visitor();
    }

    private class Visitor extends CandidateVisitor {

        ArrayList<HashSet<PsiPackage>> usedPackages;

        @Override
        protected void initCounters() {
            if (usedPackages == null) {
                usedPackages = new ArrayList<>();
            }
            usedPackages.clear();
            methodCandidates.forEach(cand -> usedPackages.add(new HashSet<>()));
        }

        @Override
        protected int getCounterForCand(int i) {
            return usedPackages.get(i).size();
        }

        @Override
        public void visitMethod(PsiMethod method) {
            super.visitMethod(method);
            List<PsiPackage> containingPackages = Arrays.asList(
                    ClassUtils.calculatePackagesRecursive(method)
            );

            for (int i = 0; i < methodCandidates.size(); i++) {
                usedPackages.get(i).addAll(containingPackages);
            }
        }

        @Override
        public void visitReferenceElement(PsiJavaCodeReferenceElement reference) {
            super.visitReferenceElement(reference);
            if (!isInsideMethod)
                return;

            PsiElement element = reference.resolve();
            if (element == null || element.getContainingFile() == null) // for packages, dirs etc
                return;

            List<PsiPackage> packages = Arrays.asList(ClassUtils.calculatePackagesRecursive(element));

            for (int i = 0; i < methodCandidates.size(); i++) {
                if (methodCandidates.get(i).getCandidate().isInCandidate()) {
                    usedPackages.get(i).addAll(packages);
                }
            }
        }
    }
}
