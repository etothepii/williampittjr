package uk.co.epii.conservatives.williampittjr;

import org.junit.Ignore;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * User: James Robinson
 * Date: 06/10/2013
 * Time: 13:51
 */
public class LogoGeneratorTests {

    private final ConservativeLogoGeneratorImpl conservativeLogoGeneratorImpl = ConservativeLogoGeneratorImpl.getInstance();

    @Test
    @Ignore
    public void toryStyleGuideExampleTest() {
        String[] associationNames = new String[] {
                "Bath",
                "Westbury",
                "Aldershot",
                "Kensington & Chelsea",
                "Bognor Regis & Littlehampton",
                "Croydon Central and South Conservative Federation"};
        BufferedImage[][] logos = new BufferedImage[associationNames.length][];
        int maxWidth = 0;
        int totalHeight = 50;
        for (int i = 0; i < associationNames.length; i++) {
            logos[i] = new BufferedImage[] {
                conservativeLogoGeneratorImpl.getLogo(associationNames[i]),
                conservativeLogoGeneratorImpl.getPaddedLogo(associationNames[i])
            };
            if (maxWidth < logos[i][1].getWidth()) {
                maxWidth = logos[i][1].getWidth();
            }
            totalHeight += 50;
            totalHeight += logos[i][1].getHeight();
        }
        final BufferedImage image = new BufferedImage(maxWidth + 75, totalHeight / 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setTransform(AffineTransform.getScaleInstance(0.5, 0.5));
        int y = 50;
        g.setColor(Color.BLACK);
        for (int i = 0; i < logos.length; i++) {
            g.drawImage(logos[i][0], 50, y, null);
            g.drawImage(logos[i][1], 100 + maxWidth, y, null);
            g.drawRect(50, y, logos[i][0].getWidth(), logos[i][0].getHeight());
            g.drawRect(100 + maxWidth, y, logos[i][1].getWidth(), logos[i][1].getHeight());
            y += logos[i][1].getHeight();
            y += 50;
        }
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        JPanel panel = new JPanel() {
            @Override
            public void paint(Graphics graphics) {
                graphics.drawImage(image, 0, 0, null);
            }
        };
        panel.setSize(new Dimension(image.getWidth(), image.getHeight()));
        panel.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        panel.setMinimumSize(new Dimension(image.getWidth(), image.getHeight()));
        panel.setMaximumSize(new Dimension(image.getWidth(), image.getHeight()));
        frame.getContentPane().add(new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));
        frame.setLocationRelativeTo(null);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        try {
            Thread.sleep(30000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
