package com.genie.controller.echo;

import com.genie.result.Result;
import com.genie.mapper.echo.EchoDataStore;
import com.genie.dto.echo.EchoModels.Reaction;
import com.genie.dto.echo.EchoModels.ReactionRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ReactionController {
  private final EchoDataStore store;

  public ReactionController(EchoDataStore store) {
    this.store = store;
  }

  @PostMapping("/reactions")
  public Result<Reaction> createReaction(
      @RequestHeader(value = "Authorization", required = false) String authorization,
      @Valid @RequestBody ReactionRequest request
  ) {
    return Result.success(store.createReaction(request, store.requireUser(authorization)));
  }

  @DeleteMapping("/reactions/{id}")
  public Result<Void> deleteReaction(
      @PathVariable Long id,
      @RequestHeader(value = "Authorization", required = false) String authorization
  ) {
    store.deleteReaction(id, store.requireUser(authorization));
    return Result.success();
  }

  @GetMapping("/posts/{postId}/reactions")
  public Result<List<Reaction>> listReactions(@PathVariable Long postId) {
    return Result.success(store.listReactionsForPost(postId));
  }
}

