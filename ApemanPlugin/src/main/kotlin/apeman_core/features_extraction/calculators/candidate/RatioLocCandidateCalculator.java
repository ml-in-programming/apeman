package apeman_core.features_extraction.calculators.candidate;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.sixrr.stockmetrics.execution.BaseMetricsCalculator;
import com.sixrr.stockmetrics.utils.BlocksUtils;
import com.sixrr.stockmetrics.utils.CandidateUtils;
import org.jetbrains.research.groups.ml_methods.utils.BlockOfMethod;
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate;

import java.util.ArrayList;

public class RatioLocCandidateCalculator extends BaseMetricsCalculator {

    private ArrayList<ExtractionCandidate> candidates;

    public RatioLocCandidateCalculator(ArrayList<ExtractionCandidate> candidates) {
        this.candidates = new ArrayList<>(candidates);
    }

    void postMetric(ExtractionCandidate candidate, int numerator, int denominator) {
        resultsHolder.postCandidateMetric(metric, candidate, (double) numerator, (double) denominator);
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new JavaRecursiveElementVisitor() {

            @Override
            public void visitMethod(PsiMethod method) {
                super.visitMethod(method);
                if (method.getBody() == null) // abstract method or interface
                    return;
                BlockOfMethod blockOfMethod = BlocksUtils.getBlockFromMethod(method);
                ArrayList<ExtractionCandidate> candidatesOfMethod =
                        CandidateUtils.getCandidatesOfMethod(method, candidates);
                int numStatementsMethod = BlocksUtils.getNumStatementsRecursively(blockOfMethod);

                for (ExtractionCandidate cand: candidatesOfMethod) {
                    postMetric(
                            cand,
                            BlocksUtils.getNumStatementsRecursively(cand.getBlock()),
                            numStatementsMethod
                    );
                }
            }
        };
    }
}
