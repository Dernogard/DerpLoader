package ru.nogard.Controller;

import javafx.scene.layout.Pane;
import ru.nogard.Main;
import ru.nogard.Model.Downloader;
import ru.nogard.View.MainWindow;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ru.nogard.Model.Settings;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;


public class Controller {

    @FXML
    private TabPane tabs;
    @FXML
    private TextField ip_tf;
    @FXML
    private TextField port_tf;
    @FXML
    private TextField kye_tf;
    @FXML
    private TextField handlerTagShow_tf;
    @FXML
    private TextField handlerTagIgnore_tf;
    @FXML
    private TextField numPagesForDownload_tf;
    @FXML
    private ListView<String> choiceTagsForShowList;
    @FXML
    private ListView<String> choiceTagsForIgnoreList;
    @FXML
    private ListView<String> tagsForShowList;
    @FXML
    private ListView<String> tagsForIgnoreList;
    @FXML
    private TextArea textAreaLog;
    @FXML
    private CheckBox notSafeForWorkBox;
    @FXML
    private Button btnDownload;
    @FXML
    private Button btnDownloadPopular;
    @FXML
    private Button btnStopPlease;
    @FXML
    private Button btnOpenTagShowList;
    @FXML
    private Button btnOpenTagIgnoreList;
    @FXML
    private TextField chooseFolder_tf;
    @FXML
    private ImageView statusProxy;
    @FXML
    private TextField imgHeight_tf;
    @FXML
    private TextField imgWidth_tf;
    @FXML
    private CheckBox imageSizeBox;
    @FXML
    private CheckBox dontWantProxyBox;
    @FXML
    private Pane proxyBlock;
    @FXML
    private Button handRequestButton;
    @FXML
    private TextField handRequestField;

    private ObservableList<String> tagsForShow = FXCollections.observableArrayList();
    private ObservableList<String> tagsForIgnore = FXCollections.observableArrayList();

    private Image img_ok = new Image(Controller.class.getResourceAsStream("/proxy_ok.png"));
    private Image img_err = new Image(Controller.class.getResourceAsStream("/proxy_err.png"));

    /*-------------------------------------------------------------------------------------------------------------*/

    @FXML
    public void initialize() {

        //Задаем значения полей подключения на 1 вкладке "по умолчанию"
        ip_tf.setText(Settings.IP_PROXY);
        port_tf.setText(String.valueOf(Settings.PORT_PROXY));
        kye_tf.setText(Settings.KEY_BOORU);

        changeImageStatusProxy();

        chooseFolder_tf.setText(Settings.DIR4SAVE);

        //Говорим программе заполнить лист любимых тегов из файла
        choiceTagsForShowList.setItems(readTagsFromFile(Settings.PATH_TO_SHOW_TAGS_FILE));
        choiceTagsForIgnoreList.setItems(readTagsFromFile(Settings.PATH_TO_IGNORE_TAGS_FILE));

        //Связываем листы
        tagsForShowList.setItems(tagsForShow);
        tagsForIgnoreList.setItems(tagsForIgnore);

        //При двойном клике по строчке тега добавляем/удаляем его в/из соответствующий(его) лист(а)
        choiceTagsForShowList.setOnMouseClicked(event -> {
            if(event.getClickCount() > 1)
                addTagToList((ListView)event.getSource());
        });
        choiceTagsForIgnoreList.setOnMouseClicked(event -> {
            if(event.getClickCount() > 1)
                addTagToList((ListView)event.getSource());
        });

        tagsForShowList.setOnMouseClicked(event -> {
            if (event.getClickCount() > 1)
                removeTagFromList((ListView)event.getSource());
        });
        tagsForIgnoreList.setOnMouseClicked(event -> {
            if (event.getClickCount() > 1)
                removeTagFromList((ListView)event.getSource());
        });

        btnOpenTagShowList.setOnAction(this::hndlOpenFile);
        btnOpenTagIgnoreList.setOnAction(this::hndlOpenFile);
    }

    @FXML
    private void download(MouseEvent event) {
        updateStatus("Начинаю процесс загрузки");

        Settings.typeDownload type = checkTypeRequest(event);

        new Downloader(this, type,
                checkInteger(numPagesForDownload_tf.getText()), notSafeForWorkBox.isSelected(),
                checkInteger(imgWidth_tf.getText()), checkInteger(imgHeight_tf.getText()), imageSizeBox.isSelected());
    }

    @FXML
    private void handlerAddToShowList() {
        //Ручное добавление тега через поле ввода
        String tag = handlerTagShow_tf.getText();

        if(!tag.isEmpty() && !tagsForShow.contains(tag) && !tagsForIgnore.contains(tag))
            tagsForShow.add(tag);

        handlerTagShow_tf.clear();

    }

    @FXML
    private void handlerAddToIgnoreList() {
        //Ручное добавление тега через поле ввода
        String tag = handlerTagIgnore_tf.getText();

        if(!tag.isEmpty() && !tagsForIgnore.contains(tag) && !tagsForShow.contains(tag))
            tagsForIgnore.add(tag);

        handlerTagIgnore_tf.clear();
    }

    @FXML
    private void clearTextAreaLog () {
        Thread thread = new Thread(textAreaLog::clear);
        thread.start();
    }

    @FXML
    private void writeProperties() {
        Settings.saveProperties(this);
    }

