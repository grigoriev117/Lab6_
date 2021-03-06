package Client;
import java.nio.channels.Pipe;
import spacemarine.*;
import spacemarine.Writer;
import Exceptions.*;
import command.*;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Client {

    private static boolean exit = false;

    public static void main(String[] args) throws IOException {
        try {
            do {
                InetSocketAddress addr = new InetSocketAddress(InetAddress.getByName("localhost"), 54673);
                Selector selector = Selector.open();
                SocketChannel sc = SocketChannel.open();
                sc.configureBlocking(false);
                sc.connect(addr);
                sc.register(selector, SelectionKey.OP_CONNECT);
                try {
                    while (true) {
                        if (selector.select() > 0) {
                            Boolean doneStatus = process(selector);
                            if (doneStatus) {
                                break;
                            }
                        }
                    }
                } catch (IOException e) {
                    Channel.channel("\u001B[31m" + "Не удалось получить или прочитать ответ от сервера." + "\u001B[0m");
                    Channel.channel("\u001B[31m" + "Соединение разорвано." + "\u001B[0m");
                }
                sc.close();
            } while (!exit && ConsoleClient.handlerB("Попробовать переподключить клиент? boolean: ", CommandConvertClient.boolCheck));
        } catch (IOException e) {
        	Channel.channel("\u001B[31m" + "Неправильное закрытие сокета." + "\u001B[0m");
        } catch (ClassNotFoundException e) {
        	Channel.channel("\u001B[31m" + "Отсутствует класс для сериализации." + "\u001B[0m");
            exit = true;
        } catch (EndOfFileException e) {
        	Channel.channel("\u001B[31m" + "Неожиданное завершение работы консоли" + "\u001B[0m");//ctrl-d
            exit = true;
        }
        Channel.channel("Клиент был закрыт...");
    }

    public static Boolean process(Selector selector) throws IOException, EndOfFileException, ClassNotFoundException {
        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

        while (iterator.hasNext()) {
            SelectionKey key = iterator.next();
            iterator.remove();

            if (key.isConnectable()) {
                Boolean connected = processConnect(selector, key);
                if (!connected) {
                    return true;
                }
            }
            if (key.isReadable()) {
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer bb = ByteBuffer.allocate(8*1024);
                bb.clear();
                sc.read(bb);

                ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bb.array()));
                Writer w = (Writer) objectInputStream.readObject();
                if (w != null) {
                    w.writeAll();
                    if (w.isEnd())
                        key.interestOps(SelectionKey.OP_WRITE);
                } else {
                	Channel.channel("Отсутствуют данные вывода");
                    key.interestOps(SelectionKey.OP_WRITE);
                }
            }
            if (key.isWritable()) {

            	Channel.channel("\u001B[33m" + "Ожидание ввода команды: " + "\u001B[0m");
                String[] com = CommandReader.splitter(ConsoleClient.console.read());
                CommandSimple command = CommandConvertClient.switcher(com[0], com[1]);

                if (command == null)
                    return false;
                else if (command.getCurrent() == CommandsList.EXIT) {
                    exit = true;
                    return true;
                }

                SocketChannel sc = (SocketChannel) key.channel();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(command);
                objectOutputStream.flush();
                //конец
                ByteBuffer bb = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
                if (bb.array().length > 8192)
                {
                	Channel.channel("Отправляемые дынные слишком большие (" + bb.array().length + " > 8192). ");
                    return false;
                }
                sc.write(bb);
                bb.clear();

                key.interestOps(SelectionKey.OP_READ);
            }
        }
        return false;
    }

    public static Boolean processConnect(Selector selector, SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        try {
            while (sc.isConnectionPending()) {
                sc.finishConnect();
            }
            sc.configureBlocking(false);
            sc.register(selector, SelectionKey.OP_WRITE);
            Channel.channel("Соединение установлено: " + sc.getLocalAddress());

        } catch (IOException e) {
            key.cancel();
            Channel.channel("Сервер недоступен. Попробуйте переподключиться позже.");
            return false;
        }
        return true;
    }
}
