package config;

import grid.export.FullLineMapper;
import grid.export.LineMapper;
import grid.export.MinimalLineMapper;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class LineMapperProvider implements Provider<LineMapper> {
  
  private final Model model;

  @Inject
  public LineMapperProvider(Model model) {
    this.model = model;
  }

  @Override
  public LineMapper get() {
    if (model.getMinimalExport()) {
      return new MinimalLineMapper();
    }
    return new FullLineMapper();
  }

}
