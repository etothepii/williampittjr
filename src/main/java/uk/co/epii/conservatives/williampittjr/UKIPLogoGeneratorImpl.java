package uk.co.epii.conservatives.williampittjr;


import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
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
public class UKIPLogoGeneratorImpl implements LogoGenerator {

    private static Logger LOG = Logger.getLogger(UKIPLogoGeneratorImpl.class);

    private static final String fontName;

    static {

        InputStream is = UKIPLogoGeneratorImpl.class.getResourceAsStream("/lucidaSansUnicode.ttf");
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
    private static final int UKIP = 2;
    private static final int NAME = 0;
    private static final int x = 1;
    private static final int C = 3;
    private static final int o = 4;
    private static final int Cy = 5;
    private static final Color UKIP_PURPLE = new Color(112, 30, 122);
    private static final Color UKIP_YELLOW = new Color(254, 223, 0);
    private int fontSize;

    public UKIPLogoGeneratorImpl() throws IOException {
        this(ImageIO.read(Main.class.getResourceAsStream("/ukip_party_logo.png")));
    }

    public UKIPLogoGeneratorImpl(BufferedImage logo) {
        this(logo, new Rectangle(0, 0, logo.getWidth(), 162));
    }

    public UKIPLogoGeneratorImpl(BufferedImage logo, Rectangle bounds) {
        this.logo = logo;
        this.bounds = bounds;
    }

    public BufferedImage getLogo(String associationName) {
        fontSize = findBaseHeightForLogo(bounds.height, associationName.length());
        String[] associationNameLines = getOptimalSplit(associationName);
        Rectangle[] pixelBounds = getPixelBounds(fontSize, associationNameLines);
        int width = logo.getWidth() + pixelBounds[x].width + Math.max(pixelBounds[NAME].width, pixelBounds[UKIP].width);
        BufferedImage namedLogo = new BufferedImage(width, bounds.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)namedLogo.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, namedLogo.getWidth(), namedLogo.getHeight());
        g.drawImage(logo, 0, (bounds.height - logo.getHeight()) / 2, null);
        g.setColor(UKIP_PURPLE);
        g.setFont(new Font(fontName, Font.BOLD, fontSize));
        for (int i = 0; i < associationNameLines.length; i++) {
            g.drawString(associationNameLines[i],
                    logo.getWidth() + pixelBounds[x].width - pixelBounds[NAME].x, bounds.y - pixelBounds[C].y - pixelBounds[Cy].y * i);
        }
        g.setColor(UKIP_YELLOW);
        if (associationName.length() > 8) {
            g.setFont(new Font(fontName, Font.BOLD, (fontSize * 3) / 2));
        }
        g.drawString("UKIP",
                logo.getWidth() + pixelBounds[x].width - pixelBounds[UKIP].x,
                bounds.y + bounds.height - pixelBounds[o].y - pixelBounds[o].height);
        return namedLogo;
    }

  @Override
  public Color getPrimaryColor() {
    return UKIP_PURPLE;
  }

  @Override
  public Color getSecondaryColor() {
    return UKIP_YELLOW;
  }

  private String[] getOptimalSplit(String associationName) {
        if (associationName.length() <= 20) {
            return new String[] {associationName};
        }
        String[] words = associationName.split(" ");
        if (words.length == 1) {
            return new String[] {words[0]};
        }
        if (words.length == 2) {
            return new String[] {words[0], words[1]};
        }
        String[] lines = rejoin(words, 1);
        int width = Math.max(getWidth(fontSize, lines[0]), getWidth(fontSize, lines[1]));
        for (int i = 2; i < words.length - 1; i++) {
            String[] nextLines = rejoin(words, i);
            int nextWidth = Math.max(getWidth(fontSize, nextLines[0]), getWidth(fontSize, nextLines[1]));
            if (nextWidth < width) {
                lines = nextLines;
                width = nextWidth;
            }
            else {
                break;
            }
        }
        return new String[] {lines[0], lines[1]};
    }

    private String[] rejoin(String[] words, int breakAfter) {
        StringBuilder a = new StringBuilder();
        StringBuilder b = new StringBuilder();
        int i = 0;
        for (; i < breakAfter; i++) {
            if (i != 0) {
                a.append(" ");
            }
            a.append(words[i]);
        }
        for (; i < words.length; i++) {
            if (i != breakAfter) {
                b.append(" ");
            }
            b.append(words[i]);
        }
        return new String[] {a.toString(), b.toString()};
    }

