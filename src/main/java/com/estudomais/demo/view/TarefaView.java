package com.estudomais.demo.view;

import com.estudomais.demo.model.Disciplina;
import com.estudomais.demo.model.Tarefa;
import com.estudomais.demo.persistence.DisciplinaDAO;
import com.estudomais.demo.persistence.TarefaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class TarefaView {

    private ObservableList<Tarefa> tarefasData = FXCollections.observableArrayList();

    public void exibir() {
        Stage stage = new Stage();
        stage.setTitle("Cadastro de Tarefa");

        TextField txtTitulo = new TextField();
        TextArea txtDescricao = new TextArea();
        DatePicker dateEntrega = new DatePicker();
        TextField txtPeso = new TextField();

        ComboBox<String> comboDisciplinas = new ComboBox<>();
        comboDisciplinas.setItems(FXCollections.observableArrayList(
                DisciplinaDAO.listarDisciplinas().stream().map(Disciplina::getNome).collect(Collectors.toList())
        ));

        ComboBox<String> comboRemover = new ComboBox<>();
        comboRemover.setPromptText("Selecione a tarefa para remover");

        ComboBox<String> comboEditar = new ComboBox<>();
        comboEditar.setPromptText("Selecione a tarefa para editar");

        atualizarCombo(comboRemover);
        atualizarCombo(comboEditar);

        Button btnSalvar = new Button("Salvar");
        Button btnRemover = new Button("Remover Tarefa");
        Button btnEditar = new Button("Editar Tarefa");

        TextArea txtArea = new TextArea();
        txtArea.setEditable(false);
        txtArea.setPrefHeight(100);

        TableView<Tarefa> tabelaTarefas = criarTabelaTarefas();
        atualizarTabelaTarefas(tabelaTarefas);

        btnSalvar.setOnAction(e -> {
            String titulo = txtTitulo.getText().trim();
            String descricao = txtDescricao.getText().trim();
            LocalDate dataEntrega = dateEntrega.getValue();
            String disciplina = comboDisciplinas.getValue();
            String pesoStr = txtPeso.getText().trim();

            if (titulo.isEmpty() || descricao.isEmpty() || dataEntrega == null || disciplina == null || pesoStr.isEmpty()) {
                txtArea.setText("Erro: Todos os campos devem ser preenchidos.");
                return;
            }

            if (dataEntrega.isBefore(LocalDate.now())) {
                txtArea.setText("Erro: A data de entrega não pode ser no passado.");
                return;
            }

            double peso;
            try {
                peso = Double.parseDouble(pesoStr);
                if (peso < 0 || peso > 100) {
                    txtArea.setText("Erro: O peso da tarefa deve estar entre 0 e 100.");
                    return;
                }
            } catch (NumberFormatException ex) {
                txtArea.setText("Erro: Peso inválido. Use números como 25 ou 50.5.");
                return;
            }

            try {
                Tarefa tarefa = new Tarefa(titulo, descricao, dataEntrega, disciplina, peso);
                TarefaDAO.salvarTarefa(tarefa);
                txtArea.setText("Tarefa salva ou atualizada com sucesso!");
                atualizarCombo(comboRemover);
                atualizarCombo(comboEditar);
                atualizarTabelaTarefas(tabelaTarefas);
                txtTitulo.setDisable(false);
            } catch (IOException ex) {
                txtArea.setText("Erro ao salvar: " + ex.getMessage());
            }
        });

        btnRemover.setOnAction(e -> {
            String tituloSelecionado = comboRemover.getValue();
            if (tituloSelecionado == null) {
                txtArea.setText("Selecione uma tarefa para remover.");
                return;
            }

            try {
                TarefaDAO.removerPorTitulo(tituloSelecionado);
                txtArea.setText("Tarefa removida com sucesso.");
                atualizarCombo(comboRemover);
                atualizarCombo(comboEditar);
                atualizarTabelaTarefas(tabelaTarefas);
            } catch (IOException ex) {
                txtArea.setText("Erro ao remover: " + ex.getMessage());
            }
        });

        btnEditar.setOnAction(e -> {
            String tituloSelecionado = comboEditar.getValue();
            if (tituloSelecionado == null) {
                txtArea.setText("Selecione uma tarefa para editar.");
                return;
            }

            Tarefa tarefa = TarefaDAO.listarTarefas().stream()
                    .filter(t -> t.getTitulo().equalsIgnoreCase(tituloSelecionado))
                    .findFirst().orElse(null);

            if (tarefa != null) {
                txtTitulo.setText(tarefa.getTitulo());
                txtTitulo.setDisable(true); // título não pode ser alterado
                txtDescricao.setText(tarefa.getDescricao());
                dateEntrega.setValue(tarefa.getDataEntrega());
                comboDisciplinas.setValue(tarefa.getDisciplinaAssociada());
                txtPeso.setText(String.valueOf(tarefa.getPeso()));
                txtArea.setText("Altere os campos e clique em 'Salvar' para atualizar.");
            } else {
                txtArea.setText("Erro: tarefa não encontrada.");
            }
        });

        VBox formCadastro = new VBox(8,
                new Label("Título da Tarefa:"), txtTitulo,
                new Label("Descrição:"), txtDescricao,
                new Label("Data de Entrega:"), dateEntrega,
                new Label("Disciplina Associada:"), comboDisciplinas,
                new Label("Peso da Tarefa:"), txtPeso,
                btnSalvar,
                new Label("Remover Tarefa:"), comboRemover, btnRemover,
                new Label("Editar Tarefa:"), comboEditar, btnEditar,
                new Label("Mensagens do sistema:"), txtArea
        );
        formCadastro.setPadding(new Insets(10));
        formCadastro.setStyle("-fx-background-color: #eef6fb; -fx-border-color: #ccc; -fx-border-radius: 8;");

        HBox layoutPrincipal = new HBox(20, formCadastro, tabelaTarefas);
        layoutPrincipal.setPadding(new Insets(20));

        Scene scene = new Scene(layoutPrincipal, 1000, 750);
        stage.setScene(scene);
        stage.show();
    }

    private void atualizarCombo(ComboBox<String> combo) {
        combo.getItems().setAll(
                TarefaDAO.listarTarefas().stream()
                        .map(Tarefa::getTitulo)
                        .collect(Collectors.toList())
        );
        combo.setValue(null);
    }

    private TableView<Tarefa> criarTabelaTarefas() {
        TableView<Tarefa> tabela = new TableView<>();

        TableColumn<Tarefa, String> colTitulo = new TableColumn<>("Título");
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));

        TableColumn<Tarefa, String> colDescricao = new TableColumn<>("Descrição");
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));

        TableColumn<Tarefa, LocalDate> colEntrega = new TableColumn<>("Data de Entrega");
        colEntrega.setCellValueFactory(new PropertyValueFactory<>("dataEntrega"));

        TableColumn<Tarefa, String> colDisciplina = new TableColumn<>("Disciplina");
        colDisciplina.setCellValueFactory(new PropertyValueFactory<>("disciplinaAssociada"));

        TableColumn<Tarefa, Double> colPeso = new TableColumn<>("Peso");
        colPeso.setCellValueFactory(new PropertyValueFactory<>("peso"));

        tabela.getColumns().addAll(colTitulo, colDescricao, colEntrega, colDisciplina, colPeso);
        tabela.setPrefWidth(700);
        tabela.setItems(tarefasData);

        return tabela;
    }

    private void atualizarTabelaTarefas(TableView<Tarefa> tabela) {
        tarefasData.setAll(TarefaDAO.listarTarefas());
        tabela.refresh();
    }
}
