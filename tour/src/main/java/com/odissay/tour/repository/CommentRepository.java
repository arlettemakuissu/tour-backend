package com.odissay.tour.repository;

import com.odissay.tour.model.dto.reponse.CommentResponse;
import com.odissay.tour.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Integer> {
    @Query("SELECT new com.odissay.tour.model.dto.reponse.CommentResponse(" +
            "c.id, " +
            "(c.customer.user.firstname || ' ' || c.customer.user.lastname) AS displayName, " +
            "c.createAt, " +
            "CASE WHEN (c.censored = true) THEN '********' ELSE c.content END, " +
            "c.refererTo.id" +
            ") FROM Comment c " +
            "WHERE c.tour.id = :tourId " +
            "ORDER BY c.createAt")
List<CommentResponse> getCommentsByTour(int tourId);

}
