package uk.co.epii.conservatives.williampittjr;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * User: James Robinson
 * Date: 15/07/2013
 * Time: 00:01
 */
public class Main {

    public static void main(String[] args) throws IOException {
        BufferedImage bufferedImage =
                ImageIO.read(Main.class.getResourceAsStream("/conservative_party_logo.png"));
        ConservativeLogoGeneratorImpl conservativeLogoGeneratorImpl = new ConservativeLogoGeneratorImpl(bufferedImage, new Rectangle(0, 29, bufferedImage.getWidth(), 162));
        BufferedImage namedLogo = conservativeLogoGeneratorImpl.getLogo("Beaconsfield");
        ImageIO.write(namedLogo, "png", new File(System.getProperty("user.home") + "/frederickNorth/beaconsfieldLogo.png"));
        BufferedImage paddedLogo = conservativeLogoGeneratorImpl.getPaddedLogo("Poplar and Limehouse");
        ImageIO.write(paddedLogo, "png", new File(System.getProperty("user.home") + "/frederickNorth/beaconsfieldPaddedLogo.png"));
    }

}
