
package Server;

import command.*;
import spacemarine.*;
import spacemarine.Writer;

import org.apache.log4j.Logger;
import org.apache.log4j.varia.NullAppender;

import Exceptions.EndOfFileException;

import org.apache.log4j.LogManager;
import org.apache.log4j.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Properties;
import java.util.ResourceBundle;

import recources.*;


public class Server {
	
    private static final Logger logger = LogManager.getLogger(Server.class);
    private static int number = 0;
    private static Collection collection;
    public static void main(String[] args) throws IOException, EndOfFileException {
        Properties properties=new Properties();
        properties.setProperty("log4j.rootLogger","TRACE,stdout,MyFile");
        properties.setProperty("log4j.rootCategory","TRACE");

        properties.setProperty("log4j.appender.stdout",     "org.apache.log4j.ConsoleAppender");
        properties.setProperty("log4j.appender.stdout.layout",  "org.apache.log4j.PatternLayout");
        properties.setProperty("log4j.appender.stdout.layout.ConversionPattern","%d{yyyy/MM/dd HH:mm:ss.SSS} [%5p] %t (%F) - %m%n");

        properties.setProperty("log4j.appender.MyFile", "org.apache.log4j.RollingFileAppender");
        properties.setProperty("log4j.appender.MyFile.File", "my_example.log");
        properties.setProperty("log4j.appender.MyFile.MaxFileSize", "100KB");
        properties.setProperty("log4j.appender.MyFile.MaxBackupIndex", "1");
        properties.setProperty("log4j.appender.MyFile.layout",  "org.apache.log4j.PatternLayout");
        properties.setProperty("log4j.appender.MyFile.layout.ConversionPattern","%d{yyyy/MM/dd HH:mm:ss.SSS} [%5p] %t (%F) - %m%n");

        PropertyConfigurator.configure(properties);

        Logger logger = Logger.getLogger("MyFile");
        collection = Collection.startFromSave(args);
        try {
            Selector selector = Selector.open();
            ServerSocketChannel serverSocket = ServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress("localhost", 54673));
            serverSocket.configureBlocking(false);
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);
            ByteBuffer buffer = ByteBuffer.allocate(8192);

            InputStreamReader fileInputStream = new InputStreamReader(System.in);
            BufferedReader bufferedReader = new BufferedReader(fileInputStream);

            Writer.writeln("Сервер запущен.");
            logger.info("Сервер запущен. " + serverSocket.getLocalAddress());

            while (true) {
                if (bufferedReader.ready())
                    if (bufferedReader.readLine().equals("exit"))
                        break;
                   // else {
                    //	if (bufferedReader.readLine().equals("save"))
                    	//	SaveManagement.saveToFile(collection);
                    	//else {if (bufferedReader.readLine().equals("save"))
                    	//	Writer.writeln("Чтобы сохранить коллекцию в файл - введите save /n"
                    		//		+ "Чтобы выйти - введите exit");
                    	else
                        Writer.writeln("Неизвестная комманда.");
                if (selector.selectNow() <= 0) {
                    Thread.sleep(1000);
                    continue;
                }

                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                while (iter.hasNext()) {

                    SelectionKey key = iter.next();
                    iter.remove();

                    if (!key.isValid()) {
                        continue;
                    }

                    if (key.isAcceptable()) {
                        register(selector, key);
                    } else if (key.isWritable()) {
                        answer(buffer, key);
                    } else if (key.isReadable()) {
                        read(buffer, key);
                    }
                }
            }
            serverSocket.close();
            logger.info("Сервер закрыт");
        } catch (IOException | ClassNotFoundException e) {
            Writer.writeln("Пренудительное закрытие сервера.");
            logger.error("Пренудительное закрытие сервера.");
            logger.error(e.getLocalizedMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        SaveManagement.saveToFile(collection);
        logger.info("Коллекция сохранена");
    }

    private static void answer(ByteBuffer buffer, SelectionKey key) throws IOException, ClassNotFoundException, EndOfFileException {
        SocketChannel client = (SocketChannel) key.channel();

        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(buffer.array()));
        CommandSimple command = (CommandSimple) objectInputStream.readObject();
        objectInputStream.close();
        buffer.clear();
        Writer w = CommandConvert.switcher(command, collection);

        if (number == 0) {
            Writer.writeln("Вызвана команада: " + command.getCurrent().toString());
            logger.info("Вызвана команада: " + command.toString());
            logger.info("Комманда обработана успешно. Ответ:" + (w.toString()));
        }
            w.shatter();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        Writer subW = w.getSubWriter((number)*10,(number + 1)*10);
        objectOutputStream.writeObject(subW);
        buffer = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
        client.write(buffer);
        buffer.clear();
        number++;
        objectOutputStream.flush();
        if(subW.isEnd()) {
            key.interestOps(SelectionKey.OP_READ);
            number = 0;
        }
    }

    private static void read(ByteBuffer buffer, SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();

        try {
            if (client.read(buffer) <= 0)
                throw new SocketException();
            else
                buffer.flip();
        } catch (SocketException e) {
            client.close();
            buffer.clear();
            Writer.writeln("Соединение разорвано...");
            Writer.writeln("Сервер продолжит работать. Попробуйте запустить другой клиент, чтобы восстановить соединение.");
            logger.info("Соединение разорвано. Сервер продолжил работать.");
            return;
        }

        key.interestOps(SelectionKey.OP_WRITE);
    }

    private static void register(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel serverSocket = (ServerSocketChannel) key.channel();
        SocketChannel client = serverSocket.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        Writer.writeln("Соединение уcтановлено: " + client.getLocalAddress());
        logger.info("Соединение уcтановлено: " + client.getLocalAddress());
    }
}