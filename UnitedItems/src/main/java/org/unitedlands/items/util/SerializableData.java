package org.unitedlands.items.util;

import java.io.*;

public enum SerializableData {
	Farming("plugins/UnitedItems/farming");
    
    private final String fileName;

    SerializableData(String fileDir) {
        this.fileName = fileDir;
    }

    public <T> T readFromDatabase(String fileName, Class<T> clazz) {
        File file = new File(this.fileName, fileName);
        if (!file.exists()) {
            return null;
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return clazz.cast(in.readObject());
        } catch (IOException | ClassNotFoundException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            return null;
        }
    }

    public void writeToDatabase(Object obj, String fileName) {
        File file = new File(this.fileName, fileName);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(obj);
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

}
