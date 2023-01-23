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
    // private Font universalFont = new Font("Arial", FontWeight.BOLD, 12);
/* 
    public float SignedVolumeOfTriangle(Vector3 p1, Vector3 p2, Vector3 p3)
    {
        var v321 = p3.x * p2.y * p1.z;
        var v231 = p2.x * p3.y * p1.z;
        var v312 = p3.x * p1.y * p2.z;
        var v132 = p1.x * p3.y * p2.z;
        var v213 = p2.x * p1.y * p3.z;
        var v123 = p1.x * p2.y * p3.z;
        return (1.0f / 6.0f) * (-v321 + v231 + v312 - v132 - v213 + v123);
    }
*/


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
        for(int i=0;(i+1)*9<=vertexs.size();i++){
          total_volume += SignedVolumeOfTriangle(vertexs.get(i*9+0), vertexs.get(i*9+1), vertexs.get(i*9+2),vertexs.get(i*9+3), vertexs.get(i*9+4), vertexs.get(i*9+5),vertexs.get(i*9+6), vertexs.get(i*9+7), vertexs.get(i*9+8));
          //System.out.print(SignedVolumeOfTriangle(vertexs.get(i*9+0), vertexs.get(i*9+1), vertexs.get(i*9+2),vertexs.get(i*9+3), vertexs.get(i*9+4), vertexs.get(i*9+5),vertexs.get(i*9+6), vertexs.get(i*9+7), vertexs.get(i*9+8)));
          //Console.sendMessage(total_volume.toString());
        }
/* 
        foreach (TriangleMesh mesh in meshArray)
        {
            total_volume += SignedVolumeOfTriangle(mesh.vert1, mesh.vert2, mesh.vert3);
        }
*/
        /* Build the Scene Graph */
        Text t = new Text (10, 20, "This is a text sample" + total_volume );
        
        t.setFont(universalFont);

        Group root = new Group();
        
        root.getChildren().add(camera);
        root.getChildren().add(example);
        /* Use a SubScene */
        SubScene subScene = new SubScene(root, 500, 500);
        subScene.setFill(Color.ALICEBLUE);
        subScene.setCamera(camera);
        Group group = new Group();
        group.getChildren().add(subScene);
        group.getChildren().add(t);
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