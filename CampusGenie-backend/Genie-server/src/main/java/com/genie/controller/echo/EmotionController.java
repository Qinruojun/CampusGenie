package com.genie.controller.echo;

import com.genie.result.Result;
import com.genie.mapper.echo.EchoDataStore;
import com.genie.dto.echo.EchoModels.EmotionCheckin;
import com.genie.dto.echo.EchoModels.EmotionCheckinRequest;
import com.genie.dto.echo.EchoModels.EmotionWeather;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EmotionController {
  private final EchoDataStore store;

  public EmotionController(EchoDataStore store) {
    this.store = store;
  }

  @PostMapping("/emotions/checkin")
  public Result<EmotionCheckin> checkinEmotion(
      @RequestHeader(value = "Authorization", required = false) String authorization,
      @Valid @RequestBody EmotionCheckinRequest request
  ) {
    return Result.success(store.checkinEmotion(request, store.requireUser(authorization)));
  }

  @GetMapping("/islands/{islandId}/emotion-weather")
  public Result<EmotionWeather> emotionWeather(@PathVariable Long islandId) {
    return Result.success(store.emotionWeather(islandId));
  }
}

