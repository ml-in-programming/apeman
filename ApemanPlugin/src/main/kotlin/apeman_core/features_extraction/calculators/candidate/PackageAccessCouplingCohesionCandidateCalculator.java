package apeman_core.features_extraction.calculators.candidate;

import apeman_core.base_entities.BlockOfMethod;
import apeman_core.base_entities.FeatureType;
import apeman_core.pipes.CandidateWithFeatures;
import apeman_core.utils.BlocksUtils;
import apeman_core.utils.ClassUtils;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PackageAccessCouplingCohesionCandidateCalculator extends AbstractCouplingCohesionCandidateCalculator<PsiPackage> {

    public PackageAccessCouplingCohesionCandidateCalculator(
            ArrayList<CandidateWithFeatures> candidates,
            FeatureType neededFeature,
            boolean isCouplingMethod,
            boolean isFirstPlace) {

        super(candidates, neededFeature, PsiPackage.class, isCouplingMethod, isFirstPlace);
    }

    @Override
    public CandidateVisitor createVisitor() {
        return new PackageAccessCouplingCohesionCandidateCalculator.Visitor();
    }

    public class Visitor extends CandidateVisitor {}

    @Override
    protected Set<PsiPackage> getElementsFromBlock(BlockOfMethod block) {
        assert block.getStatementsCount() > 0;

        Set<PsiPackage> result = new HashSet<>(Arrays.asList(
                ClassUtils.calculatePackagesRecursive(block.getFirstStatement())
        ));

        for (int i = 0; i < block.getStatementsCount(); i++) {
            block.get(i).accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitReferenceElement(PsiJavaCodeReferenceElement reference) {
                    super.visitReferenceElement(reference);
                    PsiElement elem = reference.resolve();
                    if (elem == null || elem.getContainingFile() == null)
                        return;

                    PsiPackage[] packages = ClassUtils.calculatePackagesRecursive(elem);
                    result.addAll(Arrays.asList(packages));
                }

                @Override
                public void visitPackage(PsiPackage aPackage) {
                    super.visitPackage(aPackage);
                    result.add(aPackage);
                }
            });
        }
        return result;
    }

    private static int ourCount = 0;

    @Override
    protected int getCountOfElementFromBlock(@NotNull BlockOfMethod block, PsiPackage psiPackage) {
        ourCount = 0;

        for (int i = 0; i < block.getStatementsCount(); i++) {
            block.get(i).accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitReferenceElement(PsiJavaCodeReferenceElement reference) {
                    super.visitReferenceElement(reference);
                    PsiElement resolved = reference.resolve();
                    if (resolved == null || resolved.getContainingFile() == null)
                        return;

                    PsiPackage[] packages = ClassUtils.calculatePackagesRecursive(reference.resolve());
                    for (PsiPackage pack: packages) {
                        if (pack == psiPackage)
                            ourCount++;
                    }
                }

                @Override
                public void visitPackage(PsiPackage aPackage) {
                    super.visitPackage(aPackage);
                    if (aPackage == psiPackage)
                        ourCount++;
                }
            });
        }
        return ourCount;
    }

    protected double getFreqOfElementFromBlock(BlockOfMethod block, PsiPackage elem) {
        int count = getCountOfElementFromBlock(block, elem);
        return (double)count / BlocksUtils.getNumStatementsRecursively(block);
    }
}
