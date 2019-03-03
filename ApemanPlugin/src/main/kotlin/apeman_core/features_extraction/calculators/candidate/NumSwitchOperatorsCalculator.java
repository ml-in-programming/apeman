package apeman_core.features_extraction.calculators.candidate;

import apeman_core.base_entities.FeatureType;
import apeman_core.pipes.CandidateWithFeatures;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiSwitchStatement;
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate;

import java.util.ArrayList;

public class NumSwitchOperatorsCalculator extends AbstractNumCandidateCalculator {

    public NumSwitchOperatorsCalculator(ArrayList<CandidateWithFeatures> candidates) {
        super(candidates, FeatureType.NUM_SWITCH);
    }

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
        return new NumSwitchOperatorsCalculator.Visitor();
    }

    private class Visitor extends CandidateVisitor {

        @Override
        public void visitSwitchStatement(PsiSwitchStatement statement) {
            super.visitSwitchStatement(statement);

            if (!isInsideMethod)
                return;
            updateCounters();
        }
    }
}
