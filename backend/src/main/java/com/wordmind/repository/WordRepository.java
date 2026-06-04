package com.wordmind.repository;

import com.wordmind.entity.Word;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {
    
    @Query("SELECT w FROM Word w WHERE " +
           "(:keyword IS NULL OR LOWER(w.word) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(w.meaning) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:pos IS NULL OR w.pos = :pos)")
    Page<Word> searchWords(@Param("keyword") String keyword, 
                           @Param("pos") String pos, 
                           Pageable pageable);
    
    List<Word> findByWordIn(List<String> words);
}
