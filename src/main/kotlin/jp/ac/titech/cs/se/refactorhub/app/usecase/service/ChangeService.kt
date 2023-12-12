package jp.ac.titech.cs.se.refactorhub.app.usecase.service

import com.fasterxml.jackson.databind.ObjectMapper
import jp.ac.titech.cs.se.refactorhub.app.exception.BadRequestException
import jp.ac.titech.cs.se.refactorhub.app.exception.NotFoundException
import jp.ac.titech.cs.se.refactorhub.app.interfaces.repository.ChangeRepository
import jp.ac.titech.cs.se.refactorhub.app.model.Change
import jp.ac.titech.cs.se.refactorhub.core.annotator.*
import jp.ac.titech.cs.se.refactorhub.core.model.ActionName
import jp.ac.titech.cs.se.refactorhub.core.model.ActionType
import jp.ac.titech.cs.se.refactorhub.core.model.DiffCategory
import jp.ac.titech.cs.se.refactorhub.core.model.annotator.FileMapping
import jp.ac.titech.cs.se.refactorhub.core.model.element.CodeElement
import jp.ac.titech.cs.se.refactorhub.core.model.element.CodeElementHolder
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

private const val DEFAULT_CHANGE_TYPE = "Other"

@KoinApiExtension
class ChangeService : KoinComponent {
    private val annotationService: AnnotationService by inject()
    private val snapshotService: SnapshotService by inject()
    private val changeTypeService: ChangeTypeService by inject()
    private val actionService: ActionService by inject()
    private val changeRepository: ChangeRepository by inject()
    private val mapper: ObjectMapper by inject()

    fun get(changeId: UUID): Change {
        val change = changeRepository.findById(changeId)
        return change ?: throw NotFoundException("Change(id=$changeId) is not found")
    }

    fun createEmpty(snapshotId: UUID, orderIndex: Int): Change {
        val changeType = changeTypeService.get(DEFAULT_CHANGE_TYPE)
        return changeRepository.create(
            snapshotId,
            orderIndex,
            "Extract Method", // temporary
            "",
            Change.ParameterData()
        ).applyAndSaveResult { it.changeType(changeType) }
    }

    fun modifyLineNumbers(changeId: UUID, notAppliedFileMappings: List<FileMapping>): Change {
        val change = get(changeId)
        val newParameterData = Change.ParameterData(
            change.parameterData.before,
            change.parameterData.after.modifyLineNumbers(notAppliedFileMappings)
        )
        return changeRepository.updateById(changeId, parameterData = newParameterData)
    }

    fun updateOrderIndex(changeId: UUID, orderIndex: Int): Change {
        return changeRepository.updateById(changeId, orderIndex = orderIndex)
    }

    fun update(changeId: UUID, description: String, typeName: String, userId: UUID): Change {
        var change = get(changeId)
        if (description != change.description) {
            change = changeRepository.updateById(changeId, description = description)
        }
        if (typeName != change.typeName) {
            val changeType = changeTypeService.get(typeName)
            change = change.applyAndSaveResult { it.changeType(changeType) }
        }

        val actionData = mapper.createObjectNode().apply {
            put("changeId", changeId.toString())
            put("description", description)
            put("typeName", typeName)
        }
        actionService.log(ActionName.UpdateChange, ActionType.Server, actionData, userId)
        return change
    }

    fun delete(changeId: UUID) {
        changeRepository.deleteById(changeId)
    }

    fun putNewParameter(
        changeId: UUID,
        diffCategory: DiffCategory,
        parameterName: String,
        elementType: String,
        multiple: Boolean,
        userId: UUID
    ): Change {
        val change = get(changeId).applyAndSaveResult {
            it.putCodeElementHolder(diffCategory, parameterName, elementType, multiple)
        }

        val actionData = mapper.createObjectNode().apply {
            put("changeId", changeId.toString())
            put("diffCategory", diffCategory.name)
            put("parameterName", parameterName)
            put("codeElementTypeName", elementType)
            put("multiple", multiple)
        }
        actionService.log(ActionName.PutNewParameter, ActionType.Server, actionData, userId)

        return change
    }

    fun removeParameter(
        changeId: UUID,
        diffCategory: DiffCategory,
        parameterName: String,
        userId: UUID
    ): Change {
        var change = get(changeId)
        val changeType = changeTypeService.get(change.typeName)
        change = change.applyAndSaveResult { it.removeCodeElementHolder(diffCategory, parameterName, changeType) }

        val actionData = mapper.createObjectNode().apply {
            put("changeId", changeId.toString())
            put("diffCategory", diffCategory.name)
            put("parameterName", parameterName)
        }
        actionService.log(ActionName.RemoveParameter, ActionType.Server, actionData, userId)

        return change
    }

