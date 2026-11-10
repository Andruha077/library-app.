
package com.example.persistence.repository;

import com.example.persistence.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    List<CommentEntity> findByBookId(Long bookId);

    List<CommentEntity> findByUserId(Long userId);
}
