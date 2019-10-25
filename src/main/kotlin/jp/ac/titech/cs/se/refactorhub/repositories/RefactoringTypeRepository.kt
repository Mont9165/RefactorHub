package jp.ac.titech.cs.se.refactorhub.repositories

import jp.ac.titech.cs.se.refactorhub.models.RefactoringType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RefactoringTypeRepository : JpaRepository<RefactoringType, Long> {
    fun findByName(name: String): Optional<RefactoringType>
}