    private int getWidth(int base, String string) {
        Graphics2D g = (Graphics2D)logo.getGraphics();
        g.setFont(new Font(fontName, Font.BOLD, base));
        Rectangle[] rectangles = new Rectangle[5];
        FontRenderContext frc = g.getFontRenderContext();
        return g.getFont().createGlyphVector(frc, string).getPixelBounds(frc, 0, 0).width;
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

    private int findBaseHeightForLogo(int target, int associationNameLength) {
        int base = 1;
        double height = getLogoHeight(associationNameLength, base);
        while (height < target) {
            base *= 2;
            height = getLogoHeight(associationNameLength, base);
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
            height = getLogoHeight(associationNameLength, base);
            LOG.debug(String.format("%d --> %f", base, height));
        }
        if (height > target) {
            double alternate = getLogoHeight(associationNameLength, base - 1);
            if (Math.abs(alternate - target) < Math.abs(height - target)) base--;
        }
        if (height < target) {
            double alternate = getLogoHeight(associationNameLength, base + 1);
            if (Math.abs(alternate - target) < Math.abs(height - target)) base++;
        }
        LOG.info("Best size: " + base);
        return base;
    }

    private Rectangle[] getPixelBounds(int base, String[] name) {
        Graphics2D g = (Graphics2D)logo.getGraphics();
        g.setFont(new Font(fontName, Font.BOLD, base));
        Rectangle[] rectangles = new Rectangle[6];
        FontRenderContext frc = g.getFontRenderContext();
        rectangles[NAME] = g.getFont().createGlyphVector(frc, name[0]).getPixelBounds(frc, 0, 0);
        if (name.length == 2) {
            Rectangle alternate = g.getFont().createGlyphVector(frc, name[1]).getPixelBounds(frc, 0, 0);
            rectangles[NAME].width = Math.max(alternate.width, rectangles[NAME].width);
        }
        rectangles[x] = g.getFont().createGlyphVector(frc, "x").getPixelBounds(frc, 0, 0);
        rectangles[C] = g.getFont().createGlyphVector(frc, "C").getPixelBounds(frc, 0, 0);
        g.setFont(new Font(fontName, Font.BOLD, (base * 3) / 2));
        rectangles[UKIP] = g.getFont().createGlyphVector(frc, "UKIP").getPixelBounds(frc, 0, 0);
        rectangles[o] = g.getFont().createGlyphVector(frc, "o").getPixelBounds(frc, 0, 0);
        rectangles[Cy] = g.getFont().createGlyphVector(frc, "Cy").getPixelBounds(frc, 0, 0);
        return rectangles;
    }

    private double getLogoHeight(int fontSize, int lines, float conservativesBiggerBy) {
        Graphics2D g = (Graphics2D)logo.getGraphics();
        g.setFont(new Font(fontName, Font.BOLD, fontSize));
        FontRenderContext frc = g.getFontRenderContext();
        double height = g.getFont().createGlyphVector(frc, "C").getPixelBounds(frc, 0, 0).getHeight();
        if (lines > 1) {
            height += (lines - 1) * g.getFont().createGlyphVector(frc, "Cy").getPixelBounds(frc, 0, 0).getHeight();
        }
        LOG.debug(String.format("C %d --> %f", fontSize, height));
        height += g.getFont().createGlyphVector(frc, "x").getPixelBounds(frc, 0, 0).getHeight();
        LOG.debug(String.format("Cx %d --> %f", fontSize, height));
        g.setFont(new Font(fontName, Font.BOLD, Math.round(fontSize * conservativesBiggerBy)));
        height += g.getFont().createGlyphVector(frc, "o").getPixelBounds(frc, 0, 0).getHeight();
        LOG.debug(String.format("Cxo %d --> %f", fontSize, height));
        return height;
    }

    private double getLogoHeight(int associationNameLength, int fontSize) {
        if (associationNameLength <= 8) {
            return getLogoHeight(fontSize, 1, 1f);
        }
        if (associationNameLength > 20) {
            return getLogoHeight(fontSize, 2, 1.5f);
        }
        return getLogoHeight(fontSize, 1, 1.5f);
    }



}
