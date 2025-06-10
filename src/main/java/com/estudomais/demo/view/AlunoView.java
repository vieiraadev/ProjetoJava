package com.estudomais.demo.view;

import com.estudomais.demo.model.Aluno;
import com.estudomais.demo.persistence.AlunoDAO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AlunoView {

    private ObservableList<Aluno> alunosData = FXCollections.observableArrayList();

    public void exibir() {
        Stage stage = new Stage();
        stage.setTitle("Cadastro de Aluno");

        TextField txtNome = new TextField();
        TextField txtEmail = new TextField();
        TextField txtCurso = new TextField();
        TextField txtPeriodo = new TextField();
        TextField txtFaculdade = new TextField();

        List<String> disciplinasSelecionadas = new ArrayList<>();
        MenuButton dropdownDisciplinas = new MenuButton("Selecionar Disciplinas");

        TextArea txtArea = new TextArea();
        txtArea.setEditable(false);
        txtArea.setPrefHeight(100);

        try {
            for (String nomeDisc : DisciplinaDAO.listarDisciplinas().stream().map(d -> d.getNome()).collect(Collectors.toList())) {
                CheckMenuItem item = new CheckMenuItem(nomeDisc);
                item.setOnAction(e -> {
                    if (item.isSelected()) {
                        disciplinasSelecionadas.add(nomeDisc);
                    } else {
                        disciplinasSelecionadas.remove(nomeDisc);
                    }
                });
                dropdownDisciplinas.getItems().add(item);
            }
        } catch (Exception ex) {
            dropdownDisciplinas.setDisable(true);
            txtArea.setText("Erro ao carregar disciplinas: " + ex.getMessage());
        }

        ComboBox<String> comboRemoverAluno = new ComboBox<>();
        ComboBox<String> comboEditarAluno = new ComboBox<>();
        atualizarComboAlunos(comboRemoverAluno);
        atualizarComboAlunos(comboEditarAluno);

        Button btnSalvar = new Button("Salvar");
        Button btnRemover = new Button("Remover Aluno");
        Button btnEditar = new Button("Editar Aluno");

        TableView<Aluno> tabelaAlunos = criarTabelaAlunos();
        atualizarTabelaAlunos(tabelaAlunos);

        btnSalvar.setOnAction(e -> {
            String nome = txtNome.getText().trim();
            String email = txtEmail.getText().trim();
            String curso = txtCurso.getText().trim();
            String periodo = txtPeriodo.getText().trim();
            String faculdade = txtFaculdade.getText().trim();
            List<String> disciplinas = new ArrayList<>(disciplinasSelecionadas);

            if (nome.isEmpty() || email.isEmpty() || curso.isEmpty() || periodo.isEmpty() || faculdade.isEmpty()) {
                txtArea.setText("Erro: Todos os campos devem ser preenchidos.");
                return;
            }

            if (!email.contains("@") || !email.contains(".")) {
                txtArea.setText("Erro: E-mail inválido.");
                return;
            }

            if (!curso.chars().allMatch(c -> Character.isLetter(c) || Character.isWhitespace(c))) {
                txtArea.setText("Erro: O campo 'Curso' só pode conter letras.");
                return;
            }

            if (!faculdade.chars().allMatch(c -> Character.isLetter(c) || Character.isWhitespace(c))) {
                txtArea.setText("Erro: O campo 'Faculdade' só pode conter letras.");
                return;
            }

            int periodoNum;
            try {
                periodoNum = Integer.parseInt(periodo);
                if (periodoNum < 1 || periodoNum > 12) {
                    txtArea.setText("Erro: O período deve ser um número entre 1 e 12.");
                    return;
                }
            } catch (NumberFormatException ex1) {
                txtArea.setText("Erro: O período deve ser um número válido.");
                return;
            }

            if (disciplinas.isEmpty()) {
                txtArea.setText("Erro: Selecione pelo menos uma disciplina.");
                return;
            }

            List<Aluno> existentes;
            try {
                existentes = AlunoDAO.listarAlunos();
            } catch (IOException ex2) {
                txtArea.setText("Erro ao verificar alunos existentes: " + ex2.getMessage());
                return;
            }

            boolean nomeDuplicado = existentes.stream().anyMatch(a -> a.getNome().equalsIgnoreCase(nome));
            boolean emailDuplicado = existentes.stream().anyMatch(a -> a.getEmail().equalsIgnoreCase(email));

            if (nomeDuplicado) {
                txtArea.setText("Erro: Já existe um aluno com esse nome.");
                return;
            }

            if (emailDuplicado) {
                txtArea.setText("Erro: Já existe um aluno com esse e-mail.");
                return;
            }

            try {
                Aluno aluno = new Aluno(nome, email, curso, periodo, faculdade, disciplinas);
                AlunoDAO.salvarAluno(aluno);
                txtArea.setText("Aluno salvo com sucesso!");
                atualizarComboAlunos(comboRemoverAluno);
                atualizarComboAlunos(comboEditarAluno);
                atualizarTabelaAlunos(tabelaAlunos);
            } catch (IOException ex3) {
                txtArea.setText("Erro ao salvar: " + ex3.getMessage());
            }
        });

        btnRemover.setOnAction(e -> {
            String nomeSelecionado = comboRemoverAluno.getValue();
            if (nomeSelecionado == null) {
                txtArea.setText("Por favor, selecione um aluno para remover.");
                return;
            }

            try {
                List<Aluno> alunos = AlunoDAO.listarAlunos();
                Aluno alunoRemover = alunos.stream()
                        .filter(a -> a.getNome().equalsIgnoreCase(nomeSelecionado))
                        .findFirst()
                        .orElse(null);

                if (alunoRemover != null) {
                    AlunoDAO.removerAlunoPorEmail(alunoRemover.getEmail());
                    txtArea.setText("Aluno removido com sucesso!");
                    atualizarComboAlunos(comboRemoverAluno);
                    atualizarComboAlunos(comboEditarAluno);
                    atualizarTabelaAlunos(tabelaAlunos);
                } else {
                    txtArea.setText("Erro: aluno não encontrado.");
                }
            } catch (IOException ex4) {
                txtArea.setText("Erro ao remover: " + ex4.getMessage());
            }
        });

        btnEditar.setOnAction(e -> {
            String nomeSelecionado = comboEditarAluno.getValue();
            if (nomeSelecionado == null) {
                txtArea.setText("Por favor, selecione um aluno para editar.");
                return;
            }

            List<Aluno> alunos;
            try {
                alunos = AlunoDAO.listarAlunos();
            } catch (IOException ex) {
                txtArea.setText("Erro ao buscar alunos: " + ex.getMessage());
                return;
            }

            Aluno alunoSelecionado = alunos.stream()
                    .filter(a -> a.getNome().equalsIgnoreCase(nomeSelecionado))
                    .findFirst()
                    .orElse(null);

            if (alunoSelecionado != null) {
                txtNome.setText(alunoSelecionado.getNome());
                txtEmail.setText(alunoSelecionado.getEmail());
                txtCurso.setText(alunoSelecionado.getCurso());
                txtPeriodo.setText(alunoSelecionado.getPeriodo());
                txtFaculdade.setText(alunoSelecionado.getFaculdade());

                disciplinasSelecionadas.clear();
                for (MenuItem mi : dropdownDisciplinas.getItems()) {
                    if (mi instanceof CheckMenuItem checkItem) {
                        boolean selected = alunoSelecionado.getDisciplinasVinculadas().contains(checkItem.getText());
                        checkItem.setSelected(selected);
                        if (selected) {
                            disciplinasSelecionadas.add(checkItem.getText());
                        }
                    }
                }

                try {
                    AlunoDAO.removerAlunoPorEmail(alunoSelecionado.getEmail());
                    txtArea.setText("Aluno carregado para edição. Altere os dados e clique em Salvar.");
                    atualizarComboAlunos(comboRemoverAluno);
                    atualizarComboAlunos(comboEditarAluno);
                    atualizarTabelaAlunos(tabelaAlunos);
                } catch (IOException ex) {
                    txtArea.setText("Erro ao preparar para edição: " + ex.getMessage());
                }
            } else {
                txtArea.setText("Aluno não encontrado para edição.");
            }
        });

        VBox formCadastro = new VBox(8,
                new Label("Nome:"), txtNome,
                new Label("E-mail:"), txtEmail,
                new Label("Curso:"), txtCurso,
                new Label("Período:"), txtPeriodo,
                new Label("Faculdade:"), txtFaculdade,
                new Label("Disciplinas Vinculadas:"), dropdownDisciplinas,
                btnSalvar,
                new Label("Remover Aluno:"), comboRemoverAluno, btnRemover,
                new Label("Editar Aluno:"), comboEditarAluno, btnEditar,
                new Label("Mensagens do sistema:"), txtArea
        );
        formCadastro.setPadding(new Insets(10));
        formCadastro.setStyle("-fx-background-color: #eef6fb; -fx-border-color: #ccc; -fx-border-radius: 8;");

        HBox layoutPrincipal = new HBox(20, formCadastro, tabelaAlunos);
        layoutPrincipal.setPadding(new Insets(20));

        Scene scene = new Scene(layoutPrincipal, 1000, 700);
        stage.setScene(scene);
        stage.show();
    }

    private void atualizarComboAlunos(ComboBox<String> combo) {
        try {
            combo.getItems().setAll(
                    AlunoDAO.listarAlunos().stream()
                            .map(Aluno::getNome)
                            .collect(Collectors.toList())
            );
            combo.setValue(null);
        } catch (IOException e) {
            combo.getItems().clear();
            System.out.println("Erro ao carregar alunos para o combo: " + e.getMessage());
        }
    }

    private TableView<Aluno> criarTabelaAlunos() {
        TableView<Aluno> tabela = new TableView<>();

        TableColumn<Aluno, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<Aluno, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Aluno, String> colCurso = new TableColumn<>("Curso");
        colCurso.setCellValueFactory(new PropertyValueFactory<>("curso"));

        TableColumn<Aluno, String> colPeriodo = new TableColumn<>("Período");
        colPeriodo.setCellValueFactory(new PropertyValueFactory<>("periodo"));

        TableColumn<Aluno, String> colFaculdade = new TableColumn<>("Faculdade");
        colFaculdade.setCellValueFactory(new PropertyValueFactory<>("faculdade"));

        TableColumn<Aluno, String> colDisciplinas = new TableColumn<>("Disciplinas");
        colDisciplinas.setCellValueFactory(cellData -> {
            List<String> disciplinas = cellData.getValue().getDisciplinasVinculadas();
            String disciplinasStr = String.join(", ", disciplinas);
            return new javafx.beans.property.SimpleStringProperty(disciplinasStr);
        });

        tabela.getColumns().addAll(colNome, colEmail, colCurso, colPeriodo, colFaculdade, colDisciplinas);
        tabela.setPrefWidth(700);
        tabela.setItems(alunosData);

        return tabela;
    }

    private void atualizarTabelaAlunos(TableView<Aluno> tabela) {
        try {
            alunosData.setAll(AlunoDAO.listarAlunos());
            tabela.refresh();
        } catch (IOException e) {
            alunosData.clear();
            tabela.refresh();
            System.out.println("Erro ao atualizar tabela: " + e.getMessage());
        }
    }
}
