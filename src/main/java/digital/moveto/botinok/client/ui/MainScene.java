package digital.moveto.botinok.client.ui;

import digital.moveto.botinok.client.config.ClientConst;
import digital.moveto.botinok.client.config.UIConst;
import jakarta.annotation.PostConstruct;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MainScene {

    @Autowired
    private UiElements uiElements;

    private Scene scene = null;

    @PostConstruct
    public void init() {

        HBox hBox = new HBox();
        hBox.setPrefSize(UIConst.WIDTH_OF_SCENE, UIConst.HEIGHT_OF_SCENE);

        Pane settingPane = initSettingPane();
        Pane mainPane = initMainPane();

        hBox.getChildren().addAll(settingPane, mainPane);

        Group root = new Group(hBox);
        scene = new Scene(root, UIConst.WIDTH_OF_SCENE, UIConst.HEIGHT_OF_SCENE);
    }

    public void finishInitialization() {
        Platform.runLater(
                () -> {
                    UiElements.stage.setScene(scene);
                }
        );
    }

    private Pane initSettingPane() {

        VBox result = new VBox();
        result.setPrefSize(UIConst.WIDTH_OF_SETTING, UIConst.HEIGHT_OF_SCENE);

        VBox setting = new VBox();
        setting.setPrefSize(UIConst.WIDTH_OF_SETTING, UIConst.HEIGHT_OF_SCENE - UIConst.HEIGHT_OF_VERSION);
        setting.setBorder(UIConst.BORDER_DEFAULT_SMALL);
        setting.setStyle(UIConst.STYLE_OF_BACKGROUND);
        setting.setCenterShape(true);


        Label settingLabel = new Label("Setting");
        settingLabel.setPadding(new Insets(10, 10, 10, 10));
        settingLabel.setFont(Font.font("Dialog", FontWeight.BOLD, 25));

        BorderPane settingLabelPane = new BorderPane();
        settingLabelPane.setCenter(settingLabel);
        settingLabelPane.setPrefSize(UIConst.WIDTH_OF_SETTING, UIConst.HEIGHT_OF_LABEL);

        BorderPane userNameLabelPane = new BorderPane();
        userNameLabelPane.setCenter(uiElements.getUserNameLabel());
        userNameLabelPane.setPrefSize(UIConst.WIDTH_OF_SETTING, UIConst.HEIGHT_OF_LABEL);

        Label positionLabel = new Label("Positions:");
        positionLabel.setTextAlignment(TextAlignment.CENTER);
        positionLabel.setPadding(new Insets(10, 10, 0, 10));

        Label locationLabel = new Label("Location:");
        locationLabel.setTextAlignment(TextAlignment.CENTER);
        locationLabel.setPadding(new Insets(10, 10, 0, 10));

        Label countDailyApply = new Label("Count apply per day:");
        countDailyApply.setTextAlignment(TextAlignment.CENTER);
        countDailyApply.setPadding(new Insets(10, 10, 0, 10));

        Label countDailyConnect = new Label("Count connect per day:");
        countDailyConnect.setTextAlignment(TextAlignment.CENTER);
        countDailyConnect.setPadding(new Insets(10, 10, 0, 10));

        setting.getChildren().addAll(
                settingLabelPane,
                userNameLabelPane,
//                uiElements.getWorkInShabatCheckBox(),
                uiElements.getActiveSearch(),
                uiElements.getRemoteWork(),
                locationLabel,
                uiElements.getLocations(),
                positionLabel,
                uiElements.getPositionsField(),
                countDailyApply,
                uiElements.getCountDailyApplySlider(),
                countDailyConnect,
                uiElements.getCountDailyConnectSlider(),
                uiElements.getStartButton(),
                uiElements.getScrollAccountPane());


        VBox globalSetting = new VBox();

        Label versionLabel = new Label("Version: " + ClientConst.VERSION);
        versionLabel.setTextAlignment(TextAlignment.CENTER);
        versionLabel.setPrefSize(UIConst.WIDTH_OF_SETTING, UIConst.HEIGHT_OF_VERSION);
        versionLabel.setPadding(new Insets(10, 10, 10, 10));
        versionLabel.setFont(Font.font("Dialog", FontWeight.BOLD, 10));

        globalSetting.getChildren().addAll(uiElements.getWorkOrPause(), uiElements.getStartEvery24Hours(), versionLabel);
        result.getChildren().addAll(setting, globalSetting);

        return result;
    }

    private Pane initMainPane() {
        VBox main = new VBox();

        Pane loggingPane = initLoggingPane();
        Pane statisticPane = initStatisticPane();

        main.getChildren().addAll(loggingPane, statisticPane);
        return main;
    }

    private Pane initLoggingPane() {
        Pane main = new Pane();

        main.setPrefSize(UIConst.WIDTH_OF_MAIN_PANE, UIConst.HEIGHT_OF_LOGGING_PANE);
        main.setBorder(UIConst.BORDER_DEFAULT_SMALL);
        main.setStyle(UIConst.STYLE_OF_BACKGROUND);

        main.getChildren().add(uiElements.getScrollLogPane());
        return main;
    }

    private Pane initStatisticPane() {
        VBox statisticVBox = new VBox();
        statisticVBox.setPrefSize(UIConst.WIDTH_OF_MAIN_PANE, UIConst.HEIGHT_OF_STATISTIC);
        statisticVBox.setBorder(UIConst.BORDER_DEFAULT_SMALL);
        statisticVBox.setStyle(UIConst.STYLE_OF_BACKGROUND);

        Label statisticLabel = new Label("Statistic");
        statisticLabel.setPadding(new Insets(10, 10, 5, 10));
        statisticLabel.setFont(Font.font("Dialog", FontWeight.BOLD, 25));

        BorderPane statisticLabelPane = new BorderPane();
        statisticLabelPane.setCenter(statisticLabel);
        statisticLabelPane.setPrefSize(UIConst.WIDTH_OF_MAIN_PANE, UIConst.HEIGHT_OF_LABEL);

        VBox todayVBox = generateStatisticPane("TODAY", uiElements.getStatisticApplyTodayLabel(), uiElements.getStatisticConnectTodayLabel());
        VBox totalVBox = generateStatisticPane("TOTAL", uiElements.getStatisticApplyTotalLabel(), uiElements.getStatisticConnectTotalLabel());

        HBox statisticHBox = new HBox(todayVBox, totalVBox);
        statisticVBox.getChildren().addAll(statisticLabelPane, statisticHBox);

        return statisticVBox;
    }

    private VBox generateStatisticPane(String name, Label applyLabel, Label connectLabel) {
        VBox totalVBox = new VBox();

        Label totalTextLabel = new Label(name);
        totalTextLabel.setPadding(new Insets(5, 10, 10, 10));
        totalTextLabel.setFont(Font.font("Dialog", FontWeight.BOLD, 18));
        BorderPane totalTextLabelPane = new BorderPane();
        totalTextLabelPane.setCenter(totalTextLabel);
        totalTextLabelPane.setPrefSize(UIConst.WIDTH_OF_MAIN_PANE / 2, UIConst.HEIGHT_OF_LABEL);

        HBox totalApplyPane = new HBox();
        totalApplyPane.setPadding(new Insets(5, 50, 5, 100));
        Label applytotalTextLabel = new Label("Applied: ");
        applytotalTextLabel.setFont(Font.font("Dialog", FontWeight.NORMAL, 16));
        applyLabel.setFont(Font.font("Dialog", FontWeight.NORMAL, 16));
        totalApplyPane.getChildren().addAll(applytotalTextLabel, applyLabel);

        HBox totalConnectPane = new HBox();
        totalConnectPane.setPadding(new Insets(5, 50, 5, 100));
        Label connecttotalTextLabel = new Label("Connect: ");
        connecttotalTextLabel.setFont(Font.font("Dialog", FontWeight.NORMAL, 16));
        connectLabel.setFont(Font.font("Dialog", FontWeight.NORMAL, 16));
        totalConnectPane.getChildren().addAll(connecttotalTextLabel, connectLabel);

        totalVBox.getChildren().addAll(totalTextLabelPane, totalApplyPane, totalConnectPane);
        return totalVBox;
    }

}
