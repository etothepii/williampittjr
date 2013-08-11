package uk.co.epii.conservatives.williampittjr;


import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * User: James Robinson
 * Date: 15/07/2013
 * Time: 00:02
 */
public class LogoGenerator {

    private static Logger LOG = Logger.getLogger(LogoGenerator.class);

    private static final String fontName;

    static {

        InputStream is = LogoGenerator.class.getResourceAsStream("/lucidaSansUnicode.ttf");
        try {
            fontName = Font.createFont(Font.TRUETYPE_FONT, is).getName();
        }
        catch (FontFormatException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final BufferedImage logo;
    private final Rectangle bounds;
    private static final int CONSERVATIVES = 2;
    private static final int NAME = 0;
    private static final int x = 1;
    private static final int C = 3;
    private static final int o = 4;
    private static final Color CONSERVATIVE_GREEN = new Color(110, 215, 0);
    private static final Color CONSERVATIVE_BLUE = new Color(0, 135, 220);
    private int fontSize;

    public LogoGenerator(BufferedImage logo, Rectangle bounds) {
        this.logo = logo;
        this.bounds = bounds;
    }

    public BufferedImage getLogo(String associationName) {
        if (associationName.length() <= 8 || associationName.length() > 20) {
            throw new UnsupportedOperationException("Name length not supported");
        }
        fontSize = findBaseHeightForMediumLogo(bounds.height);
        Rectangle[] pixelBounds = getPixelBounds(fontSize, associationName);
        int width = logo.getWidth() + pixelBounds[x].width + Math.max(pixelBounds[NAME].width, pixelBounds[CONSERVATIVES].width);
        BufferedImage namedLogo = new BufferedImage(width, logo.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)namedLogo.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, namedLogo.getWidth(), namedLogo.getHeight());
        g.drawImage(logo, 0, 0, null);
        g.setColor(CONSERVATIVE_GREEN);
        g.setFont(new Font(fontName, Font.BOLD, fontSize));
        g.drawString(associationName, logo.getWidth() + pixelBounds[x].width - pixelBounds[NAME].x, bounds.y - pixelBounds[C].y);
        g.setColor(CONSERVATIVE_BLUE);
        g.setFont(new Font(fontName, Font.BOLD, (fontSize * 3) / 2));
        g.drawString("Conservatives", logo.getWidth() + pixelBounds[x].width - pixelBounds[CONSERVATIVES].x, bounds.y + bounds.height - pixelBounds[o].y - pixelBounds[o].height);
        return namedLogo;
    }

    public BufferedImage getPaddedLogo(String associationName) {
        BufferedImage namedLogo = getLogo(associationName);
        Graphics2D g = (Graphics2D)namedLogo.getGraphics();
        g.setFont(new Font(fontName, Font.BOLD, (fontSize * 3) / 2));
        FontRenderContext frc = g.getFontRenderContext();
        int padding = g.getFont().createGlyphVector(frc, "C").getPixelBounds(frc, 0, 0).height;
        BufferedImage paddedLogo = new BufferedImage(
                namedLogo.getWidth() + padding * 2,
                namedLogo.getHeight() + padding * 2,
                BufferedImage.TYPE_INT_ARGB);
        g = (Graphics2D)paddedLogo.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, paddedLogo.getWidth(), paddedLogo.getHeight());
        g.drawImage(namedLogo, padding, padding, null);
        return paddedLogo;


    }

    private int findBaseHeightForMediumLogo(int target) {
        int base = 1;
        double height = getLogoMediumLength(base);
        while (height < target) {
            base *= 2;
            height = getLogoMediumLength(base);
            LOG.debug(String.format("%d --> %f", base, height));
        }
        int delta = base / 4;
        while (delta >= 1) {
            if (height > target) {
                base -= delta;
            }
            else if (height < target) {
                base += delta;
            }
            else {
                break;
            }
            delta /= 2;
            height = getLogoMediumLength(base);
            LOG.debug(String.format("%d --> %f", base, height));
        }
        if (height > target) {
            double alternate = getLogoMediumLength(base - 1);
            if (Math.abs(alternate - target) < Math.abs(height - target)) base--;
        }
        if (height < target) {
            double alternate = getLogoMediumLength(base + 1);
            if (Math.abs(alternate - target) < Math.abs(height - target)) base++;
        }
        LOG.info("Best size: " + base);
        return base;
    }

    private Rectangle[] getPixelBounds(int base, String name) {
        Graphics2D g = (Graphics2D)logo.getGraphics();
        g.setFont(new Font(fontName, Font.BOLD, base));
        Rectangle[] rectangles = new Rectangle[5];
        FontRenderContext frc = g.getFontRenderContext();
        rectangles[0] = g.getFont().createGlyphVector(frc, name).getPixelBounds(frc, 0, 0);
        rectangles[1] = g.getFont().createGlyphVector(frc, "x").getPixelBounds(frc, 0, 0);
        rectangles[3] = g.getFont().createGlyphVector(frc, "C").getPixelBounds(frc, 0, 0);
        g.setFont(new Font(fontName, Font.BOLD, (base * 3) / 2));
        rectangles[2] = g.getFont().createGlyphVector(frc, "Conservatives").getPixelBounds(frc, 0, 0);
        rectangles[4] = g.getFont().createGlyphVector(frc, "o").getPixelBounds(frc, 0, 0);
        return rectangles;
    }

    public double getLogoMediumLength(int size) {
        Graphics2D g = (Graphics2D)logo.getGraphics();
        g.setFont(new Font(fontName, Font.BOLD, size));
        double height = 0d;
        FontRenderContext frc = g.getFontRenderContext();
        height += g.getFont().createGlyphVector(frc, "C").getPixelBounds(frc, 0, 0).getHeight();
        LOG.debug(String.format("C %d --> %f", size, height));
        height += g.getFont().createGlyphVector(frc, "x").getPixelBounds(frc, 0, 0).getHeight();
        LOG.debug(String.format("Cx %d --> %f", size, height));
        g.setFont(new Font(fontName, Font.BOLD, (size * 3) / 2));
        height += g.getFont().createGlyphVector(frc, "o").getPixelBounds(frc, 0, 0).getHeight();
        LOG.debug(String.format("Cxo %d --> %f", size, height));
        return height;
    }



}
