package com.estudomais.demo.app;

import com.estudomais.demo.view.AlunoView;
import com.estudomais.demo.view.DetalhesView;
import com.estudomais.demo.view.DisciplinaView;
import com.estudomais.demo.view.TarefaView;
import com.estudomais.demo.view.VinculoAlunoDisciplinaView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MenuPrincipal extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Menu Principal - Estudo+");

        // Botões
        Button btnAluno = new Button("Aluno");
        Button btnDisciplina = new Button("Disciplina");
        Button btnTarefa = new Button("Tarefa");
        Button btnDetalhes = new Button("Ver Detalhes");
        Button btnVinculo = new Button("Gerenciar Vínculo");

        Button[] botoes = {btnAluno, btnDisciplina, btnTarefa, btnDetalhes, btnVinculo};
        for (Button b : botoes) {
            b.setStyle("-fx-background-color: #2e86de; -fx-text-fill: white; -fx-font-weight: bold;");
            b.setPrefWidth(160);
            b.setPrefHeight(40);
        }

        // Ações
        btnAluno.setOnAction(e -> new AlunoView().exibir());
        btnDisciplina.setOnAction(e -> new DisciplinaView().exibir());
        btnTarefa.setOnAction(e -> new TarefaView().exibir());
        btnDetalhes.setOnAction(e -> new DetalhesView().exibir());
        btnVinculo.setOnAction(e -> new VinculoAlunoDisciplinaView().exibir());

        // Layout horizontal dos botões
        HBox botoesBox = new HBox(15, btnAluno, btnDisciplina, btnTarefa, btnDetalhes, btnVinculo);
        botoesBox.setAlignment(Pos.CENTER);

        // Retângulo container
        VBox container = new VBox(botoesBox);
        container.setPadding(new Insets(30));
        container.setAlignment(Pos.CENTER);
        container.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(15), Insets.EMPTY)));
        container.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, new CornerRadii(15), BorderWidths.DEFAULT)));
        container.setEffect(new DropShadow(10, Color.GRAY));

        // Fundo da cena
        StackPane root = new StackPane(container);
        root.setStyle("-fx-background-color: #ecf0f1;");

        Scene scene = new Scene(root, 900, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
