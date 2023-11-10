package com.bestreads.bookrecommendations.feature;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/features")
public class FeatureController {

  @Value("${FeatureFlags__Recommendations:false}")
  private boolean recommendationsEnabled;

  @GetMapping("/recommendations/status")
  public boolean isRecommendationsEnabled() {
    return recommendationsEnabled;
  }
}
