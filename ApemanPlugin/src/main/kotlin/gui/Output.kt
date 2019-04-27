package gui

import apeman_core.base_entities.CandidatesWithFeaturesAndProba
import apeman_core.base_entities.FeatureType
import apeman_core.utils.CandidateUtils
import apeman_core.utils.CandidateValidation
import com.intellij.execution.configurations.AdditionalTabComponentManager
import com.intellij.find.impl.FindPopupPanel.createToolbar
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.editor.actionSystem.EditorActionManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiEditorUtil
import com.intellij.remoteServer.impl.runtime.ui.DefaultServersToolWindowManager.WINDOW_ID
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.Content
import com.intellij.ui.table.JBTable
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.table.AbstractTableModel
import javax.swing.JScrollPane
import javax.swing.ScrollPaneConstants
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener
import javax.swing.event.TableModelEvent
import javax.swing.event.TableModelListener


fun showInfoDialog(candidates: List<CandidatesWithFeaturesAndProba>) {
    val sortedCandidates = candidates.sortedBy { -it.probability }
    var info = StringBuilder()
    for ((cand, features, proba) in sortedCandidates) {
        info.append("\n\n$cand:\n proba = $proba\n\n")
        for ((name, value) in features) {
            info.append("$name = $value\n")
        }
    }

    Messages.showInfoMessage(info.toString(), "checked")
}


class CandidatesTable(
        val candidates: List<CandidatesWithFeaturesAndProba>,
        val project: Project
) {
    val sortedCands = candidates.sortedBy { -it.probability }
    var table: JBTable? = null
    var toolWindow: ToolWindow? = null
    var content: Content? = null

    init {
        createTable()
        createToolWindow()
        createContent()
        connectThem()
    }

    fun createTable() {
        table = JBTable(CandidateModel())
        table!!.isVisible = true
        table!!.autoResizeMode = JBTable.AUTO_RESIZE_OFF
        table!!.selectionModel.addListSelectionListener {
            val record = sortedCands[table!!.selectedRow]
            val range = CandidateUtils.toTextRange(record.candidate)
            val virtualFile = record.candidate.start.containingFile.virtualFile!!
            val fileEditorManager = FileEditorManager.getInstance(project)

            val editors = fileEditorManager.openFile(virtualFile, false, true)
            val editor = (editors[0] as TextEditor).editor
            editor.selectionModel.setSelection(range.startOffset, range.endOffset)
            val position = editor.offsetToLogicalPosition(range.startOffset)
            editor.scrollingModel.scrollTo(position, ScrollType.CENTER_DOWN)
        }
        setColumnWidths()
    }

    fun setColumnWidths() {
        assert(table != null)

        table!!.columnModel.getColumn(0).preferredWidth = (1300 / 2.0).toInt()
        table!!.columnModel.getColumn(1).preferredWidth = (1300 / 4.0).toInt()

        FeatureType.values().forEachIndexed { index, featureType ->
            table!!.columnModel.getColumn(2 + index).preferredWidth =
                    featureType.toString().count() * 12
        }
    }

    fun createToolWindow() {
        val toolWindowManager = ToolWindowManager.getInstance(project)
        val WINDOW_ID = "Candidates"
        toolWindow = toolWindowManager.getToolWindow(WINDOW_ID)
        if (toolWindow == null)
            toolWindow = toolWindowManager.registerToolWindow(WINDOW_ID, true, ToolWindowAnchor.BOTTOM)
    }

    fun createContent() {
        val pane = JBScrollPane(table!!,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        )
        val toolbar = createToolbar()
        val contentPanel = JPanel(BorderLayout())
        contentPanel.add(pane, BorderLayout.CENTER)
        contentPanel.add(toolbar.component, BorderLayout.WEST)
        content = toolWindow!!.contentManager.factory
                .createContent(contentPanel, "Candidates", true)
        content!!.isCloseable = true
    }

    fun connectThem() {
        toolWindow!!.contentManager.removeAllContents(true)
        toolWindow!!.contentManager.addContent(content!!)

        toolWindow!!.setAvailable(true, null)
        toolWindow!!.show(null)
    }

    inner class CandidateModel: AbstractTableModel() {
        override fun getRowCount(): Int {
            return sortedCands.count()
        }

        override fun getColumnCount(): Int {
            return 1 + 1 + FeatureType.values().count() // cand, proba, features
        }

        override fun getValueAt(row: Int, column: Int): Any {
            val record = sortedCands[row]
            return when (column) {
                0 -> record.candidate.toString().take(50)
                1 -> record.probability
                else -> record.features[FeatureType.values()[column - 2]]!!
            }
        }

        override fun addTableModelListener(l: TableModelListener?) {
            super.addTableModelListener(l)
        }

        override fun getColumnName(column: Int): String {
            return when (column) {
                0 -> "candidate"
                1 -> "probability"
                else -> FeatureType.values()[column - 2].toString()
            }
        }
    }
}
