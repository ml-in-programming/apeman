package apeman_core.features_extraction.calculators.candidate;

import apeman_core.utils.BlocksUtils;
import apeman_core.utils.CandidateUtils;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.sixrr.stockmetrics.execution.BaseMetricsCalculator;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.groups.ml_methods.utils.BlockOfMethod;
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AbstractCouplingCohesionCandidateCalculator<T> extends BaseMetricsCalculator {

    private ArrayList<ExtractionCandidate> candidates;
    private Class<T> aClass;
    private double coupling = 0.0;
    private double cohesion = 0.0;
    private boolean isCouplingMethod;
    private boolean isFirstPlace;

    public AbstractCouplingCohesionCandidateCalculator(
            ArrayList<ExtractionCandidate> candidates,
            Class<T> aClass,
            boolean isCouplingMethod,
            boolean isFirstPlace) {

        this.candidates = new ArrayList<>(candidates);
        this.aClass = aClass;
        this.isCouplingMethod = isCouplingMethod;
        this.isFirstPlace = isFirstPlace;
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new CandidateVisitor();
    }

    void postMetric(ExtractionCandidate candidate, int numerator, int denominator) {
        resultsHolder.postCandidateMetric(metric, candidate, (double) numerator, (double) denominator);
    }

    void postMetric(ExtractionCandidate candidate, int value) {
        resultsHolder.postCandidateMetric(metric, candidate, (double) value);
    }

    void postMetric(ExtractionCandidate candidate, double value) {
        resultsHolder.postCandidateMetric(metric, candidate, value);
    }

    public class CandidateVisitor extends JavaRecursiveElementVisitor {
        private ArrayList<ExtractionCandidate> methodCandidates;

        @Override
        public void visitMethod(PsiMethod method) {
            super.visitMethod(method);
            methodCandidates = CandidateUtils.getCandidatesOfMethod(method, candidates);
            for (ExtractionCandidate candidate : methodCandidates) {
                calculateCouplingCohesion(candidate);
                postMetric(candidate, getResult());
            }
        }
    }

    private void calculateCouplingCohesion(ExtractionCandidate candidate) {
        coupling = 0.0;
        cohesion = 0.0;

        PsiMethod sourceMethod = candidate.getSourceMethod();
        BlockOfMethod sourceBlock = BlocksUtils.getBlockFromMethod(sourceMethod);
        BlockOfMethod candidateBlock = candidate.getBlock();

        Set<T> elements = getElementsFromBlock(candidateBlock);
        if (elements.isEmpty() || (elements.size() == 1 && !isFirstPlace)) {
            return;
        }

        HashMap<T, Double> ratio = new HashMap<>();

        for (T e : elements) {
            double freqCandidate = getFreqOfElementFromBlock(candidateBlock, e);
            double freqMethod = getFreqOfElementFromBlock(sourceBlock, e);
            ratio.put(e, freqCandidate / freqMethod);
        }

        T bestElem = getElementFromRatio(ratio);
        coupling = ratio.get(bestElem);

        int loc = BlocksUtils.getNumStatementsRecursively(candidateBlock);
        int count = getCountOfElementFromBlock(candidateBlock, bestElem);
        cohesion = (double) count / loc;
    }

    protected Set<T> getElementsFromBlock(BlockOfMethod block) {
        return BlocksUtils.getElementsOfBlock(block, aClass);
    }

    protected int getCountOfElementFromBlock(BlockOfMethod block, T elem) {
        return BlocksUtils.getCountOfElementFromBlock(block, elem);
    }

    protected double getFreqOfElementFromBlock(BlockOfMethod block, T elem) {
        return BlocksUtils.getFreqOfElementFromBlock(block, elem);
    }

    private T getElementFromRatio(HashMap<T, Double> ratio) {
        T elem = getMaxRatio(ratio);
        if (isFirstPlace)
            return elem;

        ratio.remove(elem);
        return getMaxRatio(ratio);
    }

    private T getMaxRatio(@NotNull HashMap<T, Double> ratio) {
        T maxElem = null;
        Double maxDouble = -1.0;

        for (Map.Entry<T, Double> entry : ratio.entrySet()) {
            if (entry.getValue() > maxDouble) {
                maxElem = entry.getKey();
                maxDouble = entry.getValue();
            }
        }
        return maxElem;
    }

    protected double getResult() {
        return isCouplingMethod ? getCoupling() : getCohesion();
    }

    @Contract(pure = true)
    private double getCoupling() {
        return coupling;
    }

    @Contract(pure = true)
    private double getCohesion() {
        return cohesion;
    }
}
