package jp.ac.titech.cs.se.refactorhub.app.interfaces.repository

import jp.ac.titech.cs.se.refactorhub.app.model.Annotation
import jp.ac.titech.cs.se.refactorhub.app.model.Snapshot
import java.util.UUID

interface AnnotationRepository {
    fun findById(id: UUID): Annotation?
    fun findByOwnerId(ownerId: UUID): List<Annotation>
    fun findByCommitId(commitId: UUID): List<Annotation>

    fun create(
        ownerId: UUID,
        commitId: UUID,
        experimentId: UUID,
        isDraft: Boolean,
        hasTemporarySnapshot: Boolean,
        latestInternalCommitSha: String,
        snapshots: List<Snapshot>
    ): Annotation

    fun updateById(
        id: UUID,
        isDraft: Boolean? = null,
        hasTemporarySnapshot: Boolean? = null,
        latestInternalCommitSha: String? = null
    ): Annotation

    fun deleteById(id: UUID)
}
