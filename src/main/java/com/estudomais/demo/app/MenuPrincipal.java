package com.estudomais.demo.app;

import com.estudomais.demo.view.AlunoView;
import com.estudomais.demo.view.DetalhesView;
import com.estudomais.demo.view.DisciplinaView;
import com.estudomais.demo.view.TarefaView;
import com.estudomais.demo.view.VinculoAlunoDisciplinaView;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MenuPrincipal extends Application {

    private ObservableList<String> logsData = FXCollections.observableArrayList();
    private TableView<String> tabelaLogs = new TableView<>();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Estudo+");

        // Tabela de logs (direita)
        TableColumn<String, String> colAcao = new TableColumn<>("Histórico de Ações");
        colAcao.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()));
        colAcao.setPrefWidth(600);
        tabelaLogs.getColumns().add(colAcao);
        tabelaLogs.setItems(logsData);

        // Adiciona o primeiro log ao iniciar
        adicionarLog("Sistema inicializado");

        // Botões da sidebar
        Button btnAluno = new Button("Aluno");
        Button btnDisciplina = new Button("Disciplina");
        Button btnTarefa = new Button("Tarefa");
        Button btnDetalhes = new Button("Ver Detalhes");
        Button btnVinculo = new Button("Gerenciar Vínculo");

        Button[] botoes = {btnAluno, btnDisciplina, btnTarefa, btnDetalhes, btnVinculo};
        for (Button b : botoes) {
            b.setStyle("-fx-background-color: #2e86de; -fx-text-fill: white; -fx-font-weight: bold;");
            b.setMaxWidth(Double.MAX_VALUE);
        }

        // Ações dos botões + log
        btnAluno.setOnAction(e -> {
            new AlunoView().exibir();
            adicionarLog("Tela de Aluno aberta");
        });

        btnDisciplina.setOnAction(e -> {
            new DisciplinaView().exibir();
            adicionarLog("Tela de Disciplina aberta");
        });

        btnTarefa.setOnAction(e -> {
            new TarefaView().exibir();
            adicionarLog("Tela de Tarefa aberta");
        });

        btnDetalhes.setOnAction(e -> {
            new DetalhesView().exibir();
            adicionarLog("Tela de Detalhes aberta");
        });

        btnVinculo.setOnAction(e -> {
            new VinculoAlunoDisciplinaView().exibir();
            adicionarLog("Tela de Vínculo Aluno x Disciplina aberta");
        });

        // Sidebar
        VBox sidebar = new VBox(15, btnAluno, btnDisciplina, btnTarefa, btnDetalhes, btnVinculo);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #dfe6e9;");
        sidebar.setPrefWidth(200);
        sidebar.setAlignment(Pos.TOP_CENTER);
        sidebar.setEffect(new DropShadow(5, Color.GRAY));

        // Painel da tabela
        VBox painelLogs = new VBox(tabelaLogs);
        painelLogs.setPadding(new Insets(20));
        painelLogs.setAlignment(Pos.TOP_CENTER);

        // Layout principal: sidebar + painelLogs
        HBox layoutPrincipal = new HBox(sidebar, painelLogs);

        Scene scene = new Scene(layoutPrincipal, 900, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Método para adicionar logs à tabela
    private void adicionarLog(String texto) {
        logsData.add(texto);
        tabelaLogs.scrollTo(logsData.size() - 1);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
