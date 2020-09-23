package Server;
import spacemarine.*;
import command.*;
import Exceptions.FailedCheckException;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import Exceptions.EndOfFileException;
import Exceptions.IncorrectFileNameException;

import java.util.Date;
import java.util.LinkedList;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import java.util.Collections;

public class CommandConvert {
	
	public static Writer switcher(CommandSimple com, Collection c) throws IOException, EndOfFileException {
        switch (com.getCurrent()) {
            case HELP:
                return help();
            case INFO:
                return info(c);
            case SHOW:
                return show(c);
            case ADD:
                return add(c, com);
            case UPDATE:
                return update(c, com);
            case REMOVE_BY_ID:
                return removeById(c, com);
            case CLEAR:
                return clear(c);
            case EXECUTE_SCRIPT:
                return executeScript(c, com);
            case REMOVE_FIRST:
                return removeFirst(c);
            case HEAD:
                return head(c);
            case REMOVE_HEAD:
                return remove_head(c);
            case REMOVE_ALL_BY_WEAPON_TYPE:
                return remove_all_by_weapon_type(c, com);
            case GROUP_COUNTING_BY_CHAPTER:
                return group_counting_by_chapter(c);
            case FILTER_LESS_THAN_LOYAL:
                return filter_less_than_loyal(c, com);
            default:
                Writer.writeln("Такой команды нет");
        }
        return new Writer();
    }

	/**
     * Показывает информацию по всем возможным командам
     */
    public static Writer help() {
        Writer w = new Writer();
        w.addToList(true,
        		"help : вывести справку по доступным командам\n"+
    					"info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n"+
    					"show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n"+
    					"add {element} : добавить новый элемент в коллекцию\n"+
    					"update id {element} : обновить значение элемента коллекции, id которого равен заданному\n"+
    					"remove_by_id id : удалить элемент из коллекции по его id\n"+
    					"clear : очистить коллекцию\n"+
    					"save : сохранить коллекцию в файл\n"+
    					"execute_script file_name : считать и исполнить скрипт из указанного файла.\n"+
    					"В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.\n"+
    					"exit : завершить программу (без сохранения в файл)\n"+
    					"remove_first : удалить первый элемент из коллекции\n"+
    					"head : вывести первый элемент коллекции\n"+
    					"remove_head : вывести первый элемент коллекции и удалить его\n"+
    					"remove_all_by_weapon_type weaponType : удалить из коллекции все элементы, значение поля weaponType которого эквивалентно заданному\n"+
    					"group_counting_by_chapter : сгруппировать элементы коллекции по значению поля chapter, вывести количество элементов в каждой группе\n"+
    					"filter_less_than_loyal loyal : вывести элементы, значение поля loyal которых меньше заданного\n"
       );

        w.addToList(false,"end");
        return w;
    }
    
    public static Writer info(Collection collection) {
        Writer w = new Writer();
        w.addToList(true, "Тип коллекции: " + collection.list.getClass().getName());
        w.addToList(true, "Колличество элементов: " + collection.list.size());
        w.addToList(true, "Коллеция создана: " + collection.getDate());

        w.addToList(false,"end");
        return w;
    }
    
    
    /**
     * Считывает и исполняет скрипт из указанного файла.
     * В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме
     * @throws IOException 
     */
    public static Writer executeScript(Collection c, CommandSimple command) throws IOException {
        Writer w = new Writer();
        String s = (String) command.returnObj();
        boolean programIsWorking = true;
        try (Reader reader = new Reader(s)) {
            if (RecursionHandler.isContains(s)) {
                RecursionHandler.addToFiles(s);
                String[] com;
                w.addToList(false, "\u001B[33m" + "Чтение команды в файле " + s + ": " + "\u001B[0m");
                String line = reader.read(w);
                while (line != null && programIsWorking) {
                    com = CommandReader.splitter(line);
                    programIsWorking = Command.switcher(w, reader, c, com[0], com[1]);
                    w.addToList(false, "\u001B[33m" + "Чтение команды в файле " + s + ": " + "\u001B[0m");
                    line = reader.read(w);
                }
                RecursionHandler.removeLast();
            } else
                w.addToList(true, "\u001B[31m" + "Найдено повторение" + "\u001B[0m");

        } catch (Exceptions.IncorrectFileNameException e) {
            w.addToList(true, "\u001B[31m" + "Неверное имя файла" + "\u001B[0m");
        } catch (Exceptions.EndOfFileException e) {
            w.addToList(true, "\u001B[31m" + "Неожиданный конец файла " + s + "\u001B[0m");
            RecursionHandler.removeLast();
        } catch (FileNotFoundException e) {
            w.addToList(true, "\u001B[31m" + "Файл не найден" + "\u001B[0m");
        } catch (FailedCheckException | NumberFormatException e) {
            w.addToList(true, "\u001B[31m" + "Файл содержит неправильные данные" + "\u001B[0m");
            RecursionHandler.removeLast();
        }

        w.addToList(false,"end");
        return w;
    }
    
