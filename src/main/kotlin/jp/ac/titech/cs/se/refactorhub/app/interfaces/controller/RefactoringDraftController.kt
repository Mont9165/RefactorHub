package jp.ac.titech.cs.se.refactorhub.app.interfaces.controller

import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import jp.ac.titech.cs.se.refactorhub.app.model.Refactoring
import jp.ac.titech.cs.se.refactorhub.app.model.RefactoringDraft
import jp.ac.titech.cs.se.refactorhub.app.usecase.service.RefactoringDraftService
import jp.ac.titech.cs.se.refactorhub.tool.model.element.CodeElement
import org.koin.core.KoinComponent
import org.koin.core.inject

@KtorExperimentalLocationsAPI
@Location("/drafts")
class RefactoringDraftController : KoinComponent {
    private val refactoringDraftService: RefactoringDraftService by inject()

    data class UpdateDraftBody(
        val description: String? = null,
        val type: String? = null
    )

    data class PutDraftElementKeyBody(
        val key: String,
        val type: String
    )

    data class UpdateDraftElementValueBody(
        val element: CodeElement
    )

    fun get(id: Int): RefactoringDraft {
        return refactoringDraftService.get(id)
    }

    fun save(id: Int, userId: Int?): Refactoring {
        return refactoringDraftService.save(id, userId)
    }

    fun discard(id: Int, userId: Int?) {
        return refactoringDraftService.discard(id, userId)
    }

    fun update(id: Int, body: UpdateDraftBody, userId: Int?): RefactoringDraft {
        return refactoringDraftService.update(id, body.description, body.type, userId)
    }

    fun putElementKey(id: Int, category: String, body: PutDraftElementKeyBody, userId: Int?): RefactoringDraft {
        return refactoringDraftService.putElementKey(id, category, body.key, body.type, userId)
    }

    fun removeElementKey(id: Int, category: String, key: String, userId: Int?): RefactoringDraft {
        return refactoringDraftService.removeElementKey(id, category, key, userId)
    }

    fun appendElementValue(id: Int, category: String, key: String, userId: Int?): RefactoringDraft {
        return refactoringDraftService.appendElementValue(id, category, key, userId)
    }

    fun updateElementValue(
        id: Int,
        category: String,
        key: String,
        index: Int,
        body: UpdateDraftElementValueBody,
        userId: Int?
    ): RefactoringDraft {
        return refactoringDraftService.updateElementValue(id, category, key, index, body.element, userId)
    }

    fun removeElementValue(id: Int, category: String, key: String, index: Int, userId: Int?): RefactoringDraft {
        return refactoringDraftService.removeElementValue(id, category, key, index, userId)
    }
}
