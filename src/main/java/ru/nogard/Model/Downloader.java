package ru.nogard.Model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.nogard.Controller.Controller;

import java.io.*;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class Downloader {

    private Controller controller;
    private int numPagesForDownload;
    private boolean dwPopular;
    private int countFile = 0;
    private boolean safe;

    public Downloader(Controller controller, boolean popular, int numPages, boolean safe) {
        this.controller = controller;
        this.dwPopular = popular;
        this.numPagesForDownload = numPages;
        this.safe = safe;

        startDownload();
    }

    private void startDownload() {

        Thread thread = new Thread(() -> {

                for (int i = 1; i <= numPagesForDownload; i++) {

                    if (!Thread.currentThread().isInterrupted()) {

                        controller.updateStatus("Начинаю скачивать страницу №" + i);

                        downloadImages(
                                new AnswerCreation(
                                        new RequestCreation(i, controller, dwPopular, safe).toString(),
                                        controller).toString()
                        );

                    }

                }
                controller.updateStatus("---------------------------------------------------\n");
        });
        thread.start();

        Thread thread4btn = new Thread(() -> {
            while (thread.isAlive()) {

                controller.lockDownloadButtons(true);

                if (controller.getBtnStopPlease().isPressed())
                    thread.interrupt();

            }
            controller.lockDownloadButtons(false);

        });
        thread4btn.start();
    }

    private void downloadImages(String json) {

        //Проверка, на случай, если на прошлом этапе что-то пошло не так и запрос пуст
        if (json.length() <= 10) {
            controller.updateStatus("Ошибка со строкой запроса - проблема с тегами?");
            Thread.currentThread().interrupt();
        } else {

            JSONParser parser = new JSONParser();

            Object obj = null;

            try {
                obj = parser.parse(json);
            } catch (ParseException e) {
                controller.updateStatus("Ошибка распарсивания файла JSON");
            }
            JSONObject jsonObj = (JSONObject) obj;

            JSONArray ja = (JSONArray) jsonObj.get("search");

            controller.updateStatus("\nВсего файлов: " + ja.size());

            for (Object obc : ja) {
                if (!Thread.currentThread().isInterrupted()) {
                    String link = ((JSONObject) obc).get("image").toString();
                    link = "https://" + link.substring(2);

                    downloadUsingNIO(link);
                }
            }
        }
    }

    private void downloadUsingNIO(String urlStr) {

        File filer = new File(urlStr);
        String filePath = Settings.DIR4SAVE + filer.getName();

        if (!new File(filePath).exists()) {

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                controller.updateStatus("Скачиваю файл #" + (++countFile));

                URL url = new URL(urlStr);
                ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                rbc.close();
            }
            catch (MalformedURLException e) {
                controller.updateStatus("Ошибка на этапе получения распарсенной ссылки на картинку");
            }
            catch (FileNotFoundException e) {
                controller.updateStatus("Ошибка на этапе скачивания картинки");
            }
            catch (IOException e) {
                controller.updateStatus("Иная ошибка на этапе скачивания картинки");
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
    }
}
