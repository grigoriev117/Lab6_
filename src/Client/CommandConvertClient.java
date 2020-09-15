package Client;
import spacemarine.*;
import spacemarine.SpaceMarine.Coordinates;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.fasterxml.jackson.databind.DeserializationFeature;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;

public class CommandConvertClient {

	/**
     * ќбработка команд, вводимых с консоли
	 * @throws IOException 
     */
    public static CommandSimple switcher(String s1, String s2) throws EndOfFileException, IOException {
        switch (s1) {
            case ("help"):
                return new CommandSimple(CommandsList.HELP);
            case ("info"):
                return new CommandSimple(CommandsList.INFO);
            case ("show"):
                return new CommandSimple(CommandsList.SHOW);
            case ("remove_first"):
                return new CommandSimple(CommandsList.REMOVE_FIRST);
            case ("add"):
                return add(s2);
            case ("magic"):
            	return magic(s2);
            case ("update"):
                return update(s2);
            case ("remove_by_id"):
                return removeById(s2);
            case ("clear"):
                return new CommandSimple(CommandsList.CLEAR);
            case ("execute_script"):
                return new CommandScript(s2);
            case ("exit"):
                return new CommandSimple(CommandsList.EXIT);
            case ("filter_less_than_loyal"):
                return filter_less_than_loyal(s2);
            case ("remove_all_by_weapon_type"):
                return remove_all_by_weapon_type(s2);
            case ("remove_head"):
                return new CommandSimple(CommandsList.REMOVE_HEAD);
            case ("head"):
                return new CommandSimple(CommandsList.HEAD);
            case ("group_counting_by_chapter"):
                return new CommandSimple(CommandsList.GROUP_COUNTING_BY_CHAPTER);
            default:
                Writer.writeln("“акой команды нет");
        }
        return null;
    }
    
    public static CommandSimple remove_all_by_weapon_type(String s2) throws EndOfFileException{
		
		return new CommandWArg(s2);
	}

	public static CommandSimple filter_less_than_loyal(String s2) throws EndOfFileException {
		
		return new CommandBoolArg(s2);
	}

	/**
     * ƒобавл€ет элемент в список
     */
    public static CommandSimple add(String s) throws EndOfFileException {
        SpaceMarine sm = AddSM(s);
        return new CommandArgs(CommandsList.ADD, sm);
    }
    
    public static Checker<Boolean> boolCheck = (Boolean B) -> {
        if (B != null) return B;
        else throw new FailedCheckException();
    };
    
    /**
     * —читывает скрипт
     */
    
    /**
     * ”дал€ет элемент по его id
     */
    public static CommandSimple removeById(String s) throws EndOfFileException {
        Long id;
        try {
            id = SpaceMarine.idCheck.checker(Long.parseLong(s));
        } catch (NumberFormatException | FailedCheckException e) {
            id = ConsoleClient.handlerL("¬ведите Long id: ", SpaceMarine.idCheck);
        }
        return new CommandLonArg(id);
    }
    
    public static CommandSimple magic(String s) throws EndOfFileException, IOException {
    	SpaceMarine sm = new SpaceMarine(null, s, null, null, false, s, null, null);
    	String smtr;
        //hmm
        sm.setId(null);
        try {
            SpaceMarine.nameCheck.checker(s);
            Writer.writeln("ѕоле name: " + s);
        } catch (FailedCheckException e) {
            s = ConsoleClient.handlerS("¬ведите String name, диной больше 0: ", SpaceMarine.nameCheck);
        }
        sm.setName(s);
        Writer.writeln("¬вoд полей Coordinates:");
        int cx = ConsoleClient.handlerI("¬ведите int x: ", Coordinates.xCheck);
        Double cy = ConsoleClient.handlerD("¬ведите Double y: ", Coordinates.yCheck);
        sm.setCoordinates(new Coordinates(cx, cy));

        LocalDate creationTime = LocalDate.now();
        sm.setCreationDate(creationTime);
        
        Double health1 = ConsoleClient.handlerD("¬ведите Double health, больше 0:", SpaceMarine.healthCheck);
        sm.setHealth(health1);

        boolean loyal1 = ConsoleClient.handlerB("¬ведите boolean loyal", boolCheck);
        sm.setLoyal(loyal1);
        
        String achievements = ConsoleClient.handlerS("¬ведите String achievements", SpaceMarine.nameCheck);
        sm.setAchievements(achievements);

        Writer.writeln("¬вoд полей Chapter");
        String name1 = ConsoleClient.handlerS("¬ведите String name: ", Chapter.cCheck);
        String parentLegion1 = ConsoleClient.handlerS("¬ведите String parentLegion: ", Chapter.cCheck);
        sm.setChapter(new Chapter(name1, parentLegion1));
        
        String weaponType1 = ConsoleClient.handlerS("¬ведите Weapon weaponType {\r\n" + 
        		"    HEAVY_BOLTGUN,\r\n" + 
        		"    BOLT_RIFLE,\r\n" + 
        		"    PLASMA_GUN,\r\n" + 
        		"    COMBI_PLASMA_GUN,\r\n" + 
        		"    INFERNO_PISTOL;\r\n" + 
        		"} ", SpaceMarine.nameCheck);
        
        if (weaponType1.equals("HEAVY_BOLTGUN") || weaponType1.equals("BOLT_RIFLE") || weaponType1.equals("PLASMA_GUN") || weaponType1.equals("COMBI_PLASMA_GUN") || weaponType1.equals("INFERNO_PISTOL"))
        {
        	sm.setWeaponType(SpaceMarine.Weapon.valueOf(weaponType1));
        }
        else {
        	Writer.writeln("¬ведите из предложенных");
        	Writer.writeln("    HEAVY_BOLTGUN,\r\n" + 
    		"    BOLT_RIFLE,\r\n" + 
    		"    PLASMA_GUN,\r\n" + 
    		"    COMBI_PLASMA_GUN,\r\n" + 
    		"    INFERNO_PISTOL;\r\n"
    		);
        }
        smtr = sm.toString();
       // FileOutputStream outputStream = new FileOutputStream("save.ser");
       // ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
       // smtr = readUsingFiles("save.ser");
        
        
        //vot eto nado
        StringWriter writer1 = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.writeValue(writer1, sm);
        smtr = writer1.toString();
        //smtr = sm.toString();
        return new CommandMagic(smtr);
    }
    
