/**
 * Author: Amar Campbell - 2501977
 * Date: 11/21/2026
 * Description: This class stores every stadium shop's products in products.dat, a random-access
 * file of fixed-length records. Every record is the same size, so record n starts at byte
 * n * RECORD_SIZE. That lets one product's stock be changed in place after a sale, without
 * rewriting the rest of the file.
 *
 * Record layout (text is padded with spaces, and each character takes 2 bytes):
 *   stadium ID (10 characters), product code (10), name (30), kind ('M' merchandise or 'F' food),
 *   base price (double, 8 bytes), quantity in stock (int, 4 bytes),
 *   merchandise type (20 characters), size (10), team name (30),
 *   beverage (boolean, 1 byte), expiry date as a day number (long, 8 bytes)
 */

package oop_project.persistence;

import oop_project.entities.FoodItem;
import oop_project.entities.FootballLeague;
import oop_project.entities.Merchandise;
import oop_project.entities.Product;
import oop_project.entities.Stadium;
import oop_project.exceptions.DataFileException;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductFile {
    public static final int STADIUM_ID_LENGTH = 10;
    public static final int CODE_LENGTH = 10;
    public static final int NAME_LENGTH = 30;
    public static final int TYPE_LENGTH = 20;
    public static final int SIZE_LENGTH = 10;
    public static final int TEAM_LENGTH = 30;

    // Where the quantity starts inside a record: after 50 characters, the kind (2 bytes) and the price (8 bytes)
    private static final int QUANTITY_OFFSET = 110;
    public static final int RECORD_SIZE = 243;

    // Saved when a food item has no expiry date
    private static final long NO_EXPIRY = Long.MIN_VALUE;

    private File file;

    public ProductFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    // Rewrites the whole file with every shop's products, through a temporary file.
    // Returns the number of records written.
    public int saveAll(List<Stadium> stadiums) throws IOException {
        File temp = new File(file.getPath() + ".tmp");
        RandomAccessFile output = new RandomAccessFile(temp, "rw");
        int count = 0;
        try {
            output.setLength(0);
            for (Stadium stadium : stadiums) {
                for (Product product : stadium.getShop().getInventory()) {
                    writeRecord(output, stadium.getStadiumId(), product);
                    count++;
                }
            }
        } finally {
            output.close();
        }
        LeagueFileManager.replace(temp, file);
        return count;
    }

    // Adds each saved product to its stadium's shop. Returns a message for each record that was
    // skipped. A missing file means there are no products yet.
    public List<String> loadAll(FootballLeague league) throws IOException {
        List<String> warnings = new ArrayList<>();
        if (!file.exists()) {
            return warnings;
        }
        RandomAccessFile input = new RandomAccessFile(file, "r");
        try {
            long records = input.length() / RECORD_SIZE;
            for (int i = 0; i < records; i++) {
                input.seek((long) i * RECORD_SIZE);
                try {
                    readRecord(input, league);
                } catch (RuntimeException e) {
                    warnings.add(file.getName() + " record " + (i + 1) + ": " + e.getMessage() + " (skipped)");
                }
            }
        } finally {
            input.close();
        }
        return warnings;
    }

    // Overwrites one product's quantity in place. Returns false when the product is not in the
    // file yet, because it was added after the last save.
    public boolean updateStock(String stadiumId, Product product) throws IOException {
        if (!file.exists()) {
            return false;
        }
        RandomAccessFile output = new RandomAccessFile(file, "rw");
        try {
            long start = findRecord(output, stadiumId, product.getProductCode());
            if (start == -1) {
                return false;
            }
            output.seek(start + QUANTITY_OFFSET);
            output.writeInt(product.getQuantityInStock());
            return true;
        } finally {
            output.close();
        }
    }

    // Reads the quantity saved for one product; returns -1 when it is not in the file
    public int readStock(String stadiumId, String productCode) throws IOException {
        if (!file.exists()) {
            return -1;
        }
        RandomAccessFile input = new RandomAccessFile(file, "r");
        try {
            long start = findRecord(input, stadiumId, productCode);
            if (start == -1) {
                return -1;
            }
            input.seek(start + QUANTITY_OFFSET);
            return input.readInt();
        } finally {
            input.close();
        }
    }

    // Returns the byte where the product's record starts, or -1 when it is not in the file
    private long findRecord(RandomAccessFile input, String stadiumId, String productCode) throws IOException {
        long records = input.length() / RECORD_SIZE;
        for (int i = 0; i < records; i++) {
            long start = (long) i * RECORD_SIZE;
            input.seek(start);
            String savedStadium = readText(input, STADIUM_ID_LENGTH);
            String savedCode = readText(input, CODE_LENGTH);
            if (savedStadium.equalsIgnoreCase(stadiumId) && savedCode.equalsIgnoreCase(productCode)) {
                return start;
            }
        }
        return -1;
    }

    private void writeRecord(RandomAccessFile output, String stadiumId, Product product) throws IOException {
        // Start with empty values, then fill in the ones this kind of product has
        char kind = 'F';
        String type = "";
        String size = "";
        String team = "";
        boolean beverage = false;
        long expiry = NO_EXPIRY;
        if (product instanceof Merchandise) {
            Merchandise merchandise = (Merchandise) product;
            kind = 'M';
            type = merchandise.getType();
            size = merchandise.getSize();
            team = merchandise.getTeamName();
        } else if (product instanceof FoodItem) {
            FoodItem food = (FoodItem) product;
            beverage = food.isBeverage();
            if (food.getExpiryDate() != null) {
                expiry = food.getExpiryDate().toEpochDay();
            }
        }
        writeText(output, stadiumId, STADIUM_ID_LENGTH);
        writeText(output, product.getProductCode(), CODE_LENGTH);
        writeText(output, product.getName(), NAME_LENGTH);
        output.writeChar(kind);
        output.writeDouble(product.getBasePrice());
        output.writeInt(product.getQuantityInStock());
        writeText(output, type, TYPE_LENGTH);
        writeText(output, size, SIZE_LENGTH);
        writeText(output, team, TEAM_LENGTH);
        output.writeBoolean(beverage);
        output.writeLong(expiry);
    }

    // Reads the fields in the same order writeRecord writes them
    private void readRecord(RandomAccessFile input, FootballLeague league) throws IOException {
        String stadiumId = readText(input, STADIUM_ID_LENGTH);
        String code = readText(input, CODE_LENGTH);
        String name = readText(input, NAME_LENGTH);
        char kind = input.readChar();
        double basePrice = input.readDouble();
        int quantity = input.readInt();
        String type = readText(input, TYPE_LENGTH);
        String size = readText(input, SIZE_LENGTH);
        String team = readText(input, TEAM_LENGTH);
        boolean beverage = input.readBoolean();
        long expiry = input.readLong();

        Stadium stadium = league.findStadium(stadiumId);
        if (stadium == null) {
            throw new DataFileException("no stadium with ID " + stadiumId);
        }
        if (kind == 'M') {
            stadium.getShop().addProduct(new Merchandise(code, name, basePrice, quantity, type, size, team));
        } else if (kind == 'F') {
            LocalDate expiryDate = null;
            if (expiry != NO_EXPIRY) {
                expiryDate = LocalDate.ofEpochDay(expiry);
            }
            stadium.getShop().addProduct(new FoodItem(code, name, basePrice, quantity, beverage, expiryDate));
        } else {
            throw new DataFileException("unknown product kind '" + kind + "'");
        }
    }

    // Writes text padded with spaces so that it always takes the same number of characters
    private void writeText(RandomAccessFile output, String text, int length) throws IOException {
        if (text.length() > length) {
            throw new IllegalArgumentException("\"" + text + "\" is longer than " + length + " characters.");
        }
        String padded = text;
        while (padded.length() < length) {
            padded = padded + " ";
        }
        output.writeChars(padded);
    }

    // Reads the characters back and removes the padding
    private String readText(RandomAccessFile input, int length) throws IOException {
        String text = "";
        for (int i = 0; i < length; i++) {
            text = text + input.readChar();
        }
        return text.trim();
    }
}
