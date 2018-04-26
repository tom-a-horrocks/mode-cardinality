package integrated;

import io.CommandLineInterface;

import org.junit.Ignore;
import org.junit.Test;

import com.google.inject.Guice;

import config.ClusteringModule;

@SuppressWarnings("unused")
public class SystemTest {

  @Test
  @Ignore
  public void standardClustering() {
    CommandLineInterface cli = Guice
        .createInjector(new ClusteringModule())
        .getInstance(CommandLineInterface.class);
    
    String inputFile = "C:\\Users\\20365994\\Desktop\\Experiments\\Synthetic\\models\\Dyke_synth.csv";
    String similarityPath = "D:\\PhD Data\\processed\\similarity.csv";
    String clusterPath = "D:\\PhD Data\\processed\\cluster.csv";
    String bandwidthPath = "D:\\PhD Data\\processed\\bandwidth.csv";
    
//    cli.parseArgumentsAndExecuteCommand("load --load-path \"" + inputFile + "\"");
//    cli.parseArgumentsAndExecuteCommand("set-option --minimal-export \"true\" --bandwidth-selector \"silverman\" --local-radius \"100.0\"");
//    cli.parseArgumentsAndExecuteCommand("bandwidth-volume --attribute-export-path \"" + bandwidthPath + "\" --projection-export-path \"" + bandwidthPath + "\"");
//    cli.parseArgumentsAndExecuteCommand("similarity-volume --export-path \"" + similarityPath + "\"");
    
   
    inputFile = "C:\\Users\\20365994\\Desktop\\Experiments\\Kevitsa\\models\\dens_trimmed.csv";
    cli.parseArgumentsAndExecuteCommand("load --load-path \"" + inputFile + "\"");
    cli.parseArgumentsAndExecuteCommand("set-option --minimal-export \"true\"");
    cli.parseArgumentsAndExecuteCommand("set-option --local-radius \"86.17\"");
    cli.parseArgumentsAndExecuteCommand("set-option --rotation \"0\"");
    
    cli.parseArgumentsAndExecuteCommand("set-option --attribute-bandwidth-selector \"0.0075\" --projection-bandwidth-selector \"0.4\"");
    cli.parseArgumentsAndExecuteCommand("mode-volume --number-of-subdivisions 20 --use-gradient true --gradient-cutoff 0.005 --export-path \"out/modes/dens_trimmed_r=86.17_abw=0.0075_pbw=0.4.csv\"");
    
  }

}