 // читаем файл в строку с помощью класса Files
    private static String readUsingFiles(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }
 
    
    /**
     * ѕерезаписывает элемент списка с указанным id
     */
    public static CommandSimple update(String s) throws EndOfFileException {
        Long id;
        try {
            id = SpaceMarine.idCheck.checker(Long.parseLong(s));
        } catch (NumberFormatException | FailedCheckException e) {
            id = ConsoleClient.handlerL("¬ведите Long id: ", SpaceMarine.idCheck);
        }
        String name = ConsoleClient.handlerS("¬ведите String: name", SpaceMarine.nameCheck);
        SpaceMarine sm1  = AddSM(name);
        sm1.setId(id);
        return new CommandArgs(CommandsList.UPDATE, sm1); 
    }
    
    public static SpaceMarine AddSM(String s) throws EndOfFileException {
        SpaceMarine sm = new SpaceMarine(null, s, null, null, false, s, null, null);
        //hmm
        sm.setId(null);
        try {
            SpaceMarine.nameCheck.checker(s);
            Writer.writeln("ѕоле name: " + s);
        } catch (FailedCheckException e) {
            s = ConsoleClient.handlerS("¬ведите String name, диной больше 0: ", SpaceMarine.nameCheck);
        }
        sm.setName(s);

        Writer.writeln("¬вoд полей Coordinates:");
        int cx = ConsoleClient.handlerI("¬ведите int x: ", Coordinates.xCheck);
        Double cy = ConsoleClient.handlerD("¬ведите Double y: ", Coordinates.yCheck);
        sm.setCoordinates(new Coordinates(cx, cy));

        LocalDate creationTime = LocalDate.now();
        sm.setCreationDate(creationTime);
        
        Double health1 = ConsoleClient.handlerD("¬ведите Double health, больше 0:", SpaceMarine.healthCheck);
        sm.setHealth(health1);

        boolean loyal1 = ConsoleClient.handlerB("¬ведите boolean loyal", boolCheck);
        sm.setLoyal(loyal1);
        
        String achievements = ConsoleClient.handlerS("¬ведите String achievements", SpaceMarine.nameCheck);
        sm.setAchievements(achievements);

        Writer.writeln("¬вoд полей Chapter");
        String name1 = ConsoleClient.handlerS("¬ведите String name: ", SpaceMarine.Chapter.cCheck);
        String parentLegion1 = ConsoleClient.handlerS("¬ведите String parentLegion: ", SpaceMarine.Chapter.cCheck);
        sm.setChapter(new SpaceMarine.Chapter(name1, parentLegion1));
        
        String weaponType1 = ConsoleClient.handlerS("¬ведите Weapon weaponType {\r\n" + 
        		"    HEAVY_BOLTGUN,\r\n" + 
        		"    BOLT_RIFLE,\r\n" + 
        		"    PLASMA_GUN,\r\n" + 
        		"    COMBI_PLASMA_GUN,\r\n" + 
        		"    INFERNO_PISTOL;\r\n" + 
        		"} ", SpaceMarine.nameCheck);
        
        if (weaponType1.equals("HEAVY_BOLTGUN") || weaponType1.equals("BOLT_RIFLE") || weaponType1.equals("PLASMA_GUN") || weaponType1.equals("COMBI_PLASMA_GUN") || weaponType1.equals("INFERNO_PISTOL"))
        {
        	sm.setWeaponType(SpaceMarine.Weapon.valueOf(weaponType1));
        }
        else {
        	Writer.writeln("¬ведите из предложенных");
        	Writer.writeln("    HEAVY_BOLTGUN,\r\n" + 
    		"    BOLT_RIFLE,\r\n" + 
    		"    PLASMA_GUN,\r\n" + 
    		"    COMBI_PLASMA_GUN,\r\n" + 
    		"    INFERNO_PISTOL;\r\n"
    		);
        }


        return sm;
    }


	
}
