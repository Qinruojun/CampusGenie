package com.genie.controller.echo;

import com.genie.result.Result;
import com.genie.mapper.echo.EchoDataStore;
import com.genie.dto.echo.EchoModels.Comment;
import com.genie.dto.echo.EchoModels.CommentRequest;
import com.genie.dto.echo.EchoModels.Post;
import com.genie.dto.echo.EchoModels.PostRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PostController {
  private final EchoDataStore store;

  public PostController(EchoDataStore store) {
    this.store = store;
  }

  @GetMapping("/islands/{islandId}/posts")
  public Result<List<Post>> listPosts(@PathVariable Long islandId) {
    return Result.success(store.listPosts(islandId));
  }

  @PostMapping("/islands/{islandId}/posts")
  public Result<Post> createPost(
      @PathVariable Long islandId,
      @RequestHeader(value = "Authorization", required = false) String authorization,
      @Valid @RequestBody PostRequest request
  ) {
    return Result.success(store.createPost(islandId, request, store.requireUser(authorization)));
  }

  @GetMapping("/posts/{postId}")
  public Result<Post> getPost(@PathVariable Long postId) {
    return Result.success(store.getPost(postId));
  }

  @PutMapping("/posts/{postId}")
  public Result<Post> updatePost(@PathVariable Long postId, @Valid @RequestBody PostRequest request) {
    return Result.success(store.updatePost(postId, request));
  }

  @DeleteMapping("/posts/{postId}")
  public Result<Void> deletePost(@PathVariable Long postId) {
    store.deletePost(postId);
    return Result.success();
  }

  @GetMapping("/posts/{postId}/comments")
  public Result<List<Comment>> listComments(@PathVariable Long postId) {
    return Result.success(store.listComments(postId));
  }

  @PostMapping("/posts/{postId}/comments")
  public Result<Comment> createComment(
      @PathVariable Long postId,
      @RequestHeader(value = "Authorization", required = false) String authorization,
      @Valid @RequestBody CommentRequest request
  ) {
    return Result.success(store.createComment(postId, request, store.requireUser(authorization)));
  }

  @DeleteMapping("/comments/{commentId}")
  public Result<Void> deleteComment(@PathVariable Long commentId) {
    store.deleteComment(commentId);
    return Result.success();
  }
}