    /**
     * Перезаписывает элемент списка с указанным id
     */
    public static Writer update(Collection c, CommandSimple com) {
        Writer w = new Writer();
        Long id = ((SpaceMarine) com.returnObj()).getId();
        SpaceMarine sm = c.searchById(id);
        if (sm == null) {
            w.addToList(true, "Такого элемента нет");
            w.addToList(false,"end");
        }
        else {
        c.list.set(c.list.indexOf(sm), (SpaceMarine) com.returnObj());
        Collections.sort(c.list);
        w.addToList(true, "Элемент с id: " + id + " успешно изменен");

        w.addToList(false,"end"); }
        return w;
    }
    
    /**
     * Удаляет все элементы из коллекции
     */
    public static Writer clear(Collection c) {
        Writer w = new Writer();
        c.list.clear();
        w.addToList(true, "Теперь в коллекции нет элементов");
        w.addToList(false,"end");
        return w;
    }
    
    public static Writer removeById(Collection c, CommandSimple com) {
        Writer w = new Writer();
        Long id = (Long) com.returnObj();
        SpaceMarine smm = c.searchById(id);
        if (smm == null) {
            w.addToList(true, "Такого элемента нет");

            w.addToList(false,"end");
            return w;
        }
        c.list.remove(smm);
        w.addToList(true, "Элемент с id: " + id + " успешно удален");
        Collections.sort(c.list);

        w.addToList(false,"end");
        return w;
    }
    
    /**
     * Выводит все элементы списка
     */
    public static Writer show(Collection c) {
        Writer w = new Writer();
        if (c.list.isEmpty())
            w.addToList(true, "В коллекции нет элементов");
        else
            c.list.forEach(s -> w.addToList(true, s.toString()));

        w.addToList(false,"end");
        return w;
    }
    
    public static SpaceMarine SM_ID(SpaceMarine sm, Long id) {
        sm.setId(id);
        return sm;
    }

    /**
     * Добавляет элемент в список
     */
    public static Writer add(Collection c, CommandSimple com) {
        Writer w = new Writer();
        Long id = c.getRandId();
        c.list.add(SM_ID((SpaceMarine) com.returnObj(), id));
        Collections.sort(c.list);
        w.addToList(true, "Элемент с id: " + id + " добавлен в коллекцию");
        w.addToList(false,"end");
        return w;
    }
    
    
    
