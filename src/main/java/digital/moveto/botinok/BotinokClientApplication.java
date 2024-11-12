package digital.moveto.botinok;

import digital.moveto.botinok.client.config.UIConst;
import digital.moveto.botinok.client.ui.UiElements;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

public class BotinokClientApplication extends Application {

	private ConfigurableApplicationContext applicationContext;

	public static String[] args;

	public static void main(String[] args) {
		BotinokClientApplication.args = args;
		Application.launch(BotinokClientApplication.class, args);
	}

	@Override
	public void init() {}

	@Override
	public void start(Stage stage) {
		UiElements.stage = stage;

		initLoadingUI();

		new Thread(() -> {
			applicationContext = new SpringApplicationBuilder(BotinokClientSpringApplication.class).run(args);
			applicationContext.publishEvent(new StageReadyEvent(stage));
		}, "Spring Thread").start();
	}

	private void initLoadingUI() {
		Image loadingAnimation = new Image(getClass().getResourceAsStream("/ui/images/loading.gif"));
		Image applicationIcon = new Image(getClass().getResourceAsStream("/ui/images/application-icon-256x256.png"));

		ImageView imageView = new ImageView();
		imageView.setImage(loadingAnimation);
		imageView.setX(0);
		imageView.setY(0);
		imageView.setFitWidth(UIConst.WIDTH_OF_SCENE);
		imageView.setPreserveRatio(true);
		Group root = new Group(imageView);
		Scene scene = new Scene(root, UIConst.WIDTH_OF_SCENE, UIConst.HEIGHT_OF_SCENE);
		UiElements.stage.setScene(scene);
		UiElements.stage.setTitle("BotInOk");
		UiElements.stage.getIcons().add(applicationIcon);
		UiElements.stage.setScene(scene);
		UiElements.stage.setResizable(false);
		UiElements.stage.show();
	}

	@Override
	public void stop() {
		SpringApplication.exit(SpringApplication.run(BotinokClientApplication.class, args));
		Platform.exit();
		System.exit(0);
	}

	private class StageReadyEvent extends ApplicationEvent {
		public StageReadyEvent(Stage stage) {
			super(stage);
		}
	}
}
