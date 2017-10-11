package ru.nogard.Model;

import ru.nogard.Controller.Controller;

import java.util.ArrayList;

class RequestCreation {

    private String request;

    RequestCreation(int numPage, Controller controller, boolean dwPopular, boolean safe) {
        this.request = createRequest(numPage, controller, dwPopular, safe);
    }

    private String createRequest(int numPage, Controller controller, boolean dwPopular, boolean safe) {

        StringBuilder sb = new StringBuilder();

        sb.append("https://derpibooru.org/search.json?").append("key=").append(Settings.KEY_BOORU); //Ключ авторизации
        sb.append("&page=").append(numPage);
        sb.append("&q=");   //Стартуем теги

        if (!dwPopular && !controller.getTagsForShow().isEmpty()) {

            ArrayList<String> tagsShowList = new ArrayList<>(controller.getTagsForShow());
            ArrayList<String> tagsIgnoreList = new ArrayList<>(controller.getTagsForIgnore());

            sb.append(String.join("+AND+", tagsShowList));

            for (String tag : tagsIgnoreList)
                sb.append("-").append(tag).append("+AND+");

            if(safe)
                sb.append("+AND+safe");

        } else {
            sb.append("first_seen_at.gt%3A3+days+ago");
            if(safe)
                sb.append("+AND+safe");
            sb.append("&sd=desc&sf=score");
        }

        controller.updateStatus("Запрос к сайту успешно сгенерирован");
        controller.updateStatus(sb.toString());

        return sb.toString();

    }

    @Override
    public String toString() {
        return request;
    }
}