    //remove_first : удалить первый элемент из коллекции
    public static Writer removeFirst(Collection c) throws EndOfFileException {
    	Writer w = new Writer();
    	int size = c.list.size();
        int i = 0;
        if (i < size) {
                c.list.remove(c.list.get(i));
            }
        else  {
        	w.addToList(true, "\u001B[31m" + "В коллекции нет элементов" + "\u001B[0m");}
        Collections.sort(c.list);
        w.addToList(false,"end");
        return w;
    }
      //  head : вывести первый элемент коллекции
        public static Writer head(Collection c) throws EndOfFileException {
        	Writer w = new Writer();
		int size = c.list.size();
		int i = 0;
		if (i < size) {
			c.list.stream().limit(1).forEach(sm -> w.addToList(true, c.list.get(i).toString()));
		}
		else {
			w.addToList(true, "\u001B[31m" + "В коллекции нет элементов" + "\u001B[0m");}
			Collections.sort(c.list);
		w.addToList(false,"end");
		return w;  
        }
    //remove_head : вывести первый элемент коллекции и удалить его
	public static Writer remove_head(Collection c) throws EndOfFileException {
		Writer w = new Writer();
		int size = c.list.size();
		int i = 0;
		if (i < size) {
			c.list.stream().limit(1).forEach(sm -> w.addToList(true, c.list.get(i).toString()));
			c.list.remove(c.list.get(i));
		}
		else {
			w.addToList(true, "\u001B[31m" + "В коллекции нет элементов" + "\u001B[0m");}
			Collections.sort(c.list);
		w.addToList(false,"end");
		return w;  
	}
            //group_counting_by_chapter
            
       public static Writer group_counting_by_chapter(Collection c) {
    	   Writer w = new Writer();     
    	   if (c.list.isEmpty()) {
                	w.addToList(true, "В коллекции нет элементов");
                	w.addToList(false,"end");
                	return w;
                }
                else {
                	    LinkedList lan = new LinkedList<>();
    			    c.list.stream().filter(sm -> !lan.contains(sm.getChapter().getParentLegion())).forEach(sm -> lan.add(sm.getChapter().getParentLegion()));
			    for (int j = 0; j < lan.size(); j++) {
				    w.addToList(true, "Легион:");
				    w.addToList(true, lan.get(j));
				    int k = 0;
				    for (SpaceMarine sm : c.list) {
				    	if (sm.getChapter().getParentLegion().equals(lan.get(j))) {
				    	w.addToList(true, sm.toString());
				    	k = k+1;
			   	    }
			    }
			    w.addToList(true, "Всего элементов: " + k);
			    }
			    w.addToList(false,"end");
			    return w;

    }}           
                /**
                 * remove_all_by_weapon_type
                 * @param w 
                 */
    public static Writer remove_all_by_weapon_type(Collection c, CommandSimple com) {
    	Writer w = new Writer();
    	String s = (String) com.returnObj();
        if (s.equals("HEAVY_BOLTGUN") || s.equals("BOLT_RIFLE") || s.equals("PLASMA_GUN") || s.equals("COMBI_PLASMA_GUN") || s.equals("INFERNO_PISTOL"))
        {
        	
		int i = 0;
		List<Integer> num = new ArrayList<Integer>();

                   for (SpaceMarine sm : c.list) {
                        if (sm.getWeaponType().toString().equals(s)) {
				num.add(i);
                        	 }
			else {
				i = i+1;
			}}
		for (int j = 0; j < num.size(); j++)
		{
		c.list.remove(c.list.get(num.get(j)));
                  }
		Collections.sort(c.list); 
        	w.addToList(true, "\u001B[32m" + "..." + "\u001B[32m");
        	w.addToList(false,"end");
        }
        else {
        	w.addToList(true, "\u001B[32m" + "Такого типа оружия пока нет!" + "\u001B[32m");
        	w.addToList(false,"end");
            return w;
        	}
		return w;
                }

            
                /**
                 * filter_less_than_loyal
                 * @param w 
                 */
                public static Writer filter_less_than_loyal(Collection c, CommandSimple com) throws EndOfFileException {
                	Writer w = new Writer();
                	String s = (String) com.returnObj();
                	if (s == "0") w.addToList(true, "Таких элементов нет");
                    else {
                    	for (SpaceMarine sm : c.list) {
                            if (sm.getLoyal() == false)
                            	w.addToList(true, sm.toString());;
                        }
                    }
                    Collections.sort(c.list);
                    w.addToList(false,"end");
                    return w;
                }
                

	
	
}
