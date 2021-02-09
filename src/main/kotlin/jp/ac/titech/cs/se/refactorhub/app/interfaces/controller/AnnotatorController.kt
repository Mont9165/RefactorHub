package jp.ac.titech.cs.se.refactorhub.app.interfaces.controller

import jp.ac.titech.cs.se.refactorhub.app.usecase.service.EditorService
import jp.ac.titech.cs.se.refactorhub.core.model.DiffCategory
import jp.ac.titech.cs.se.refactorhub.core.model.annotator.FileContent
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinApiExtension
class AnnotatorController : KoinComponent {
    private val editorService: EditorService by inject()

    fun getFileContent(
        sha: String,
        owner: String,
        repository: String,
        category: DiffCategory,
        path: String
    ): FileContent {
        return editorService.getFileContent(sha, owner, repository, category, path)
    }
}
