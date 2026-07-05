package com.genie.controller.echo;

import com.genie.result.Result;
import com.genie.mapper.echo.EchoDataStore;
import com.genie.dto.echo.EchoModels.Roadmark;
import com.genie.dto.echo.EchoModels.RoadmarkRequest;
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
public class RoadmarkController {
  private final EchoDataStore store;

  public RoadmarkController(EchoDataStore store) {
    this.store = store;
  }

  @PostMapping("/roadmarks")
  public Result<Roadmark> createRoadmark(
      @RequestHeader(value = "Authorization", required = false) String authorization,
      @Valid @RequestBody RoadmarkRequest request
  ) {
    return Result.success(store.createRoadmark(request, store.requireUser(authorization)));
  }

  @GetMapping("/me/roadmarks")
  public Result<List<Roadmark>> listMyRoadmarks(
      @RequestHeader(value = "Authorization", required = false) String authorization
  ) {
    return Result.success(store.listMyRoadmarks(store.requireUser(authorization)));
  }

  @DeleteMapping("/roadmarks/{id}")
  public Result<Void> deleteRoadmark(
      @PathVariable Long id,
      @RequestHeader(value = "Authorization", required = false) String authorization
  ) {
    store.deleteRoadmark(id, store.requireUser(authorization));
    return Result.success();
  }
}

