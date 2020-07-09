package Server;

import spacemarine.*;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Класс для хранения и обработки LinkedList
 */
public class Collection {

	
	
    /**
     * Дата создания списка
     */
    private Date date = new Date();
    /**
     * Список, в котором хранятся элементы типа SpaceMarine
     */
    public LinkedList<SpaceMarine> list = new LinkedList<>();

    /**
     * Метод, возвращающий список, удобный для сохранения в формат CSV
     * @throws IOException 
     */
    public static Collection startFromSave(String[] args) throws IOException {
        if (args.length > 0) {
            File saveFile = new File(args[0]);
            if (saveFile.exists()) {
                SaveManagement.setFile(saveFile);
                return SaveManagement.listFromSave();
            }
        }
        return new Collection();
    }

    /**
     * Метод, осуществляющий поиск элемента по id
     */
    public SpaceMarine searchById(Long id) {
        for (SpaceMarine r : list) {
            if (r.getId().equals(id))
                return r;
        }
        return null;
    }

    public Date getDate() {
        return date;
    }

    /**
     * Метод, возвращающий уникальный id
     */
    public Long getRandId() {
        Long id;
        do {
            id = (long) (1 + Math.random() * (Long.MAX_VALUE - 1));
        } while (this.searchById(id) != null);
        return id;
    }
}