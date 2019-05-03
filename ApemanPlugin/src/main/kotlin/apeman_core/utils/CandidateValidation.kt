package apeman_core.utils

import apeman_core.base_entities.ExtractionCandidate
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.util.parentOfType
import com.intellij.refactoring.HelpID
import com.intellij.refactoring.RefactoringBundle
import com.intellij.refactoring.extractMethod.ExtractMethodHandler
import com.intellij.refactoring.extractMethod.ExtractMethodProcessor
import com.intellij.refactoring.extractMethod.PrepareFailedException

object CandidateValidation {

    val REFACTORING_NAME = RefactoringBundle.message("extract.method.title")
    val filesToEditors = hashMapOf<PsiFile, Editor>()

    // get editor, select candidate and check if we can extract it
    fun isValid(candidate: ExtractionCandidate, project: Project): Boolean {

        val editor = getEditor(candidate)

        editor.selectionModel.setSelection(
                candidate.start.textOffset,
                candidate.end.textRange.endOffset
        )

        return try {
            val processor = ExtractMethodProcessor(
                    project,
                    editor,
                    candidate.block.get().toTypedArray(),
                    null,
                    REFACTORING_NAME,
                    "candidate",
                    HelpID.EXTRACT_METHOD
            )
            processor.setShowErrorDialogs(false)
            processor.prepare()

        } catch (e: PrepareFailedException) {
            false
        } catch (e: AssertionError) {
            return e.message?.contains("editor must be showing on the screen")
                    ?: false
        }
//        return ExtractMethodHandler().isAvailableForQuickList(
//                editor,
//                candidate.sourceMethod.containingFile,
//                DataContext.EMPTY_CONTEXT
//        )
    }

    public fun getEditor(candidate: ExtractionCandidate): Editor {

        val file = candidate.sourceMethod.containingFile
        return filesToEditors.getOrPut(file) {
            val project = candidate.sourceMethod.project
            val document = PsiDocumentManager.getInstance(project).getDocument(file)!!
            EditorFactory.getInstance().createEditor(document)!!
        }
    }

    public fun releaseEditors() {
        filesToEditors.forEach { EditorFactory.getInstance().releaseEditor(it.value) }
        filesToEditors.clear()
    }
}
