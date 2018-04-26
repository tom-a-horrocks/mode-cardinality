package config;

import kde.bw.BandwidthSelector;
import kde.bw.MultipliedBandwidthSelector;
import kde.bw.SilvermanBandwidthSelector;
import kde.bw.StaticBandwidthSelector;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class ProjectionBandwidthSelectorProvider implements Provider<BandwidthSelector> {
  
  private final Model model;

  @Inject
  public ProjectionBandwidthSelectorProvider(Model model) {
    this.model = model;
  }

  @Override
  public BandwidthSelector get() {
    String projectionBandwidthSelectorString = model.getProjectionBandwidthSelectorString();
    BandwidthSelector newBandwidthSelector;
    try {
      double bandwidth = Double.parseDouble(projectionBandwidthSelectorString);
      newBandwidthSelector = new StaticBandwidthSelector(bandwidth);
    } catch (NumberFormatException e) {
      switch (projectionBandwidthSelectorString) {
        case "silverman":
          newBandwidthSelector = new SilvermanBandwidthSelector();
          break;
        default:
          newBandwidthSelector = null;
      }
    }
    
    // SetOptions should have detected bad bandwidthSelectorString
    assert newBandwidthSelector != null;
    
    return new MultipliedBandwidthSelector(newBandwidthSelector, model.getBandwidthMultiplier());
  }

}
