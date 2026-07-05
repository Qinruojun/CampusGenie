package com.genie.controller.echo;

import com.genie.result.Result;
import com.genie.mapper.echo.EchoDataStore;
import com.genie.dto.echo.EchoModels.ReviewRequest;
import com.genie.dto.echo.EchoModels.WikiEditRequest;
import com.genie.dto.echo.EchoModels.WikiEditRequestPayload;
import com.genie.dto.echo.EchoModels.WikiPage;
import com.genie.dto.echo.EchoModels.WikiPageRequest;
import com.genie.dto.echo.EchoModels.WikiRevision;
import jakarta.validation.Valid;
import java.util.List;
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
public class WikiController {
  private final EchoDataStore store;

  public WikiController(EchoDataStore store) {
    this.store = store;
  }

  public record RollbackRequest(Long revisionId) {
  }

  public record ReviewCommentRequest(String reviewComment) {
  }

  @GetMapping("/islands/{islandId}/wiki")
  public Result<List<WikiPage>> listWikiPages(@PathVariable Long islandId) {
    return Result.success(store.listWikiPages(islandId));
  }

  @GetMapping("/wiki/pages/{pageId}")
  public Result<WikiPage> getWikiPage(@PathVariable Long pageId) {
    return Result.success(store.getWikiPage(pageId));
  }

  @PostMapping("/wiki/pages")
  public Result<WikiPage> createWikiPage(
      @RequestHeader(value = "Authorization", required = false) String authorization,
      @Valid @RequestBody WikiPageRequest request
  ) {
    return Result.success(store.createWikiPage(request, store.requireUser(authorization)));
  }

  @PutMapping("/wiki/pages/{pageId}")
  public Result<WikiPage> updateWikiPage(
      @PathVariable Long pageId,
      @RequestHeader(value = "Authorization", required = false) String authorization,
      @Valid @RequestBody WikiPageRequest request
  ) {
    return Result.success(store.updateWikiPage(pageId, request, store.requireUser(authorization)));
  }

  @GetMapping("/wiki/pages/{pageId}/revisions")
  public Result<List<WikiRevision>> listWikiRevisions(@PathVariable Long pageId) {
    return Result.success(store.listWikiRevisions(pageId));
  }

  @PostMapping("/wiki/pages/{pageId}/rollback")
  public Result<WikiPage> rollbackWikiPage(
      @PathVariable Long pageId,
      @RequestHeader(value = "Authorization", required = false) String authorization,
      @RequestBody(required = false) RollbackRequest request
  ) {
    var revisionId = request == null ? null : request.revisionId();
    return Result.success(store.rollbackWikiPage(pageId, revisionId, store.requireUser(authorization)));
  }

  @PostMapping("/wiki/pages/{pageId}/edit-requests")
  public Result<WikiEditRequest> createEditRequest(
      @PathVariable Long pageId,
      @RequestHeader(value = "Authorization", required = false) String authorization,
      @RequestBody WikiEditRequestPayload request
  ) {
    return Result.success(store.createWikiEditRequest(pageId, request, store.requireUser(authorization)));
  }

  @PostMapping("/wiki/edit-requests/{requestId}/approve")
  public Result<WikiEditRequest> approveEditRequest(
      @PathVariable Long requestId,
      @RequestHeader(value = "Authorization", required = false) String authorization,
      @RequestBody(required = false) ReviewCommentRequest request
  ) {
    var comment = request == null ? null : request.reviewComment();
    return Result.success(store.reviewWikiEditRequest(
        requestId,
        new ReviewRequest(true, comment),
        store.requireUser(authorization)
    ));
  }

  @PostMapping("/wiki/edit-requests/{requestId}/reject")
  public Result<WikiEditRequest> rejectEditRequest(
      @PathVariable Long requestId,
      @RequestHeader(value = "Authorization", required = false) String authorization,
      @RequestBody(required = false) ReviewCommentRequest request
  ) {
    var comment = request == null ? null : request.reviewComment();
    return Result.success(store.reviewWikiEditRequest(
        requestId,
        new ReviewRequest(false, comment),
        store.requireUser(authorization)
    ));
  }
}

