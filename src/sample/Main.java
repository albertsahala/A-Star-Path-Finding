package sample;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.ArrayList;

public class Main extends Application implements MapComponentInitializedListener {

    GoogleMapView mapView;
    GoogleMap map;

    private final int SET_ANCHOR_POINT = 0;
    private final int SET_DEST_POINT = 1;
    private final int ADD_POINT = 2;
    private final int ADD_SOURCE_POINT = 3;
    private final int ADD_DEST_POINT = 4;
    private int mode = 2;
    private ArrayList<MarkerLoc> markers;
    private Boolean[][] adjMatrix;
    private MarkerLoc anchorPoint;
    private MarkerLoc nextPoint;
    private MarkerLoc sourcePoint;
    private MarkerLoc destPoint;
    private ListView<String> listView;
    private ObservableList items = FXCollections.observableArrayList();
    private Label distance;

    @Override
    public void start(Stage stage) throws Exception {

        markers = new ArrayList<>();

        //Create the JavaFX component and set this as a listener so we know when
        //the map has been initialized, at which point we can then begin manipulating it.
        mapView = new GoogleMapView();
        mapView.setKey("AIzaSyAOVZHe9mLQQVqROYNAOmoPL77P3eER4z0");
        mapView.setPrefHeight(600);
        mapView.setPrefWidth(900);
        mapView.addMapInializedListener(this);

        final ToggleGroup group = new ToggleGroup();

        final RadioButton btnAddNode = new RadioButton("Add Node");
        btnAddNode.setToggleGroup(group);
        btnAddNode.setSelected(true);
        btnAddNode.getStyleClass().remove("radio-button");
        btnAddNode.getStyleClass().add("toggle-button");
        btnAddNode.setPadding(new Insets(10, 20, 10, 20));
        btnAddNode.setPrefWidth(250);
        btnAddNode.setPrefHeight(50);

        final RadioButton btnAddEdge = new RadioButton("Add Edge");
        btnAddEdge.setToggleGroup(group);
        btnAddEdge.getStyleClass().remove("radio-button");
        btnAddEdge.getStyleClass().add("toggle-button");
        btnAddEdge.setPadding(new Insets(10, 21, 10, 20));
        btnAddEdge.setPrefWidth(250);
        btnAddEdge.setPrefHeight(50);

        final RadioButton btnFindPath = new RadioButton("Find Path");
        btnFindPath.setToggleGroup(group);
        btnFindPath.getStyleClass().remove("radio-button");
        btnFindPath.getStyleClass().add("toggle-button");
        btnFindPath.setPadding(new Insets(10, 22, 10, 20));
        btnFindPath.setPrefWidth(250);
        btnFindPath.setPrefHeight(50);

        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(btnAddNode)) {
                mode = ADD_POINT;
                System.out.println("Add node clicked");
            }
            else if (newValue.equals(btnAddEdge)) {
                mode = SET_ANCHOR_POINT;
                System.out.println("Add edge clicked");
            }
            else if (newValue.equals(btnFindPath)) {
                mode = ADD_SOURCE_POINT;
                System.out.println("Find path clicked");
            }
        });

        adjMatrix = new Boolean[100][100];

        for (int i = 0; i < adjMatrix.length; i++) {
            for (int j = 0; j < adjMatrix[i].length; j++) {
                adjMatrix[i][j] = false;
            }
        }

        listView = new ListView<>();
        listView.setPrefHeight(400);
        listView.setPrefWidth(250);
        listView.setItems(items);

        distance = new Label();
        distance.setPadding(new Insets(10, 20, 10, 20));
        distance.setPrefHeight(50);
        distance.setPrefWidth(250);
        distance.setText("Distance: ");

        VBox vbox = new VBox();

        HBox hbox = new HBox();

        vbox.setPrefWidth(250);
        vbox.setPrefHeight(600);

        vbox.getChildren().add(btnAddNode);
        vbox.getChildren().add(btnAddEdge);
        vbox.getChildren().add(btnFindPath);
        vbox.getChildren().add(listView);
        vbox.getChildren().add(distance);

        hbox.getChildren().add(mapView);
        hbox.getChildren().add(vbox);

        Scene scene = new Scene(new Group(), 1150, 600);

        ((Group) scene.getRoot()).getChildren().add(hbox);

        scene.getStylesheets().add(getClass().getResource("FlatBee.css").toString());

        stage.setTitle("JavaFX and Google Maps");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void mapInitialized() {
        //Set the initial properties of the map.
        MapOptions mapOptions = new MapOptions();

        System.out.println("Initializing Map");

        mapOptions.center(new LatLong(-6.8948, 107.61))
                .mapType(MapTypeIdEnum.ROADMAP)
                .overviewMapControl(false)
                .panControl(false)
                .rotateControl(false)
                .scaleControl(false)
                .streetViewControl(false)
                .zoomControl(false)
                .zoom(16);

        map = mapView.createMap(mapOptions);

        System.out.println("Map created");

        map.addMouseEventHandler(UIEventType.click, gMapMouseEvent -> {
            LatLong latLong = gMapMouseEvent.getLatLong();
            System.out.println(latLong.toString());

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLong)
                    .title(Integer.toString(markers.size() + 1));

            MarkerLoc marker = new MarkerLoc(markerOptions, latLong);

            map.addUIEventHandler(marker, UIEventType.click, jsObject -> {
                if (mode == SET_ANCHOR_POINT) {
                    System.out.println("Set anchor point");
                    anchorPoint = marker;
                    mode = SET_DEST_POINT;
                }
                else if (mode == SET_DEST_POINT) {
                    System.out.println("Set dest point");
                    nextPoint = marker;

                    System.out.println(mode);

                    adjMatrix[markers.indexOf(anchorPoint)][markers.indexOf(nextPoint)] = true;

                    System.out.println(mode);

                    System.out.println("Set dest point");

                    adjMatrix[markers.indexOf(nextPoint)][markers.indexOf(anchorPoint)] = true;

                    System.out.println("Set dest point");

                    for (Boolean[] anAdjMatrix : adjMatrix) {
                        for (int j = 0; j < adjMatrix.length; j++) {
                            System.out.print(anAdjMatrix[j] + " ");
                        }
                        System.out.println();
                    }

                    drawLine(anchorPoint, nextPoint, "#9bbff4");
                    mode = SET_ANCHOR_POINT;
                }
                else if (mode == ADD_SOURCE_POINT) {
                    sourcePoint = marker;

                    mode = ADD_DEST_POINT;
                }
                else if (mode == ADD_DEST_POINT) {
                    destPoint = marker;
                    ArrayList<MarkerLoc> order = Helper.AStar(markers, sourcePoint, destPoint, adjMatrix);

                    if (order != null) {
                        System.out.println("Order");
                        for (MarkerLoc anOrder : order) {
                            System.out.println(anOrder.getTitle());
                        }
                        generatePath(order);
                        distance.setText("Distance: " + Double.toString(Math.round(getDistance(order))) + " meters");
                    }
                    mode = ADD_SOURCE_POINT;
                }
            });

            if (mode == ADD_POINT) {
                map.addMarker(marker);
                markers.add(marker);
                items.add(Integer.toString(markers.size()) + ". " + marker.getLatLong());
                listView.setItems(items);
            }

        });

    }

    private void drawLine(MarkerLoc anchorPoint, MarkerLoc nextPoint, String color) {
        LatLong ll1 = new LatLong(anchorPoint.getLatLong().getLatitude(), anchorPoint.getLatLong().getLongitude());
        LatLong ll2 = new LatLong(nextPoint.getLatLong().getLatitude(), nextPoint.getLatLong().getLongitude());

        LatLong[] latLongs = new LatLong[] {ll1, ll2};

        MVCArray mvc = new MVCArray(latLongs);
        PolylineOptions polyOpts = new PolylineOptions()
                .path(mvc)
                .strokeColor(color)
                .strokeWeight(4);
        Polyline poly = new Polyline(polyOpts);

        map.addMapShape(poly);
    }

    private void generatePath(ArrayList<MarkerLoc> markers) {
        for (int i = 0; i < markers.size() - 1; i++) {
            MarkerLoc firstPoint = markers.get(i);
            MarkerLoc secondPoint = markers.get(i + 1);
            drawLine(firstPoint, secondPoint, "#E53935");
        }
    }

    private double getDistance(ArrayList<MarkerLoc> markers) {
        double sum = 0;
        for (int i = 0; i < markers.size() - 1; i++) {
            MarkerLoc firstPoint = markers.get(i);
            MarkerLoc secondPoint = markers.get(i + 1);
            sum += firstPoint.getLatLong().distanceFrom(secondPoint.getLatLong());
        }
        return sum;
    }

    public static void main(String[] args) {
        launch(args);
    }
}