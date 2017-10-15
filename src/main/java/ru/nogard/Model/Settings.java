package ru.nogard.Model;


import ru.nogard.Controller.Controller;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.file.Path;
import java.util.Properties;

public class Settings {


    private static Properties properties = new Properties();

    private static String pathToProperties = String.valueOf(System.getProperty("user.dir") + File.separator + "config.properties");


    public static String IP_PROXY;
    public static int PORT_PROXY;
    public static String KEY_BOORU;
           static Proxy.Type TYPE_PROXY;
    public static String PATH_TO_SHOW_TAGS_FILE;
    public static String PATH_TO_IGNORE_TAGS_FILE;
    public static String DIR4SAVE;

    public static void loadProperties() {

        try (FileInputStream fis =  new FileInputStream(pathToProperties)) {

            properties.load(fis);

            IP_PROXY = properties.getProperty("proxy_ip");
            PORT_PROXY = checkPort(properties.getProperty("proxy_port"));
            KEY_BOORU = properties.getProperty("booru_key");
            TYPE_PROXY = Proxy.Type.HTTP;
            PATH_TO_SHOW_TAGS_FILE = properties.getProperty("show_tags");
            PATH_TO_IGNORE_TAGS_FILE = properties.getProperty("ignore_tags");
            DIR4SAVE = properties.getProperty("dir4save");

        } catch (FileNotFoundException e) {
            System.out.println("Не найден файл настроек");
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла настроек");
        }

    }

    public static void saveProperties(Controller controller) {

        try (OutputStream fos = new FileOutputStream(pathToProperties)) {

            properties.setProperty("proxy_ip", IP_PROXY);
            properties.setProperty("proxy_port", String.valueOf(PORT_PROXY));
            properties.setProperty("booru_key", KEY_BOORU);

            properties.store(fos, "");

        } catch (FileNotFoundException e){
            controller.updateStatus("Не найден файл настроек");
        } catch (IOException e) {
            controller.updateStatus("Ошибка чтения файла настроек");
        }

    }

    public static void saveProperties (String filePath, boolean isShow) {

        try (FileOutputStream fos = new FileOutputStream(pathToProperties)) {

            properties.setProperty(isShow ? "show_tags" : "ignore_tags", filePath);
            properties.store(fos, "");

        } catch (FileNotFoundException e){
            System.out.println("Не найден файл настроек");
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла настроек");
        }
    }

    public static void saveProperties (String dirPath) {
        try (FileOutputStream fos = new FileOutputStream(pathToProperties)){

            properties.setProperty("dir4save", dirPath);
            properties.store(fos, "");

        } catch (FileNotFoundException e){
            System.out.println("Не найден файл настроек");
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла настроек");
        }
    }

    private static int checkPort(String port) {
        try {
         return Integer.parseInt(port);
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    public static void saveConstant (String name, String value) {
        switch (name) {
            case "ip_proxy":
                IP_PROXY = value;
                break;
            case "port_proxy":
                PORT_PROXY = checkPort(value);
                break;
            case "key_booru":
                KEY_BOORU = value;
                break;
            case "path_to_show":
                PATH_TO_SHOW_TAGS_FILE = value;
                break;
            case "path_to_ignore":
                PATH_TO_IGNORE_TAGS_FILE = value;
                break;
            case "dir4save":
                DIR4SAVE = value;
                break;
        }
    }

    public static boolean checkProxy () {
        Proxy proxy = new Proxy(TYPE_PROXY, new InetSocketAddress(IP_PROXY, PORT_PROXY));
        try {
            URL url = new URL("http://google.ru");
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection(proxy);
            urlConn.setConnectTimeout(7000);
            urlConn.connect();
            return (urlConn.getResponseCode() == 200);
        } catch (IOException e) {
            return false;
        }
    }
}