package ru.nogard.Model;

import ru.nogard.Controller.Controller;

import java.util.ArrayList;

class RequestCreation {

    private String request;
    private Controller controller;
    private StringBuilder sb;
    private boolean safe;


    RequestCreation(int numPage, Controller controller, Settings.typeDownload dwPopular, boolean safe) {
        this.controller = controller;
        this.safe = safe;
        this.request = createRequest(numPage, dwPopular);
    }

    private String createRequest(int numPage, Settings.typeDownload dwPopular) {
        sb = new StringBuilder();

        sb.append("https://derpibooru.org/search.json?").append("key=").append(Settings.KEY_BOORU); //Ключ авторизации
        sb.append("&page=").append(numPage);
        sb.append("&q=");   //Стартуем теги

        switch (dwPopular) {
            case TAGS_REQUEST:
                ifUsual();
                break;
            case HANDLER_REQUEST:
                ifHandRequest();
                break;
            case POPULAR_REQUEST:
            default:
                ifPopular();
                break;
        }

        controller.updateStatus("Запрос к сайту успешно сгенерирован");
        controller.updateStatus(sb.toString());

        return sb.toString();

    }

    private void ifPopular () {
        sb.append("first_seen_at.gt%3A3+days+ago");
        ifSave();
        sb.append("&sd=desc&sf=score");
    }

    private void ifUsual () {
        ArrayList<String> tagsShowList = new ArrayList<>(controller.getTagsForShow());
        ArrayList<String> tagsIgnoreList = new ArrayList<>(controller.getTagsForIgnore());

        sb.append(String.join("+AND+", tagsShowList));
        if (tagsShowList.size()!=0 && tagsIgnoreList.size() !=0) sb.append("+AND+-");
        sb.append(String.join("+AND+-", tagsIgnoreList));

        ifSave();
    }

    private void ifHandRequest () {
       sb.append(controller.getHandRequestField().trim().replace(" ", "+"));
       ifSave();
    }

    private void ifSave() {
        if (safe) {
            sb.append("+AND+safe");
        }
    }


    @Override
    public String toString() {
        return request;
    }
}