    fun verifyParameter(
        changeId: UUID,
        diffCategory: DiffCategory,
        parameterName: String,
        isVerified: Boolean,
        userId: UUID
    ): Change {
        val change = get(changeId).applyAndSaveResult {
            it.verifyCodeElementHolder(diffCategory, parameterName, isVerified)
        }

        val actionData = mapper.createObjectNode().apply {
            put("changeId", changeId.toString())
            put("diffCategory", diffCategory.name)
            put("parameterName", parameterName)
            put("isVerified", isVerified)
        }
        actionService.log(ActionName.VerifyParameter, ActionType.Server, actionData, userId)

        return change
    }

    fun appendParameterElement(
        changeId: UUID,
        diffCategory: DiffCategory,
        parameterName: String,
        userId: UUID
    ): Change {
        val change = get(changeId).applyAndSaveResult {
            it.appendCodeElementDefaultValue(diffCategory, parameterName)
        }

        val actionData = mapper.createObjectNode().apply {
            put("changeId", changeId.toString())
            put("diffCategory", diffCategory.name)
            put("parameterName", parameterName)
        }
        actionService.log(ActionName.AppendParameterElement, ActionType.Server, actionData, userId)

        return change
    }

    fun updateParameterValue(
        changeId: UUID,
        diffCategory: DiffCategory,
        parameterName: String,
        elementIndex: Int,
        element: CodeElement,
        userId: UUID,
        annotationId: UUID,
        snapshotId: UUID
    ): Change {
        var change = get(changeId)
        val changeType = changeTypeService.get(change.typeName)
        val getFileCodeElementsMap = { category: DiffCategory ->
            annotationService.getFileCodeElementsMap(annotationId, snapshotId, category)
        }
        val isInDiffHunk = { category: DiffCategory, filePath: String, queryLineNumber: Int ->
            snapshotService.isInDiffHunk(snapshotId, category, filePath, queryLineNumber)
        }

        change = get(changeId).applyAndSaveResult {
            it.updateCodeElementValue(diffCategory, parameterName, elementIndex, element, changeType, getFileCodeElementsMap, isInDiffHunk)
        }

        val actionData = mapper.createObjectNode().apply {
            put("changeId", changeId.toString())
            put("diffCategory", diffCategory.name)
            put("parameterName", parameterName)
            put("elementIndex", elementIndex)
            replace("element", mapper.valueToTree(element))
        }
        actionService.log(ActionName.UpdateParameterValue, ActionType.Server, actionData, userId)

        return change
    }

    fun clearParameterValue(
        changeId: UUID,
        diffCategory: DiffCategory,
        parameterName: String,
        elementIndex: Int,
        userId: UUID
    ): Change {
        val change = get(changeId).applyAndSaveResult {
            it.removeCodeElementValue(diffCategory, parameterName, elementIndex)
        }

        val actionData = mapper.createObjectNode().apply {
            put("changeId", changeId.toString())
            put("diffCategory", diffCategory.name)
            put("parameterName", parameterName)
            put("elementIndex", elementIndex)
        }
        actionService.log(ActionName.ClearParameterValue, ActionType.Server, actionData, userId)

        return change
    }

    private fun Change.applyAndSaveResult(function: (Change) -> jp.ac.titech.cs.se.refactorhub.core.model.change.Change): Change {
        val newChangeCore = try {
            function(this)
        } catch (e: Exception) {
            throw BadRequestException(e.message)
        }
        return changeRepository.updateById(
            this.id,
            typeName = if (this.typeName != newChangeCore.typeName) newChangeCore.typeName else null,
            description = if (this.description != newChangeCore.description) newChangeCore.description else null,
            parameterData = Change.ParameterData(newChangeCore.parameterData.before, newChangeCore.parameterData.after)
        )
    }
}

private fun MutableMap<String, CodeElementHolder>.modifyLineNumbers(notAppliedFileMappings: List<FileMapping>): MutableMap<String, CodeElementHolder> {
    return map { (parameterName, codeElementHolder) ->
        parameterName to CodeElementHolder(
            codeElementHolder.type,
            codeElementHolder.multiple,
            codeElementHolder.elements.map elementsMap@{ codeElement ->
                val path = codeElement.location?.path ?: return@elementsMap codeElement
                val range = codeElement.location?.range ?: return@elementsMap codeElement
                val fileMapping = notAppliedFileMappings.find { it.afterPath == path } ?: return@elementsMap codeElement
                val difference = fileMapping.diffHunks
                    .filter {
                        (it.before != null && it.before.oppositeLine < range.startLine)
                                || (it.after != null && it.after.endLine < range.startLine)
                    }.sumOf {
                        ((if (it.before != null) it.before.endLine - it.before.startLine + 1 else 0)
                                - (if (it.after != null) it.after.endLine - it.after.startLine + 1 else 0))
                    }
                range.startLine += difference
                range.endLine += difference
                codeElement
            },
            codeElementHolder.state
        )
    }.toMap().toMutableMap()
}
