package com.genie.controller.echo;

import com.genie.result.Result;
import com.genie.mapper.echo.EchoDataStore;
import com.genie.dto.echo.EchoModels.ResourceItem;
import com.genie.dto.echo.EchoModels.ResourceSubmission;
import com.genie.dto.echo.EchoModels.ResourceSubmissionRequest;
import com.genie.dto.echo.EchoModels.ReviewRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ResourceController {
  private final EchoDataStore store;

  public ResourceController(EchoDataStore store) {
    this.store = store;
  }

  public record ReviewCommentRequest(String reviewComment) {
  }

  @GetMapping("/islands/{islandId}/resources")
  public Result<List<ResourceItem>> listResources(@PathVariable Long islandId) {
    return Result.success(store.listResources(islandId));
  }

  @PostMapping("/resources/submissions")
  public Result<ResourceSubmission> submitResource(
      @RequestHeader(value = "Authorization", required = false) String authorization,
      @Valid @RequestBody ResourceSubmissionRequest request
  ) {
    return Result.success(store.submitResource(request, store.requireUser(authorization)));
  }

  @PostMapping("/resources/submissions/{submissionId}/approve")
  public Result<ResourceSubmission> approveSubmission(
      @PathVariable Long submissionId,
      @RequestHeader(value = "Authorization", required = false) String authorization,
      @RequestBody(required = false) ReviewCommentRequest request
  ) {
    var comment = request == null ? null : request.reviewComment();
    return Result.success(store.reviewResourceSubmission(
        submissionId,
        new ReviewRequest(true, comment),
        store.requireUser(authorization)
    ));
  }

  @PostMapping("/resources/submissions/{submissionId}/reject")
  public Result<ResourceSubmission> rejectSubmission(
      @PathVariable Long submissionId,
      @RequestHeader(value = "Authorization", required = false) String authorization,
      @RequestBody(required = false) ReviewCommentRequest request
  ) {
    var comment = request == null ? null : request.reviewComment();
    return Result.success(store.reviewResourceSubmission(
        submissionId,
        new ReviewRequest(false, comment),
        store.requireUser(authorization)
    ));
  }
}

