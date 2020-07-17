package jp.ac.titech.cs.se.refactorhub.app.interfaces.controller

import jp.ac.titech.cs.se.refactorhub.app.usecase.service.EditorService
import jp.ac.titech.cs.se.refactorhub.tool.model.editor.FileContent
import org.koin.core.KoinComponent
import org.koin.core.inject

class EditorController : KoinComponent {
    private val editorService: EditorService by inject()

    fun getFileContent(
        sha: String,
        owner: String,
        repository: String,
        path: String,
        token: String? = null
    ): FileContent {
        return editorService.getFileContent(sha, owner, repository, path, token)
    }
}
