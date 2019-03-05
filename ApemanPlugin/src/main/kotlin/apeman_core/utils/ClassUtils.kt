package apeman_core.utils

import com.intellij.psi.*
import java.util.ArrayList

object ClassUtils {

    fun calculatePackagesRecursive(element: PsiElement): Array<PsiPackage> {

        var aPackage = findPackage(element)
        val out = ArrayList<PsiPackage>()
        while (aPackage != null) {
            out.add(aPackage)
            aPackage = aPackage.parentPackage
        }
        return out.toTypedArray()
    }

    fun findPackage(element: PsiElement?): PsiPackage? {
        if (element == null) {
            return null
        }
        val file = element.containingFile
        val directory = file.containingDirectory ?: return null
        return JavaDirectoryService.getInstance().getPackage(directory)
    }
}
