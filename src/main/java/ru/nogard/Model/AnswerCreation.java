package ru.nogard.Model;

import ru.nogard.Controller.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

class AnswerCreation {

    private String answer;

    AnswerCreation(String request, Controller controller) {
        this.answer = createStringFromJSON (request, controller);
    }

    private String createStringFromJSON (String request, Controller controller) {
        String list = "";

        if (!Settings.checkProxy()) {

            controller.updateStatus("Ошибка прокси");
            Thread.currentThread().interrupt();

        } else {

            try {

                Proxy proxy = new Proxy(Settings.TYPE_PROXY, new InetSocketAddress(Settings.IP_PROXY, Settings.PORT_PROXY));
                URL urlJSON = new URL(request);
                //URL urlJSON = new URL("http://google.ru");

                System.out.println("1");

                BufferedReader bis = new BufferedReader(new InputStreamReader(urlJSON.openConnection(proxy).getInputStream()));

                System.out.println("2");
                list = bis.readLine();
                System.out.println("3");

            } catch (MalformedURLException e) {
                controller.updateStatus("Ошибка создания URL");
            } catch (IOException e) {
                controller.updateStatus("Ошибка на этапе получения списка картинок. Есть смысл сменить прокси, даже если он рабочий");
            }
        }

        return list;
    }

    @Override
    public String toString() {
        return answer;
    }
}
