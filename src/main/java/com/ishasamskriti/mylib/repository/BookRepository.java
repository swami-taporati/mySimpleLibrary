package com.ishasamskriti.mylib.repository;

import com.ishasamskriti.mylib.domain.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the Book entity.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    @Query(
        value = "select distinct book from Book book left join fetch book.authors",
        countQuery = "select count(distinct book) from Book book"
    )
    Page<Book> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct book from Book book left join fetch book.authors")
    List<Book> findAllWithEagerRelationships();

    @Query("select book from Book book left join fetch book.authors where book.id =:id")
    Optional<Book> findOneWithEagerRelationships(@Param("id") Long id);

    // added by Swami
    @Query("select book from Book book where book.status =:status")
    List<Book> findAvailableBooks(@Param("status") Long id);
}
