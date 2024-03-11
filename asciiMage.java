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

public class asciiMage {
    public static void main(String[] args) {
        final String ANSI_GREEN = "\u001B[42m";
        final String ANSI_EMPTY = "";

        int closestKey;
        int brightnessValue;

        final String properImagePathString = getImagePathString();
        final BufferedImage image = loadImage(properImagePathString);
        final int imageWidth = image.getWidth();
        final int imageHeight = image.getHeight();
        final TreeMap<Integer, String> brightnessRepresentation = representBrightness();
        final int[] brightnessRepKeysArr = new int[brightnessRepresentation.size()];
        final String[][] charPerPixel = new String[imageHeight][imageWidth];
        final RgbData[][] rgbValues = getRgbValues(image);

        System.out.println("Image loaded.");
        System.out.println("Image width: " + imageWidth);
        System.out.println("Image height: " + imageHeight);

        int i = 0;
        for (int key : brightnessRepresentation.keySet()) {
            brightnessRepKeysArr[i] = key;
            i++;
        }

        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                brightnessValue = calculateBrightnessAverage(rgbValues[y][x]);
                closestKey = getClosestKey(brightnessRepKeysArr, brightnessValue);
                charPerPixel[y][x] = brightnessRepresentation.get(closestKey);
            }
        }

        mainDisplay(charPerPixel, ANSI_EMPTY);

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

    static int calculateBrightnessAverage(RgbData rgbData) {
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

    static void mainDisplay(String[][] charPerPixel, String ANSI_TEXT_COLOR) {
        final String ANSI_RESET = "\u001B[0m";

        if (ANSI_TEXT_COLOR == "") {
            ANSI_TEXT_COLOR = "";
        }

        for (String[] dataOuter : charPerPixel) {
            for (String dataInner : dataOuter) {
                System.out.print(ANSI_TEXT_COLOR + dataInner + ANSI_RESET);
                System.out.print(ANSI_TEXT_COLOR + dataInner + ANSI_RESET);
                System.out.print(ANSI_TEXT_COLOR + dataInner + ANSI_RESET);
            }
            System.out.println();
        }
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