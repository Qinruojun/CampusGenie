package com.genie.controller.echo;

import com.genie.result.Result;
import com.genie.mapper.echo.EchoDataStore;
import com.genie.dto.echo.EchoModels.Island;
import com.genie.dto.echo.EchoModels.IslandMember;
import com.genie.dto.echo.EchoModels.IslandRequest;
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
@RequestMapping("/api/islands")
public class IslandController {
  private final EchoDataStore store;

  public IslandController(EchoDataStore store) {
    this.store = store;
  }

  @GetMapping
  public Result<List<Island>> listIslands() {
    return Result.success(store.listIslands());
  }

  @GetMapping("/{slug}")
  public Result<Island> getIsland(@PathVariable String slug) {
    return Result.success(store.getIslandBySlug(slug));
  }

  @PostMapping
  public Result<Island> createIsland(
      @RequestHeader(value = "Authorization", required = false) String authorization,
      @Valid @RequestBody IslandRequest request
  ) {
    return Result.success(store.createIsland(request, store.requireUser(authorization)));
  }

  @PutMapping("/{id}")
  public Result<Island> updateIsland(@PathVariable Long id, @Valid @RequestBody IslandRequest request) {
    return Result.success(store.updateIsland(id, request));
  }

  @PostMapping("/{id}/join")
  public Result<IslandMember> joinIsland(
      @PathVariable Long id,
      @RequestHeader(value = "Authorization", required = false) String authorization
  ) {
    return Result.success(store.joinIsland(id, store.requireUser(authorization)));
  }

  @PostMapping("/{id}/leave")
  public Result<Void> leaveIsland(
      @PathVariable Long id,
      @RequestHeader(value = "Authorization", required = false) String authorization
  ) {
    store.leaveIsland(id, store.requireUser(authorization));
    return Result.success();
  }

  @GetMapping("/{id}/members")
  public Result<List<IslandMember>> listMembers(@PathVariable Long id) {
    return Result.success(store.listMembers(id));
  }
}

