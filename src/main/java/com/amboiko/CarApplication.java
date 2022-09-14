package com.amboiko;

import com.amboiko.common.Logger;
import com.amboiko.model.Brand;
import com.amboiko.model.Car;
import com.amboiko.model.Pilot;
import com.amboiko.model.RacingCar;
import com.amboiko.services.*;
import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class CarApplication extends Application {
    private CarManager carManager;
    private PilotManager pilotManager;
    private static LoadBrandService loadBrandService;
    private static LoadModelService loadModelService;
    private static LoadPilotService loadPilotService;
    private static CountDownLatch latch;
    private ProgressIndicator brandProgressIndicator;
    private ProgressIndicator modelProgressIndicator;
    private ProgressIndicator pilotProgressIndicator;
    private Label loadErrorLabel;
    private FlowPane brandFlow;
    private FlowPane modelFlow;
    private FlowPane pilotFlow;
    private Button loadBrandsButton;
    private Pane centerPane;
    private Path redPath;
    private Path bluePath;
    private boolean isRedSelected = Boolean.FALSE;
    private boolean isGameStarted = Boolean.FALSE;
    private Car selectedCar;
    private Pilot redPilot;
    private Pilot bluePilot;
    private Button startButton;

    public void runApplication() {
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        final URL imageResource = Main.class.getClassLoader().getResource("icon.png");
        final java.awt.Image image = defaultToolkit.getImage(imageResource);

        /// This is new since JDK 9
        final Taskbar taskbar = Taskbar.getTaskbar();

        try {
            //set icon for mac os (and other systems which do support this method)
            taskbar.setIconImage(image);
        } catch (final UnsupportedOperationException e) {
            Logger.error("The os does not support: 'taskbar.setIconImage'");
        } catch (final SecurityException e) {
            Logger.error("There was a security exception for: 'taskbar.setIconImage'");
        }

        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        final BorderPane border = new BorderPane();

        HBox header = addHeader();
        border.setTop(header);
        addLoaderAndErrorMessage(header);

        border.setLeft(setupBrandFlow());

        border.setRight(setupModelFlow());

        centerPane = addCenterPane();
        border.setCenter(centerPane);

        border.setBottom(setupPilotFlow());

        final Scene scene = new Scene(border);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Hot wheels");

        primaryStage.setResizable(Boolean.FALSE);
        primaryStage.centerOnScreen();

        setUpLoadBrandsUI();
        setUpLoadModelsUI();
        setUpLoadPilotsUI();
        primaryStage.show();
    }

    public final void initEntry() {
        Logger.info("Init all services for app");
        loadBrandService = new LoadBrandService(carManager);
        loadModelService = new LoadModelService(carManager);
        loadPilotService = new LoadPilotService(pilotManager);
        Logger.info("Init latch for the app");
        latch = new CountDownLatch(3);
    }

    public final void setCarManager(CarManager carManager) {
        this.carManager = carManager;
    }

    public void setPilotManager(PilotManager pilotManager) {
        this.pilotManager = pilotManager;
    }

    public void setUpLoadBrandsUI() {
        brandProgressIndicator.visibleProperty().bind(loadBrandService.runningProperty());
        loadBrandService.setOnSucceeded(workerStateEvent -> {
            List<Brand> result = loadBrandService.getValue();
            fillBrandFlow(result);
            loadBrandsButton.setDisable(Boolean.TRUE);
        });
        loadBrandService.setOnFailed(workerStateEvent -> loadErrorLabel.setText("ERROR BRAND LOAD"));
    }

    public void setUpLoadModelsUI() {
        modelProgressIndicator.visibleProperty().bind(loadModelService.runningProperty());
        loadModelService.setOnSucceeded(workerStateEvent -> {
            List<Car> result = loadModelService.getValue();
            fillModelFlow(result);
        });
        loadModelService.setOnFailed(workerStateEvent -> loadErrorLabel.setText("ERROR MODEL LOAD"));
    }

    public void setUpLoadPilotsUI() {
        pilotProgressIndicator.visibleProperty().bind(loadPilotService.runningProperty());
        loadPilotService.setOnSucceeded(workerStateEvent -> {
            List<Pilot> result = loadPilotService.getValue();
            fillPilotFlow(result);
        });
        loadPilotService.setOnFailed(workerStateEvent -> loadErrorLabel.setText("ERROR PILOT LOAD"));
    }

    private HBox addHeader() {
        final HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 15, 15, 15));
        hbox.setSpacing(10);   // Gap between nodes
        hbox.setStyle("-fx-background-color: steelblue;");

        loadBrandsButton = new Button("LOAD BRANDS");
        loadBrandsButton.setFocusTraversable(Boolean.FALSE);
        loadBrandsButton.setPrefSize(120, 60);
        loadBrandsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                brandFlow.getChildren().clear();
                loadErrorLabel.setText(null);
                loadBrandService.restart();
            }
        });
        Label faq = new Label();
        faq.setFont(Font.font("Verdana", FontWeight.MEDIUM, 14));
        faq.setTextFill(Color.WHITE);
        faq.setText("Press 'LOAD BRANDS'. Then choose brand and select a specific model\nAfter then choose a pilot for car.\nWhen you've selected a 2 cars you can start a race.");

        hbox.getChildren().addAll(loadBrandsButton);
        hbox.getChildren().addAll(faq);

        return hbox;
    }

    private void addLoaderAndErrorMessage(HBox hb) {
        final StackPane stack = new StackPane();
        loadErrorLabel = new Label();
        brandProgressIndicator = getStyledProgressIndicator();
        modelProgressIndicator = getStyledProgressIndicator();
        pilotProgressIndicator = getStyledProgressIndicator();

        loadErrorLabel.setFont(Font.font("Verdana", FontWeight.SEMI_BOLD, 18));
        loadErrorLabel.setTextFill(Color.WHITE);

        stack.getChildren().addAll(brandProgressIndicator, modelProgressIndicator, pilotProgressIndicator, loadErrorLabel);

        stack.setAlignment(Pos.CENTER_RIGHT);
        hb.getChildren().add(stack);
        HBox.setHgrow(stack, Priority.ALWAYS);
    }

    private ProgressIndicator getStyledProgressIndicator() {
        final ProgressIndicator pi = new ProgressIndicator();
        pi.setStyle(" -fx-progress-color: white;");
        pi.setMaxWidth(30);
        pi.setMaxHeight(30);
        return pi;
    }

    private Pane addCenterPane() {
        final Pane centerPane = new Pane();
        centerPane.setPrefSize(500, 316);
        centerPane.setMaxSize(500, 316);

        final Path outerPath = getOuterPath();
        final Path centerPath = getCenterPath();
        final Path innerPath = getInnerPath();
        redPath = getRedPath();
        bluePath = getBluePath();
        setupStartButton();

        centerPane.getChildren().addAll(
                outerPath,
                centerPath,
                innerPath,
                startButton
        );
        return centerPane;
    }

    private void setupStartButton() {
        startButton = new Button("START");
        startButton.setDisable(Boolean.TRUE);
        startButton.setLayoutX(430);
        startButton.setLayoutY(280);
        startButton.setFocusTraversable(Boolean.FALSE);
        startButton.setOnAction(actionEvent -> latch.countDown());
    }

    private Path getInnerPath() {
        final Path innerPath = new Path();
        innerPath.setStrokeWidth(1);
        innerPath.setStroke(Color.ORANGE);
        innerPath.setFill(Color.WHITE);

        final int innerStartY = 80;
        innerPath.getElements().add(new MoveTo(120, innerStartY));
        innerPath.getElements().add(new LineTo(380, innerStartY));

        final ArcTo innerPathArc1 = new ArcTo();
        innerPathArc1.setX(380);
        innerPathArc1.setY(innerStartY + 100);
        innerPathArc1.setRadiusX(25);
        innerPathArc1.setRadiusY(25);
        innerPathArc1.setSweepFlag(Boolean.TRUE);

        innerPath.getElements().add(innerPathArc1);
        innerPath.getElements().add(new LineTo(120, innerStartY + 100));

        final ArcTo innerPathArc2 = new ArcTo();
        innerPathArc2.setX(120);
        innerPathArc2.setY(innerStartY);
        innerPathArc2.setRadiusX(50);
        innerPathArc2.setRadiusY(50);
        innerPathArc2.setSweepFlag(Boolean.TRUE);
        innerPath.getElements().add(innerPathArc2);

        return innerPath;
    }

    private Path getCenterPath() {
        final Path centerPath = new Path();
        centerPath.setStrokeWidth(2);
        centerPath.setStroke(Color.WHITE);
        centerPath.getStrokeDashArray().addAll(25d, 10d);

        final int innerStartY = 60;
        centerPath.getElements().add(new MoveTo(120, innerStartY));
        centerPath.getElements().add(new LineTo(380, innerStartY));
        final ArcTo innerPathArc1 = new ArcTo();
        innerPathArc1.setX(380);
        innerPathArc1.setY(innerStartY + 140);
        innerPathArc1.setRadiusX(15);
        innerPathArc1.setRadiusY(15);
        innerPathArc1.setSweepFlag(Boolean.TRUE);

        centerPath.getElements().add(innerPathArc1);
        centerPath.getElements().add(new LineTo(120, innerStartY + 140));

        final ArcTo innerPathArc2 = new ArcTo();
        innerPathArc2.setX(120);
        innerPathArc2.setY(innerStartY);
        innerPathArc2.setRadiusX(15);
        innerPathArc2.setRadiusY(15);
        innerPathArc2.setSweepFlag(Boolean.TRUE);
        centerPath.getElements().add(innerPathArc2);

        return centerPath;
    }

    private Path getRedPath() {
        final Path centerPath = new Path();
        centerPath.setStrokeWidth(1);
        centerPath.setStroke(Color.RED);

        final int innerStartY = 60;
        centerPath.getElements().add(new MoveTo(120, innerStartY - 10));
        centerPath.getElements().add(new LineTo(380, innerStartY - 10));
        final ArcTo innerPathArc1 = new ArcTo();
        innerPathArc1.setX(380);
        innerPathArc1.setY(innerStartY + 140 + 10);
        innerPathArc1.setRadiusX(15);
        innerPathArc1.setRadiusY(15);
        innerPathArc1.setSweepFlag(Boolean.TRUE);

        centerPath.getElements().add(innerPathArc1);
        centerPath.getElements().add(new LineTo(120, innerStartY + 140 + 10));

        final ArcTo innerPathArc2 = new ArcTo();
        innerPathArc2.setX(120);
        innerPathArc2.setY(innerStartY - 10);
        innerPathArc2.setRadiusX(15);
        innerPathArc2.setRadiusY(15);
        innerPathArc2.setSweepFlag(Boolean.TRUE);
        centerPath.getElements().add(innerPathArc2);

        return centerPath;
    }

    private Path getBluePath() {
        final Path centerPath = new Path();
        centerPath.setStrokeWidth(1);
        centerPath.setStroke(Color.BLUE);

        final int innerStartY = 60;
        centerPath.getElements().add(new MoveTo(120, innerStartY + 10));
        centerPath.getElements().add(new LineTo(380, innerStartY + 10));
        final ArcTo innerPathArc1 = new ArcTo();
        innerPathArc1.setX(380);
        innerPathArc1.setY(innerStartY + 140 - 10);
        innerPathArc1.setRadiusX(15);
        innerPathArc1.setRadiusY(15);
        innerPathArc1.setSweepFlag(Boolean.TRUE);

        centerPath.getElements().add(innerPathArc1);
        centerPath.getElements().add(new LineTo(120, innerStartY + 140 - 10));

        final ArcTo innerPathArc2 = new ArcTo();
        innerPathArc2.setX(120);
        innerPathArc2.setY(innerStartY + 10);
        innerPathArc2.setRadiusX(15);
        innerPathArc2.setRadiusY(15);
        innerPathArc2.setSweepFlag(Boolean.TRUE);
        centerPath.getElements().add(innerPathArc2);

        return centerPath;
    }

    private Path getOuterPath() {
        final Path path = new Path();
        path.setStrokeWidth(1);
        path.setStroke(Color.ORANGE);
        path.setFill(Color.GREY);

        final int startY = 40;
        path.getElements().add(new MoveTo(120, startY));
        path.getElements().add(new LineTo(380, startY));
        final ArcTo arc1 = new ArcTo();
        arc1.setX(380);
        arc1.setY(startY + 180);
        arc1.setRadiusX(45);
        arc1.setRadiusY(45);
        arc1.setSweepFlag(Boolean.TRUE);
        path.getElements().add(arc1);
        path.getElements().add(new LineTo(120, startY + 180));

        final ArcTo arc2 = new ArcTo();
        arc2.setX(120);
        arc2.setY(startY);
        arc2.setRadiusX(45);
        arc2.setRadiusY(45);
        arc2.setSweepFlag(Boolean.TRUE);
        path.getElements().add(arc2);
        return path;
    }

    private FlowPane setupFlowPane() {
        final FlowPane fp = new FlowPane();
        fp.setPadding(new Insets(4, 0, 4, 4));
        fp.setVgap(4);
        fp.setHgap(4);
        fp.setPrefWrapLength(208);
        fp.setStyle("-fx-background-color: green;");
        fp.setPrefHeight(316);

        return fp;
    }

    private FlowPane setupBrandFlow() {
        brandFlow = setupFlowPane();
        return brandFlow;
    }

    private void fillBrandFlow(List<Brand> list) {
        for (Brand brand : list) {
            final ImageView brandLogo = new ImageView(
                    new Image(
                            Objects.requireNonNull(CarApplication.class.getClassLoader().getResourceAsStream(
                                    "brand/" + brand.getTitle().toLowerCase() + ".png"))
                    )
            );
            brandLogo.setFitHeight(70);
            brandLogo.setPreserveRatio(Boolean.TRUE);

            final Button brandButton = new Button(brand.getTitle());
            brandButton.setFocusTraversable(Boolean.FALSE);
            brandButton.setGraphic(brandLogo);
            brandButton.setContentDisplay(ContentDisplay.TOP);
            brandButton.setPrefSize(100, 100);
            brandButton.setMaxSize(100, 100);
            brandButton.setFont(Font.font("Verdana", FontWeight.NORMAL, 10));
            brandButton.setOnAction(event -> {
                modelFlow.getChildren().clear();
                pilotFlow.getChildren().clear();
                loadErrorLabel.setText(null);
                loadModelService.setBrandId(brand.getId());
                loadModelService.restart();
            });

            brandFlow.getChildren().add(brandButton);
        }
    }

    private FlowPane setupModelFlow() {
        modelFlow = setupFlowPane();
        return modelFlow;
    }

    private void fillModelFlow(List<Car> list) {
        for (Car car : list) {
            final Button modelButton = new Button(car.getModel());
            modelButton.setFocusTraversable(Boolean.FALSE);
            modelButton.setPrefSize(100, 100);
            modelButton.setMaxSize(100, 40);
            modelButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    pilotFlow.getChildren().clear();
                    loadErrorLabel.setText(null);
                    loadPilotService.setCarId(car.getId());
                    loadPilotService.restart();
                    selectedCar = car;

                }
            });

            modelFlow.getChildren().add(modelButton);
        }
    }

    private FlowPane setupPilotFlow() {
        pilotFlow = new FlowPane();
        pilotFlow.setPadding(new Insets(15, 10, 15, 10));
        pilotFlow.setVgap(4);
        pilotFlow.setHgap(4);
        pilotFlow.setStyle("-fx-background-color: steelblue;");
        pilotFlow.setPrefHeight(90);

        return pilotFlow;
    }

    private void fillPilotFlow(List<Pilot> list) {
        for (Pilot pilot : list) {
            final Button pilotButton = new Button(pilot.getName());
            pilotButton.setFocusTraversable(Boolean.FALSE);
            pilotButton.setPrefHeight(20);
            pilotButton.setOnAction(event -> {
                if (!isGameStarted) {
                    loadErrorLabel.setText(null);
                    pilotButton.setDisable(Boolean.TRUE);
                    if (isRedSelected) {
                        bluePilot = pilot;
                        isGameStarted = Boolean.TRUE;
                        isRedSelected = Boolean.FALSE;
                    } else {
                        isRedSelected = Boolean.TRUE;
                        redPilot = pilot;
                    }

                    addCarToRoad();
                }

            });

            pilotFlow.getChildren().add(pilotButton);
        }
    }

    private void addCarToRoad() {
        Path path = redPath;
        if (isRedSelected) {
            path = bluePath;
        }
        final String carrPath = isRedSelected ? "car/blue.png" : "car/red.png";

        final ImageView carView = new ImageView(
                new Image(
                        Objects.requireNonNull(CarApplication.class.getClassLoader().getResourceAsStream(
                                carrPath))
                )
        );
        carView.setFitHeight(20);
        carView.setFitWidth(42);

        final Path startPath = new Path();

        startPath.getElements().add(new MoveTo(21, 10));
        startPath.getElements().add(new LineTo(120, isRedSelected ? 70 : 50));


        final PathTransition ptr = new PathTransition();
        ptr.setDuration(Duration.seconds(3));
        ptr.setDelay(Duration.seconds(1));
        ptr.setPath(startPath);
        ptr.setNode(carView);
        ptr.setAutoReverse(Boolean.TRUE);
        ptr.play();
        ptr.setOnFinished(actionEvent -> {
            if (!isRedSelected) {
                startButton.setDisable(Boolean.FALSE);
            }
        });

        final RacingCar car = new RacingCar(
                selectedCar,
                isRedSelected ? redPilot : bluePilot,
                latch
        );

        final PathTransition raceTrans = new PathTransition();
        raceTrans.setDuration(Duration.millis(5.0 / (100.0 / (double) selectedCar.getSpeed() * (double) selectedCar.getPower()) * 100000.0));
        raceTrans.setPath(path);
        raceTrans.setNode(carView);
        raceTrans.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);

        car.setPathTransition(raceTrans);

        centerPane.getChildren().add(carView);
    }

}