package dataset_generation

import com.intellij.facet.impl.ProjectFacetManagerEx
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileTypes.StdFileTypes
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ex.ProjectManagerEx
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor
import com.intellij.psi.*
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.refactoring.inline.InlineMethodProcessor
import java.nio.file.Paths
import java.util.logging.Logger

class InlineMethodsProcessor(
        pathToProjects: List<String>
) {

    private val log = Logger.getGlobal()!!
    var project: Project? = null
    var pathToProject: String? = null

    init {
        pathToProjects.forEach { analyze(it) }
    }

    private fun analyze(pathToProject: String) {
        try {
            this.pathToProject = pathToProject

            log.info(pathToProject)
            log.info("load project")
            loadProject()

            log.info("inner methods")
            innerMethods()

            ProjectManager.getInstance().closeProject(project!!)

        } catch (e: Exception) {
            print(e)
        } catch (e: Error) {
            print(e)
        }
    }

    private fun loadProject() {
        assert(Paths.get(pathToProject).toFile().isDirectory)
        project = ProjectManager.getInstance().loadAndOpenProject(pathToProject!!)!!
    }

    private fun innerMethods() {

        val overallLength = getJavaFilesFromProject().count()
        ProjectManager.getInstance().closeProject(project!!)

        var offset = 20000
        val limit = 1000

        while (offset < overallLength) {

            log.info("start with offset: $offset")
            project = ProjectManager.getInstance().loadAndOpenProject(pathToProject!!)!!
            val files = getJavaFilesFromProject()
            CommandProcessor.getInstance().executeCommand(project, {

                files.withIndex()
                        .filter { offset <= it.index && it.index < (offset + limit) }
                        .forEach { (index, file) ->
                            if (index % 50 == 0)
                                log.info(index.toString())

                            file.accept(object : JavaRecursiveElementVisitor() {
                                override fun visitMethod(method: PsiMethod?) {
                                    super.visitMethod(method)

                                    val reference = getFirstReferenceOrNull(method!!) ?: return
                                    if (method.isConstructor || method.body == null || !method.isWritable)
                                        return
                                    if (!method.isValid)
                                        return
                                    if (method.containingClass?.isInterface ?: true)
                                        return
                                    if (method.name.startsWith("get") && (method.body?.statementCount == 1))
                                        return

                                    addBracketsToMethod(method)

                                    val editor = getEditor(reference)
                                    try {
                                        log.info("inline...")
                                        InlineMethodProcessor(
                                                reference.element.project, method, reference, editor, false
                                        ).run()
                                    } catch (e: Exception) {
                                        log.info(e.toString())
                                    }
                                    EditorFactory.getInstance().releaseEditor(editor)
                                    PsiDocumentManager.getInstance(project!!).commitAllDocuments()
                                }
                            })
                        }
            }, null, null)
            ProjectManager.getInstance().closeProject(project!!)
            offset += limit
        }
    }

    private fun getJavaFilesFromProject(): List<PsiFile> {
        val files = arrayListOf<VirtualFile>()

        VfsUtilCore.visitChildrenRecursively(project!!.baseDir,
                object : VirtualFileVisitor<ArrayList<VirtualFile>>() {
                    override fun visitFile(file: VirtualFile): Boolean {
                        super.visitFile(file)
                        if (file.fileType == StdFileTypes.JAVA)
                            files.add(file)
                        return true
                    }
                }
        )
        val manager = PsiManager.getInstance(project!!)
        return files.mapNotNull { manager.findFile(it) }
    }

    private fun getFirstReferenceOrNull(method: PsiMethod): PsiJavaCodeReferenceElement? {
        val query = ReferencesSearch.search(method)
        var totalRefs = 0
        var reference: PsiReference? = null
        query.forEach {
            if (totalRefs == 0) totalRefs++ else return null
            reference = it
        }
        return reference as? PsiJavaCodeReferenceElement
    }

    private fun getEditor(reference: PsiReference): Editor {
        val file = reference.element.containingFile
        val project = reference.element.project
        val document = PsiDocumentManager.getInstance(project).getDocument(file)!!
        return EditorFactory.getInstance().createEditor(document)!!
    }

    private fun addBracketsToMethod(sourceMethod: PsiMethod) {
        val factory = JavaPsiFacade.getInstance(project).getElementFactory()
        val startCandidateComment = factory.createCommentFromText("/*{*/", sourceMethod)
        val endCandidateComment = factory.createCommentFromText("/*}*/", sourceMethod)

        sourceMethod.body!!.addBefore(startCandidateComment, sourceMethod.body!!.firstBodyElement)
        sourceMethod.body!!.addAfter(endCandidateComment, sourceMethod.body!!.lastBodyElement)
    }

}