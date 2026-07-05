package com.genie.controller.echo;

import com.genie.result.Result;
import com.genie.mapper.echo.EchoDataStore;
import com.genie.dto.echo.EchoModels.AuthResponse;
import com.genie.dto.echo.EchoModels.LoginRequest;
import com.genie.dto.echo.EchoModels.RegisterRequest;
import com.genie.dto.echo.EchoModels.User;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final EchoDataStore store;

  public AuthController(EchoDataStore store) {
    this.store = store;
  }

  @PostMapping("/register")
  public Result<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
    return Result.success(store.register(request));
  }

  @PostMapping("/login")
  public Result<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    return Result.success(store.login(request));
  }

  @GetMapping("/me")
  public Result<User> me(@RequestHeader(value = "Authorization", required = false) String authorization) {
    return Result.success(store.me(authorization));
  }

  @PostMapping("/logout")
  public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
    store.logout(authorization);
    return Result.success();
  }
}

