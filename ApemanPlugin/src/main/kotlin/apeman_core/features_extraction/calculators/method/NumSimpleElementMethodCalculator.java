package apeman_core.features_extraction.calculators.method;

import apeman_core.base_entities.FeatureType;
import apeman_core.pipes.CandidateWithFeatures;
import apeman_core.utils.CandidateUtils;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.List;

abstract class NumSimpleElementMethodCalculator extends MethodCalculator {

    public NumSimpleElementMethodCalculator(List<CandidateWithFeatures> candidates, FeatureType neededFeature) {
        super(candidates, neededFeature);
    }

    @NotNull
    @Override
    public JavaRecursiveElementVisitor createVisitor() {
        return new Visitor();
    }

    class Visitor extends JavaRecursiveElementVisitor {
        int elementsCounter = 0;
        int nestingDepth = 0;

        @Override
        public void visitMethod(PsiMethod method) {
            if (nestingDepth == 0) {
                elementsCounter = 0;
            }
            nestingDepth++;
            super.visitMethod(method);
            nestingDepth--;
            if (nestingDepth == 0) {
                for (CandidateWithFeatures cand : CandidateUtils.getCandidatesOfMethod(method, getCandidates())) {
                    getResults().set(cand, getFirstFeature(), elementsCounter);
                }
            }
        }
    }
}