    @FXML
    private void hndlChooseDir () {
        DirectoryChooser dirCh = new DirectoryChooser();
        dirCh.setTitle("Куда сохраняем картинки?");
        File dir = dirCh.showDialog(MainWindow.getStage());
        if (dir != null) {
            chooseFolder_tf.setText(Settings.DIR4SAVE = dir.toString() + File.separator);
        }
    }

    @FXML
    private void saveDir4Save () {
        Settings.saveProperties(this, chooseFolder_tf.getText());
    }

    @FXML
    private void changeSettings (KeyEvent event) {
        if (event.getSource().equals(ip_tf))
            Settings.saveConstant("ip_proxy", ip_tf.getText());
        else if (event.getSource().equals(port_tf))
            Settings.saveConstant("port_proxy", String.valueOf(checkInteger(port_tf.getText())));
        else if (event.getSource().equals(kye_tf))
            Settings.saveConstant("key_booru", kye_tf.getText());

    }

    @FXML
    private void changeImageStatusProxy () {
        statusProxy.setVisible(false);
        Thread thread = new Thread(() -> {
            statusProxy.setImage(Settings.checkProxy() ? img_ok : img_err);
            statusProxy.setVisible(true);
        });
        thread.start();
    }

    @FXML
    private void goToTheProxySite () {
        Main.getHS().showDocument("https://hidemy.name/ru/proxy-list/?type=hs#list");
    }

    @FXML
    private void hideProxyBlock () {
        proxyBlock.setVisible(!dontWantProxyBox.isSelected());
    }

    /*-------------------------------------------------------------------------------------------------------------*/

    private int checkInteger (String x) {
        try {
            return Integer.parseInt(x);
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private Settings.typeDownload checkTypeRequest (MouseEvent event) {
        if (event.getSource().equals(btnDownload)) {
            return Settings.typeDownload.TAGS_REQUEST;
        }
        else if (event.getSource().equals(handRequestButton)) {
            return Settings.typeDownload.HANDLER_REQUEST;
        }
        else {
            return Settings.typeDownload.POPULAR_REQUEST;
        }
    }

    private void addTagToList (ListView list) {

        String tag = list.getSelectionModel().getSelectedItem().toString();

        switch (list.getId()) {
            case "choiceTagsForShowList":
                if(!tagsForShow.contains(tag) && !tagsForIgnore.contains(tag))
                    tagsForShow.add(tag);
                break;
            case "choiceTagsForIgnoreList":
                if(!tagsForIgnore.contains(tag) && !tagsForShow.contains(tag))
                    tagsForIgnore.add(tag);
                break;
        }
    }

    private void removeTagFromList (ListView list) {
        String tag = list.getSelectionModel().getSelectedItem().toString();

        switch (list.getId()) {
            case "tagsForShowList":
                if(tagsForShow.contains(tag))
                    tagsForShow.remove(tag);
                break;
            case "tagsForIgnoreList":
                if(tagsForIgnore.contains(tag))
                    tagsForIgnore.remove(tag);
                break;
        }
    }

    private ObservableList<String> readTagsFromFile (String file) {
        ObservableList<String> list = FXCollections.observableArrayList();

        try {
            File tagsFile = new File(file);

            if (!tagsFile.exists())
                throw new Exception();
            else
                list.addAll(Files.readAllLines(tagsFile.toPath()));

        } catch (Exception e) {
            tabs.getSelectionModel().select(2);
            updateStatus("Ошибка с файлом предпочитаемых тегов.\n" +
                    "Проверьте наличие файла.\n" +
                    "Обратите внимание, что каждый тег должен начинаться с новой строки, а теги из нескольких слов должны разделяться знаком \"+\" ");
        }

        return list;
    }

    private void hndlOpenFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл с тегами");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text (.txt)", "*.txt"));
        File file = fileChooser.showOpenDialog(MainWindow.getStage());
        if (file != null) {
            String filePath = file.toPath().toString();

            if (event.getSource().equals(btnOpenTagShowList)) {
                choiceTagsForShowList.setItems(readTagsFromFile(filePath));
                choiceTagsForShowList.refresh();
                Settings.saveProperties(this, filePath, true);
            }
            else {
                choiceTagsForIgnoreList.setItems(readTagsFromFile(filePath));
                choiceTagsForIgnoreList.refresh();
                Settings.saveProperties(this, filePath, false);
            }
        }
    }

    /*-------------------------------------------------------------------------------------------------------------*/

    public ObservableList<String> getTagsForShow() {
        return tagsForShow;
    }

    public ObservableList<String> getTagsForIgnore() {
        return tagsForIgnore;
    }

    public Button getBtnStopPlease() {
        return btnStopPlease;
    }

    public boolean isDontWantProxyBox() {
        return dontWantProxyBox.isSelected();
    }

    public String getHandRequestField() {
        return handRequestField.getText();
    }
    /*-------------------------------------------------------------------------------------------------------------*/

    public void lockDownloadButtons (boolean lock) {
        btnDownload.setDisable(lock);
        btnDownloadPopular.setDisable(lock);
        handRequestButton.setDisable(lock);
    }

    public void updateStatus (String message) {
        if (Platform.isFxApplicationThread())
            textAreaLog.appendText("\n" + message);
        else
            Platform.runLater(() -> textAreaLog.appendText("\n" + message));
    }

}
