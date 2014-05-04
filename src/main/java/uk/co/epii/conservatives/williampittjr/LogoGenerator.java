package uk.co.epii.conservatives.williampittjr;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: James Robinson
 * Date: 22/12/2013
 * Time: 15:20
 */
public interface LogoGenerator {

  public BufferedImage getLogo(String logoText);
  public Color getPrimaryColor();
  public Color getSecondaryColor();

}
