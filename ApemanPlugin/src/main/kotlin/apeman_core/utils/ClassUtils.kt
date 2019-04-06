package apeman_core.utils

import com.intellij.psi.*
import java.util.ArrayList

object ClassUtils {
    fun findPackage(element: PsiElement?): PsiPackage? {
        val file = element?.containingFile ?: return null
        val directory = file.containingDirectory ?: return null
        return JavaDirectoryService.getInstance().getPackage(directory)
    }
}
