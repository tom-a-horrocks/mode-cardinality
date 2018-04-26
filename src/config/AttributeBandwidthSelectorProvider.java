package config;

import kde.bw.BandwidthSelector;
import kde.bw.MultipliedBandwidthSelector;
import kde.bw.SilvermanBandwidthSelector;
import kde.bw.StaticBandwidthSelector;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class AttributeBandwidthSelectorProvider implements Provider<BandwidthSelector> {
  
  private final Model model;

  @Inject
  public AttributeBandwidthSelectorProvider(Model model) {
    this.model = model;
  }

  @Override
  public BandwidthSelector get() {
    String attributeBandwidthSelectorString = model.getAttributeBandwidthSelectorString();
    BandwidthSelector newBandwidthSelector;
    try {
      double bandwidth = Double.parseDouble(attributeBandwidthSelectorString);
      newBandwidthSelector = new StaticBandwidthSelector(bandwidth);
    } catch (NumberFormatException e) {
      switch (attributeBandwidthSelectorString) {
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
