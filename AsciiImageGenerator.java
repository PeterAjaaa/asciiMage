import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.TreeMap;
import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class AsciiImageGenerator {
    public static void main(String[] args) {
        final String properImagePathString = getImagePathString();
        final BufferedImage image = loadImage(properImagePathString);
        final TreeMap<Integer, String> brightnessRepresentation = representBrightness();
        final int[] brightnessRepKeysArr = new int[brightnessRepresentation.size()];
        final String[][] brightnessPerPixel = new String[getImageHeight(image)][getImageWidth(image)];
        final RgbData[][] rgbValues = getRgbValues(image);

        System.out.println("Image loaded.");
        System.out.println("Image width: " + getImageWidth(image));
        System.out.println("Image height: " + getImageHeight(image));

        int i = 0;
        for (int key : brightnessRepresentation.keySet()) {
            brightnessRepKeysArr[i] = key;
            i++;
        }

        for (int y = 0; y < getImageHeight(image); y++) {
            for (int x = 0; x < getImageWidth(image); x++) {
                brightnessPerPixel[y][x] = brightnessRepresentation
                        .get(getClosestKey(brightnessRepKeysArr, calculateBrightness(rgbValues[y][x])));
            }
        }

        for (String[] dataOuter : brightnessPerPixel) {
            for (String dataInner : dataOuter) {
                System.out.print(dataInner);
                System.out.print(dataInner);
                System.out.print(dataInner);
            }
            System.out.println();
        }
    }

    static String getImagePathString() {
        boolean validInput = false;
        String stringImagePath = "";
        Path imagePath = null;
        final Scanner scanner = new Scanner(System.in);

        while (!validInput) {
            try {
                System.out.println("Enter image path: ");
                stringImagePath = scanner.nextLine();
                imagePath = Path.of(stringImagePath);

                if (!Files.exists(imagePath, LinkOption.NOFOLLOW_LINKS)) {
                    throw new Exception("File does not exist.");
                } else {
                    validInput = true;
                }

            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                System.err.println("Failed to read image path.");
                System.err.println("Please enter a valid image path.");
            }
        }

        scanner.close();
        return stringImagePath;
    }

    static BufferedImage loadImage(String imagePath) {
        BufferedImage image = null;

        try {
            image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    static int getImageWidth(BufferedImage image) {
        return image.getWidth();
    }

    static int getImageHeight(BufferedImage image) {
        return image.getHeight();
    }

    static RgbData[][] getRgbValues(BufferedImage image) {
        final RgbData[][] rgbValues = new RgbData[image.getHeight()][image.getWidth()];

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y), true);
                rgbValues[y][x] = new RgbData(color.getRed(), color.getGreen(), color.getBlue());
            }
        }

        return rgbValues;
    }

    static int calculateBrightness(RgbData rgbData) {
        return (rgbData.r + rgbData.g + rgbData.b) / 3;
    }

    static TreeMap<Integer, String> representBrightness() {
        final String[] brightnessRepresentationArr = """
                `^\\",:;Il!i~+_-?][}{1)(|\\/tfjrxnuvczXYUJCLQ0OZmwqpdbkhao*#MW&8%B@$
                                """.strip().split("");
        int charCount = brightnessRepresentationArr.length - 1;
        final TreeMap<Integer, String> brightnessRepresentation = new TreeMap<>();

        for (int i = 0; i < brightnessRepresentationArr.length; i++) {
            if (i == 0) {
                brightnessRepresentation.put(0, brightnessRepresentationArr[0]);
            } else {
                brightnessRepresentation.put(255 / i, brightnessRepresentationArr[charCount]);
                charCount--;
            }
        }
        return brightnessRepresentation;
    }

    static int getClosestKey(int[] arr, int target) {
        int low = 0;
        int high = arr.length;
        int mid = 0;

        if (target <= arr[0]) {
            return arr[0];
        }

        if (target >= arr[arr.length - 1]) {
            return arr[arr.length - 1];
        }

        while (low <= high) {
            mid = (low + high) / 2;

            if (arr[mid] == target) {
                return arr[mid];
            } else if (target > arr[mid]) {
                low = mid + 1;
            } else if (target < arr[mid]) {
                high = mid - 1;
            }
        }
        return (arr[low] - target) < (target - arr[high]) ? arr[low] : arr[high];
    }
}

class RgbData {
    final int r;
    final int g;
    final int b;

    public RgbData(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;

    }
}