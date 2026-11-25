/**
 * Author: Amar Campbell - 2501977
 * Date: 11/21/2026
 * Description: This class stores every stadium shop's products in products.dat, a random-access
 * file of fixed-length records. Because every record is the same size, record n starts at byte
 * n * RECORD_SIZE, so one product's stock can be read and rewritten in place straight after a
 * paid sale without rewriting the rest of the file.
 *
 * Record layout (strings are fixed-length, padded with spaces, 2 bytes per character):
 *   stadium ID (10 chars), product code (10), name (30), kind ('M' merchandise or 'F' food),
 *   base price (double), quantity in stock (int),
 *   merchandise type (20), size (10), team name (30),
 *   beverage flag (boolean), expiry date as an epoch day (long, NO_EXPIRY when there is none)
 */

package oop_project.persistence;

import oop_project.entities.FoodItem;
import oop_project.entities.FootballLeague;
import oop_project.entities.Merchandise;
import oop_project.entities.Product;
import oop_project.entities.Stadium;
import oop_project.exceptions.DataFileException;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
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

    private static final char MERCHANDISE = 'M';
    private static final char FOOD = 'F';
    private static final long NO_EXPIRY = Long.MIN_VALUE;

    // Byte offset of the quantity field inside a record
    private static final int QUANTITY_OFFSET = 2 * (STADIUM_ID_LENGTH + CODE_LENGTH + NAME_LENGTH) + 2 + 8;
    public static final int RECORD_SIZE = QUANTITY_OFFSET + 4 + 2 * (TYPE_LENGTH + SIZE_LENGTH + TEAM_LENGTH) + 1 + 8;

    private final Path file;

    public ProductFile(Path file) {
        this.file = file;
    }

    public Path getFile() {
        return file;
    }

    // Rewrites the whole file with every shop's inventory, through a temporary file so that an
    // error part-way through cannot destroy the saved data. Returns the number of records written.
    public int saveAll(List<Stadium> stadiums) throws IOException {
        Path temp = file.resolveSibling(file.getFileName() + ".tmp");
        int count = 0;
        try (RandomAccessFile raf = new RandomAccessFile(temp.toFile(), "rw")) {
            raf.setLength(0);
            for (Stadium stadium : stadiums) {
                for (Product product : stadium.getShop().getInventory()) {
                    writeRecord(raf, stadium.getStadiumId(), product);
                    count++;
                }
            }
        } catch (IOException | RuntimeException e) {
            Files.deleteIfExists(temp);
            throw e;
        }
        LeagueFileManager.replace(temp, file);
        return count;
    }

    // Adds each saved product to its stadium's shop. Returns a message for each record that could
    // not be used; that record is skipped and the rest still load. A missing file means no products yet.
    public List<String> loadAll(FootballLeague league) throws IOException {
        List<String> warnings = new ArrayList<>();
        if (!Files.exists(file)) {
            return warnings;
        }
        String fileName = file.getFileName().toString();
        try (RandomAccessFile raf = new RandomAccessFile(file.toFile(), "r")) {
            long records = raf.length() / RECORD_SIZE;
            if (raf.length() % RECORD_SIZE != 0) {
                warnings.add(fileName + ": the file ends with a part record, which was ignored.");
            }
            for (int i = 0; i < records; i++) {
                raf.seek((long) i * RECORD_SIZE);
                try {
                    readRecordInto(raf, league, fileName, i + 1);
                } catch (DataFileException | IllegalArgumentException e) {
                    warnings.add(messageFor(fileName, i + 1, e) + " (skipped)");
                }
            }
        }
        return warnings;
    }

    // Overwrites one product's quantity in place. Returns false when the product is not in the file
    // yet (it was added since the last save), in which case the next save writes it.
    public boolean updateStock(String stadiumId, Product product) throws IOException {
        if (!Files.exists(file)) {
            return false;
        }
        try (RandomAccessFile raf = new RandomAccessFile(file.toFile(), "rw")) {
            long records = raf.length() / RECORD_SIZE;
            for (int i = 0; i < records; i++) {
                long start = (long) i * RECORD_SIZE;
                raf.seek(start);
                String savedStadium = readFixedString(raf, STADIUM_ID_LENGTH);
                String savedCode = readFixedString(raf, CODE_LENGTH);
                if (savedStadium.equalsIgnoreCase(stadiumId) && savedCode.equalsIgnoreCase(product.getProductCode())) {
                    raf.seek(start + QUANTITY_OFFSET);
                    raf.writeInt(product.getQuantityInStock());
                    return true;
                }
            }
        }
        return false;
    }

    // Reads the quantity stored for one product; returns -1 when it is not in the file
    public int readStock(String stadiumId, String productCode) throws IOException {
        if (!Files.exists(file)) {
            return -1;
        }
        try (RandomAccessFile raf = new RandomAccessFile(file.toFile(), "r")) {
            long records = raf.length() / RECORD_SIZE;
            for (int i = 0; i < records; i++) {
                long start = (long) i * RECORD_SIZE;
                raf.seek(start);
                String savedStadium = readFixedString(raf, STADIUM_ID_LENGTH);
                String savedCode = readFixedString(raf, CODE_LENGTH);
                if (savedStadium.equalsIgnoreCase(stadiumId) && savedCode.equalsIgnoreCase(productCode)) {
                    raf.seek(start + QUANTITY_OFFSET);
                    return raf.readInt();
                }
            }
        }
        return -1;
    }

    private static void writeRecord(RandomAccessFile raf, String stadiumId, Product product) throws IOException {
        writeFixedString(raf, stadiumId, STADIUM_ID_LENGTH, "Stadium ID");
        writeFixedString(raf, product.getProductCode(), CODE_LENGTH, "Product code");
        writeFixedString(raf, product.getName(), NAME_LENGTH, "Product name");
        // Polymorphism: the record's last fields depend on which kind of product this is
        if (product instanceof Merchandise merchandise) {
            raf.writeChar(MERCHANDISE);
            raf.writeDouble(product.getBasePrice());
            raf.writeInt(product.getQuantityInStock());
            writeFixedString(raf, merchandise.getType(), TYPE_LENGTH, "Merchandise type");
            writeFixedString(raf, merchandise.getSize(), SIZE_LENGTH, "Size");
            writeFixedString(raf, merchandise.getTeamName(), TEAM_LENGTH, "Team name");
            raf.writeBoolean(false);
            raf.writeLong(NO_EXPIRY);
        } else if (product instanceof FoodItem food) {
            raf.writeChar(FOOD);
            raf.writeDouble(product.getBasePrice());
            raf.writeInt(product.getQuantityInStock());
            writeFixedString(raf, "", TYPE_LENGTH, "Merchandise type");
            writeFixedString(raf, "", SIZE_LENGTH, "Size");
            writeFixedString(raf, "", TEAM_LENGTH, "Team name");
            raf.writeBoolean(food.isBeverage());
            raf.writeLong(food.getExpiryDate() == null ? NO_EXPIRY : food.getExpiryDate().toEpochDay());
        } else {
            throw new IllegalArgumentException("Cannot save product " + product.getProductCode() + " of type "
                    + product.getClass().getSimpleName() + ".");
        }
    }

    private static void readRecordInto(RandomAccessFile raf, FootballLeague league, String fileName, int recordNo) throws IOException {
        String stadiumId = readFixedString(raf, STADIUM_ID_LENGTH);
        String code = readFixedString(raf, CODE_LENGTH);
        String name = readFixedString(raf, NAME_LENGTH);
        char kind = raf.readChar();
        double basePrice = raf.readDouble();
        int quantity = raf.readInt();
        String type = readFixedString(raf, TYPE_LENGTH);
        String size = readFixedString(raf, SIZE_LENGTH);
        String team = readFixedString(raf, TEAM_LENGTH);
        boolean beverage = raf.readBoolean();
        long expiry = raf.readLong();

        Stadium stadium = league.findStadium(stadiumId);
        if (stadium == null) {
            throw new DataFileException(fileName, recordNo, "no stadium with ID " + stadiumId);
        }
        Product product;
        if (kind == MERCHANDISE) {
            product = new Merchandise(code, name, basePrice, quantity, type, size, team);
        } else if (kind == FOOD) {
            product = new FoodItem(code, name, basePrice, quantity, beverage, expiry == NO_EXPIRY ? null : LocalDate.ofEpochDay(expiry));
        } else {
            throw new DataFileException(fileName, recordNo, "unknown product kind '" + kind + "'");
        }
        stadium.getShop().addProduct(product);
    }

    private static String messageFor(String fileName, int recordNo, RuntimeException e) {
        return e instanceof DataFileException ? e.getMessage() : fileName + " record " + recordNo + ": " + e.getMessage();
    }

    private static void writeFixedString(RandomAccessFile raf, String value, int length, String field) throws IOException {
        String text = value == null ? "" : value;
        if (text.length() > length) {
            throw new IllegalArgumentException(field + " \"" + text + "\" is longer than " + length + " characters.");
        }
        StringBuilder padded = new StringBuilder(text);
        while (padded.length() < length) {
            padded.append(' ');
        }
        raf.writeChars(padded.toString());
    }

    private static String readFixedString(RandomAccessFile raf, int length) throws IOException {
        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            chars[i] = raf.readChar();
        }
        return new String(chars).stripTrailing();
    }
}
