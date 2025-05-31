package com.estudomais.demo.app;

import com.estudomais.demo.view.AlunoView;
import com.estudomais.demo.view.DisciplinaView;
import com.estudomais.demo.view.TarefaView;
import com.estudomais.demo.view.DetalhesView;
import com.estudomais.demo.view.VinculoAlunoDisciplinaView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MenuPrincipal extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Menu Principal");

        Button btnAluno = new Button("CRUD Aluno");
        Button btnDisciplina = new Button("CRUD Disciplina");
        Button btnTarefa = new Button("CRUD Tarefa");
        Button btnDetalhes = new Button("Ver Detalhes");
        Button btnVinculo = new Button("Gerenciar Vínculo Aluno-Disciplina");

        btnAluno.setOnAction(e -> new AlunoView().exibir());
        btnDisciplina.setOnAction(e -> new DisciplinaView().exibir());
        btnTarefa.setOnAction(e -> new TarefaView().exibir());
        btnDetalhes.setOnAction(e -> new DetalhesView().exibir());
        btnVinculo.setOnAction(e -> new VinculoAlunoDisciplinaView().exibir());

        HBox linha1 = new HBox(10, btnAluno, btnDisciplina);
        HBox linha2 = new HBox(10, btnTarefa, btnDetalhes);
        HBox linha3 = new HBox(10, btnVinculo);

        VBox layout = new VBox(15, linha1, linha2, linha3);
        layout.setPadding(new javafx.geometry.Insets(20));

        Scene scene = new Scene(layout, 500, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
