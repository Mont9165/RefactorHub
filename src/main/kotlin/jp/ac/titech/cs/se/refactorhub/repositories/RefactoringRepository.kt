package jp.ac.titech.cs.se.refactorhub.repositories

import jp.ac.titech.cs.se.refactorhub.models.Refactoring
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RefactoringRepository : JpaRepository<Refactoring, Long> {
    @Query("SELECT r FROM Refactoring r JOIN FETCH r.children WHERE r.id = (:id)")
    fun findByIdAndFetchChildrenEagerly(@Param("id") id: Long): Optional<Refactoring>

    @Query("SELECT r FROM Refactoring r JOIN FETCH r.drafts WHERE r.id = (:id)")
    fun findByIdAndFetchDraftsEagerly(@Param("id") id: Long): Optional<Refactoring>
}
