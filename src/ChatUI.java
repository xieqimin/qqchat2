import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ChatUI{

    private String fid;//
    private String fname;//
    private Client client;
    private TextArea chatlabel=new TextArea();//
    private Stage stage;
    //private Label chatlabel=new Label();
    //private Timeline timeline=new Timeline(new KeyFrame(Duration.millis(1800), event -> smessageHandle()));

    public void close(){
        stage.close();
    }
    public String getFid() {
        return fid;
    }
    /*private void smessageHandle(){
        if(cansmessage) {
            try {
                String s = sMessage(fid, id);
                if(!s.equals("no message on")) {
                    addMessage(s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/
    public ChatUI(Client client,String fname,String fid){
        this.client=client;
        this.fid=fid;
        this.fname=fname;
    }
    public void addMessage(String m){
        chatlabel.appendText("\n"+fname+":"+m);
    }
    public Stage getUI(){
        ScrollPane scrollPane=new ScrollPane();
        scrollPane.setContent(chatlabel);
        BorderPane borderPane=new BorderPane();
        HBox hBox=new HBox();
        TextArea outText=new TextArea();
        Button button=new Button("发送");
        Label fnameLable=new Label(fname);
        //timeline.setCycleCount(Timeline.INDEFINITE);
        //timeline.play();
        outText.setPrefRowCount(4);
        button.setOnAction(e->{
            String out=outText.getText();
            client.fMessage(fid,out);
            chatlabel.appendText("\n"+client.getMyname()+":"+out);//加时间
            outText.setText("");
        });

        chatlabel.setEditable(false);
        hBox.getChildren().add(outText);
        hBox.getChildren().add(button);
        borderPane.setTop(fnameLable);
        borderPane.setCenter(scrollPane);
        borderPane.setBottom(hBox);
        Scene scene=new Scene(borderPane);
        stage=new Stage();
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {//关闭处理
            client.removeIDchatUI(fid);
        });
        return stage;
    }
}