// (C) 2020 uchicom
package com.uchicom.remon.type;

import com.uchicom.remon.strategy.Analysis;
import com.uchicom.remon.strategy.PngAnalysis;
import java.util.Arrays;

public enum AnalysisStrategy {
  PNG(1, new PngAnalysis());

  private final int strategy;
  private final Analysis analysis;

  private AnalysisStrategy(int strategy, Analysis analysis) {
    this.strategy = strategy;
    this.analysis = analysis;
  }

  public static Analysis getAnalysis(int strategy) {
    return Arrays.stream(values())
        .filter(value -> value.strategy == strategy)
        .findFirst()
        .orElseThrow()
        .analysis;
  }

  public int getStrategy() {
    return strategy;
  }

  public Analysis getAnalysis() {
    return analysis;
  }
}
