package digital.moveto.botinok.client.ui;

import digital.moveto.botinok.client.config.UIConst;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class TutorialScene {
    @Getter
    @Setter
    private Boolean showTutorial = false;

    @Autowired
    private MainScene mainScene;

    private int currentStep = 0;

    private Scene scene;
    Image tutorialImage;
    ImageView imageView;
    public void init() {

        Platform.runLater(
                () -> {
                    imageView = new ImageView();
                    imageView.setImage(tutorialImage);
                    imageView.setX(0);
                    imageView.setY(0);
                    imageView.setFitWidth(UIConst.WIDTH_OF_SCENE);
                    imageView.setPreserveRatio(true);
                    imageView.setOnMouseClicked(event -> nextStep());

                    nextStep();

                    Group root = new Group(imageView);
                    scene = new Scene(root, UIConst.WIDTH_OF_SCENE, UIConst.HEIGHT_OF_SCENE);
                    UiElements.stage.setScene(scene);
                });
    }

    private void nextStep() {
        currentStep++;
        InputStream resourceAsStream = getClass().getResourceAsStream("/ui/images/tutorial/" + currentStep + ".png");

        if (resourceAsStream == null) {
            showTutorial = false;
            mainScene.finishInitialization();
            return;
        }

        tutorialImage = new Image(resourceAsStream);
        imageView.setImage(tutorialImage);
    }

}
