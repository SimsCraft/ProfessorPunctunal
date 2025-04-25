package com.simcraft.managers;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * The ImageManager class manages the loading and processing of images.
 */
public class ImageManager {

    // ----- CONSTRUCTORS -----
    /**
     * Private constructor to prevent instantiation.
     */
    private ImageManager() {
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Loads an image from a file path (using ImageIcon).
     *
     * @param fileName The path to the image.
     * @return The Image object.
     */
    public static Image loadImage(String fileName) {
        return new ImageIcon(fileName).getImage();
    }

    /**
     * Loads a BufferedImage from the provided file path. If the image is
     * bundled in the JAR (or the classpath), use a URL. Otherwise, load it as a
     * regular file.
     *
     * @param filepath The file path (relative or absolute).
     * @return The loaded image object (if possible); {@code null} if a
     * null/empty filepath is provided or the image cannot be found.
     */
    public static BufferedImage loadBufferedImage(final String filepath) {
        if (filepath == null || filepath.isEmpty()) {
            return null;
        }
        BufferedImage bufferedImage = null;

        try {
            // Try loading as a resource (for classpath resources, e.g., inside JAR file)
            URL imageUrl = ImageManager.class.getResource(filepath);
            if (imageUrl != null) {
                // If URL is found (i.e., resource exists in classpath), load it
                bufferedImage = ImageIO.read(imageUrl);
            } else {
                // If URL is not found, try loading as a normal file (e.g., file system)
                File file = new File(filepath);
                if (file.exists()) {
                    bufferedImage = ImageIO.read(file);
                }
            }
        } catch (IOException e) {
            System.err.println("Error opening file " + filepath + ": " + e.getMessage());
        }

        return bufferedImage;
    }

    /**
     * Makes a copy of the given BufferedImage.
     *
     * @param source The source BufferedImage to copy.
     * @return The copied BufferedImage.
     */
    public static BufferedImage copyImage(BufferedImage source) {
        if (source == null) {
            return null;
        }

        int imageWidth = source.getWidth();
        int imageHeight = source.getHeight();

        BufferedImage copy = new BufferedImage(
                imageWidth,
                imageHeight,
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2d = copy.createGraphics();

        // Copy the image content
        g2d.drawImage(source, 0, 0, null);
        g2d.dispose();

        return copy;
    }

    /**
     * Creates a new BufferedImage that is a scaled version of the original,
     * with the original image drawn onto it, scaled by the provided factor.
     * <p>
     * The resulting dimensions (width and height) are rounded to the nearest integer.
     *
     * @param originalImage The BufferedImage to scale.
     * @param scaleX The factor by which to scale the image's width.
     * @param scaleY The factor by which to scale the image's height. Use
     * absolute values.
     * @return A new BufferedImage that is scaled by the provided factors, or
     * null if the original image is null.
     */
    public static BufferedImage scaleBufferedImageSize(final BufferedImage originalImage, double scaleX, double scaleY) {
        if (originalImage == null) {
            return null;
        }

        scaleX = Math.abs(scaleX);
        scaleY = Math.abs(scaleY);

        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        int scaledWidth = (int) Math.round(originalWidth * scaleX); // Use Math.round for better scaling
        int scaledHeight = (int) Math.round(originalHeight * scaleY); // Use Math.round for better scaling

        BufferedImage scaled = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaled.createGraphics();
        g2d.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();

        return scaled;
    }

    /**
     * Creates a new BufferedImage that is a uniformly scaled version of the
     * original, with the original image drawn onto it, scaled by the provided
     * factor.
     * <p>
     * The resulting dimensions (width and height) are rounded to the nearest integer.
     *
     * @param originalImage The BufferedImage to scale.
     * @param scaleFactor The factor by which to scale both the width and
     * height. Use absolute value.
     * @return A new BufferedImage that is scaled by the provided factor, or
     * null if the original image is null.
     */
    public static BufferedImage scaleBufferedImageSize(final BufferedImage originalImage, double scaleFactor) {
        return scaleBufferedImageSize(originalImage, scaleFactor, scaleFactor);
    }
}
