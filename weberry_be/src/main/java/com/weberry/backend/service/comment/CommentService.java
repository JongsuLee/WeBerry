package com.weberry.backend.service.comment;

import org.springframework.stereotype.Service;

import com.weberry.backend.entity.Comment;
import com.weberry.backend.entity.Post;

@Service
public interface CommentService {

	Comment.ToShow commentOnPost(Comment.Request request);
	
	Comment.ToShow recommentOnComment(Comment.RecommentRequest request);
	
}
