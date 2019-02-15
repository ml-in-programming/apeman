package apeman_core

import com.intellij.analysis.AnalysisScope
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiMethod

fun scopeToTopMethods(scope: AnalysisScope): List<PsiMethod> {
    val methods = arrayListOf<PsiMethod>()

    // add all methods from analysis scope
    scope.accept(object : JavaRecursiveElementVisitor() {
        private var nestingDepth = 0

        override fun visitMethod(method: PsiMethod) {
            nestingDepth++
            super.visitMethod(method)

            if (nestingDepth == 1)
                methods.add(method)

            nestingDepth--
        }
    })
    return methods
}

fun methodsToScope(methods: List<PsiMethod>): AnalysisScope {
    val files = methods.map { it.containingFile.virtualFile }.distinct()
    val project = methods[0].project
    return AnalysisScope(project, files)
}