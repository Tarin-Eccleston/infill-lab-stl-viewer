package com.example;

import javafx.application.Application;
import javafx.collections.ObservableFloatArray;
import javafx.collections.ObservableIntegerArray;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.ObservableFaceArray;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.io.Console;
import java.io.File;
import java.io.IOException;

/**
 * JavaFX App
 */


public class App extends Application {

    private static Scene scene;
    private static final boolean obj = false;
    private static final boolean filled = true;
    private Font universalFont = new Font("Arial", 12);



public float SignedVolumeOfTriangle(float p1x,float p1y,float p1z,float p2x,float p2y,float p2z,float p3x,float p3y,float p3z)
{
    var v321 = p3x * p2y * p1z;
    var v231 = p2x * p3y * p1z;
    var v312 = p3x * p1y * p2z;
    var v132 = p1x * p3y * p2z;
    var v213 = p2x * p1y * p3z;
    var v123 = p1x * p2y * p3z;
    return (1.0f / 6.0f) * (-v321 + v231 + v312 - v132 - v213 + v123);
}

public double SignedareaOfTriangle(double p1x,double p1y,double p1z,double p2x,double p2y,double p2z,double p3x,double p3y,double p3z)
{
    double Yab = p2y - p1y;
    double Zac = p3z - p1z;
    double Zab = p2z - p1z;
    double Yac = p3y - p1y;
    double Xac = p3x - p1x;
    double Xab = p2x - p1x;

    double s1 = ((Yab*Zac)-(Zab*Yac));
    double s2 = ((Zab*Xac)-(Xab*Zac));
    double s3 = ((Xab*Yac)-(Yab*Xac));

    return 0.5f * Math.sqrt((s1*s1)+(s2*s2)+(s3*s3));
}

    public Parent createContent() throws Exception{
        /* Import STL model */
        MeshView example;
        if(obj){
          example = Loader.loadObj("assets/models/computer.obj");
        }else{
          example = Loader.loadStl("assets/models/computer.stl");
        }
        if(filled){
          PhongMaterial pm = new PhongMaterial();
          pm.setDiffuseMap(new Image((new File("assets/diffuse/computer.png").toURI().toString())));
          pm.setSpecularColor(Color.WHITE);
          example.setMaterial(pm);
        }else{
          example.setMaterial(new PhongMaterial(Color.RED));
          example.setDrawMode(DrawMode.LINE);
        }
        /* Position the camera in the scene */
        PerspectiveCamera camera = new PerspectiveCamera(true);
        if(obj){
          camera.getTransforms().addAll(
            new Rotate(-5, Rotate.Y_AXIS),
            new Rotate(-110 + 270, Rotate.X_AXIS),
            new Translate(0, 0, -80)
          );
        }else{
          camera.getTransforms().addAll(
            new Rotate(5, Rotate.Y_AXIS),
            new Rotate(-110, Rotate.X_AXIS),
            new Translate(0, 0, -80)
          );
        }

        TriangleMesh mesh = (TriangleMesh) example.getMesh();
        ObservableFaceArray faces = mesh.getFaces();
        ObservableFloatArray vertexs = mesh.getPoints();

        float total_volume = 0;
        float total_area = 0;

        float min_x = 9999;
        float min_y = 9999;
        float min_z = 9999;
        float max_x = -9999;
        float max_y = -9999;
        float max_z = -9999;
        // Calculate min / max x,y,z values of .stl
        for(int i=0;(i+1)*3<=vertexs.size();i++){
          // X max / min
          if (min_x > vertexs.get(i*3)){
            min_x  = vertexs.get(i*3);
          }
          if (max_x < vertexs.get(i*3)){
            max_x  = vertexs.get(i*3);
          }

          // Y max / min
          if (min_y > vertexs.get(i*3+1)){
            min_y  = vertexs.get(i*3+1);
          }
          if (max_y < vertexs.get(i*3+1)){
            max_y  = vertexs.get(i*3+1);
          } 

          // Z max / min
          if (min_z > vertexs.get(i*3+2)){
            min_z  = vertexs.get(i*3+2);
          }
          if (max_z < vertexs.get(i*3+2)){
            max_z  = vertexs.get(i*3+2);
          } 
        }

        float test1 = 0;
        float bounding_vol = (max_x - min_x) * (max_y - min_y) * (max_z - min_z);

        for(int i=0;(i+1)*9<=vertexs.size();i++){
          total_volume += SignedVolumeOfTriangle(vertexs.get(i*9+0), vertexs.get(i*9+1), vertexs.get(i*9+2),vertexs.get(i*9+3), vertexs.get(i*9+4), vertexs.get(i*9+5),vertexs.get(i*9+6), vertexs.get(i*9+7), vertexs.get(i*9+8));
          total_area += SignedareaOfTriangle(vertexs.get(i*9+0), vertexs.get(i*9+1), vertexs.get(i*9+2),vertexs.get(i*9+3), vertexs.get(i*9+4), vertexs.get(i*9+5),vertexs.get(i*9+6), vertexs.get(i*9+7), vertexs.get(i*9+8));
        }

        /* Build the Scene Graph */
        Text detail_text1 = new Text (10, 20, "volume = " + total_volume + " surface area = " + total_area);     
        Text detail_text2 = new Text (10, 40, "(max,min) = " + min_x + " " + min_y + " " + min_z + " " + max_x + " " + max_y + " "+ max_z);    
        Text detail_text3 = new Text (10, 60, "Bounding Box Vol = " + bounding_vol);    
        detail_text1.setFont(universalFont);
        detail_text2.setFont(universalFont);
        detail_text3.setFont(universalFont);

        Group root = new Group();
        
        root.getChildren().add(camera);
        root.getChildren().add(example);
        /* Use a SubScene */
        SubScene subScene = new SubScene(root, 500, 500);
        subScene.setFill(Color.ALICEBLUE);
        subScene.setCamera(camera);
        Group group = new Group();
        group.getChildren().add(subScene);
        group.getChildren().add(detail_text1);
        group.getChildren().add(detail_text2);
        group.getChildren().add(detail_text3);
        return group;
      }

    @Override
    public void start(Stage stage) throws Exception {
        /* 
        scene = new Scene(loadFXML("primary"), 640, 480);
        stage.setScene(scene);
        stage.show();
        */

        stage.setResizable(false);
        Scene scene = new Scene(createContent());
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}