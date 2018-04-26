package command;

import grid.mask.EllipsoidRadius;
import io.log.Log;
import io.parse.CommandArguments;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import command.SetOptionCommand;
import config.Model;

@RunWith(MockitoJUnitRunner.class)
public class SetOptionCommandTest {

  @Mock Model model;
  @Mock Log log;
  
  SetOptionCommand setOptionCommand;
  
  @Before 
  public void initMocks() {
      MockitoAnnotations.initMocks(this);
      setOptionCommand = new SetOptionCommand(model, log);
  }

  @Test
  public void setLocalRadius() {
    HashMap<String, String> map = new HashMap<String, String>();
    map.put("local-radius", "15.0");
    
    setOptionCommand.execute(new CommandArguments(map));

    verify(model).saveLocalRadius(new EllipsoidRadius(new double[] { 15.0, 15.0, 15.0 }));
  }
  
  @Test
  public void setLocalRadiusPerAxes() {
    HashMap<String, String> map = new HashMap<String, String>();
    map.put("local-radius", "15.0,20.0,25.0");
    
    setOptionCommand.execute(new CommandArguments(map));
    
    verify(model).saveLocalRadius(new EllipsoidRadius(new double[] { 15.0, 20.0, 25.0 }));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void setLocalRadiusOnlyTwoRadii() {
    HashMap<String, String> map = new HashMap<String, String>();
    map.put("local-radius", "15.0,20.0");
    
    setOptionCommand.execute(new CommandArguments(map));
  }

}
