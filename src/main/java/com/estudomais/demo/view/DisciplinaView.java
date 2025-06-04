package com.estudomais.demo.view;

import com.estudomais.demo.model.Disciplina;
import com.estudomais.demo.persistence.DisciplinaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class DisciplinaView {

    private boolean modoEdicao = false;
    private ObservableList<Disciplina> disciplinasData = FXCollections.observableArrayList();

    public void exibir() {
        Stage stage = new Stage();
        stage.setTitle("Cadastro de Disciplina");

        TextField txtNome = new TextField();
        TextField txtCodigo = new TextField();
        TextField txtCargaHoraria = new TextField();
        TextField txtSemestre = new TextField();
        TextField txtProfessor = new TextField();

        ComboBox<String> comboRemover = new ComboBox<>();
        comboRemover.setPromptText("Selecione a disciplina para remover");

        ComboBox<String> comboEditar = new ComboBox<>();
        comboEditar.setPromptText("Selecione a disciplina para editar");

        atualizarCombo(comboRemover);
        atualizarCombo(comboEditar);

        Button btnSalvar = new Button("Salvar");
        Button btnRemover = new Button("Remover Disciplina");
        Button btnEditar = new Button("Editar Disciplina");

        TextArea txtArea = new TextArea();
        txtArea.setEditable(false);
        txtArea.setPrefHeight(100);

        TableView<Disciplina> tabelaDisciplinas = criarTabelaDisciplinas();
        atualizarTabelaDisciplinas(tabelaDisciplinas);

        btnSalvar.setOnAction(e -> {
            String nome = txtNome.getText().trim();
            String codigo = txtCodigo.getText().trim();
            String carga = txtCargaHoraria.getText().trim();
            String semestre = txtSemestre.getText().trim();
            String professor = txtProfessor.getText().trim();

            if (nome.isEmpty() || codigo.isEmpty() || carga.isEmpty() || semestre.isEmpty() || professor.isEmpty()) {
                txtArea.setText("Erro: Todos os campos obrigatórios devem ser preenchidos.");
                return;
            }

            if (!nome.chars().allMatch(c -> Character.isLetter(c) || Character.isWhitespace(c))) {
                txtArea.setText("Erro: O nome da disciplina deve conter apenas letras.");
                return;
            }

            if (!professor.chars().allMatch(c -> Character.isLetter(c) || Character.isWhitespace(c))) {
                txtArea.setText("Erro: O nome do professor deve conter apenas letras.");
                return;
            }

            int cargaInt;
            try {
                cargaInt = Integer.parseInt(carga);
                if (cargaInt <= 0) {
                    txtArea.setText("Erro: A carga horária deve ser maior que zero.");
                    return;
                }
            } catch (NumberFormatException ex1) {
                txtArea.setText("Erro: Carga horária deve ser um número inteiro.");
                return;
            }

            int semestreInt;
            try {
                semestreInt = Integer.parseInt(semestre);
                if (semestreInt < 1 || semestreInt > 10) {
                    txtArea.setText("Erro: O semestre deve estar entre 1 e 10.");
                    return;
                }
            } catch (NumberFormatException ex2) {
                txtArea.setText("Erro: O semestre deve ser um número válido.");
                return;
            }

            List<Disciplina> existentes = DisciplinaDAO.listarDisciplinas();
            boolean codigoDuplicado = existentes.stream()
                    .anyMatch(d -> d.getCodigo().equalsIgnoreCase(codigo) &&
                            (!modoEdicao || !d.getNome().equalsIgnoreCase(nome)));

            if (codigoDuplicado) {
                txtArea.setText("Erro: Já existe uma disciplina com esse código.");
                return;
            }

            try {
                Disciplina disciplina = new Disciplina(nome, codigo, cargaInt, semestre, professor);
                DisciplinaDAO.salvarDisciplina(disciplina);
                txtArea.setText(modoEdicao ? "Disciplina atualizada com sucesso!" : "Disciplina salva com sucesso!");

                atualizarCombo(comboRemover);
                atualizarCombo(comboEditar);
                atualizarTabelaDisciplinas(tabelaDisciplinas);
                txtCodigo.setDisable(false);
                modoEdicao = false;
            } catch (IOException ex3) {
                txtArea.setText("Erro ao salvar: " + ex3.getMessage());
            }
        });

        btnRemover.setOnAction(e -> {
            String nomeSelecionado = comboRemover.getValue();
            if (nomeSelecionado == null) {
                txtArea.setText("Selecione uma disciplina para remover.");
                return;
            }

            try {
                Disciplina disciplina = DisciplinaDAO.listarDisciplinas().stream()
                        .filter(d -> d.getNome().equalsIgnoreCase(nomeSelecionado))
                        .findFirst().orElse(null);

                if (disciplina != null) {
                    DisciplinaDAO.removerPorCodigo(disciplina.getCodigo());
                    txtArea.setText("Disciplina removida com sucesso.");
                    atualizarCombo(comboRemover);
                    atualizarCombo(comboEditar);
                    atualizarTabelaDisciplinas(tabelaDisciplinas);
                } else {
                    txtArea.setText("Erro: disciplina não encontrada.");
                }
            } catch (IOException ex4) {
                txtArea.setText("Erro ao remover: " + ex4.getMessage());
            }
        });

        btnEditar.setOnAction(e -> {
            String nomeSelecionado = comboEditar.getValue();
            if (nomeSelecionado == null) {
                txtArea.setText("Selecione uma disciplina para editar.");
                return;
            }

            Disciplina disciplina = DisciplinaDAO.listarDisciplinas().stream()
                    .filter(d -> d.getNome().equalsIgnoreCase(nomeSelecionado))
                    .findFirst().orElse(null);

            if (disciplina != null) {
                txtNome.setText(disciplina.getNome());
                txtCodigo.setText(disciplina.getCodigo());
                txtCodigo.setDisable(true);
                txtCargaHoraria.setText(String.valueOf(disciplina.getCargaHoraria()));
                txtSemestre.setText(disciplina.getSemestre());
                txtProfessor.setText(disciplina.getProfessorResponsavel());
                modoEdicao = true;
                txtArea.setText("Preencha os dados e clique em salvar para atualizar.");
            } else {
                txtArea.setText("Erro: disciplina não encontrada.");
            }
        });

        VBox formCadastro = new VBox(8,
                new Label("Nome da Disciplina:"), txtNome,
                new Label("Código da Disciplina:"), txtCodigo,
                new Label("Carga Horária:"), txtCargaHoraria,
                new Label("Semestre ou Período:"), txtSemestre,
                new Label("Professor Responsável:"), txtProfessor,
                btnSalvar,
                new Label("Remover Disciplina:"), comboRemover, btnRemover,
                new Label("Editar Disciplina:"), comboEditar, btnEditar,
                new Label("Mensagens do sistema:"), txtArea
        );
        formCadastro.setPadding(new Insets(10));
        formCadastro.setStyle("-fx-background-color: #eef6fb; -fx-border-color: #ccc; -fx-border-radius: 8;");

        HBox layoutPrincipal = new HBox(20, formCadastro, tabelaDisciplinas);
        layoutPrincipal.setPadding(new Insets(20));

        Scene scene = new Scene(layoutPrincipal, 1000, 700);
        stage.setScene(scene);
        stage.show();
    }

    private void atualizarCombo(ComboBox<String> combo) {
        combo.getItems().setAll(
                DisciplinaDAO.listarDisciplinas().stream()
                        .map(Disciplina::getNome)
                        .collect(Collectors.toList())
        );
        combo.setValue(null);
    }

    private TableView<Disciplina> criarTabelaDisciplinas() {
        TableView<Disciplina> tabela = new TableView<>();

        TableColumn<Disciplina, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<Disciplina, String> colCodigo = new TableColumn<>("Código");
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));

        TableColumn<Disciplina, Integer> colCarga = new TableColumn<>("Carga Horária");
        colCarga.setCellValueFactory(new PropertyValueFactory<>("cargaHoraria"));

        TableColumn<Disciplina, String> colSemestre = new TableColumn<>("Semestre");
        colSemestre.setCellValueFactory(new PropertyValueFactory<>("semestre"));

        TableColumn<Disciplina, String> colProfessor = new TableColumn<>("Professor");
        colProfessor.setCellValueFactory(new PropertyValueFactory<>("professorResponsavel"));

        tabela.getColumns().addAll(colNome, colCodigo, colCarga, colSemestre, colProfessor);
        tabela.setPrefWidth(700);
        tabela.setItems(disciplinasData);

        return tabela;
    }

    private void atualizarTabelaDisciplinas(TableView<Disciplina> tabela) {
        disciplinasData.setAll(DisciplinaDAO.listarDisciplinas());
        tabela.refresh();
    }
}
