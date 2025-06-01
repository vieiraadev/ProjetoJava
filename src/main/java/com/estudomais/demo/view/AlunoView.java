package com.estudomais.demo.view;

import com.estudomais.demo.model.Aluno;
import com.estudomais.demo.persistence.AlunoDAO;
import com.estudomais.demo.persistence.DisciplinaDAO;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AlunoView {

    public void exibir() {
        Stage stage = new Stage();
        stage.setTitle("Cadastro de Aluno");

        TextField txtNome = new TextField();
        TextField txtEmail = new TextField();
        TextField txtCurso = new TextField();
        TextField txtPeriodo = new TextField();
        TextField txtFaculdade = new TextField();

        ComboBox<String> comboDisciplina = new ComboBox<>();
        comboDisciplina.setPromptText("Selecione uma disciplina");
        comboDisciplina.setItems(FXCollections.observableArrayList(
                DisciplinaDAO.listarDisciplinas().stream()
                        .map(d -> d.getNome())
                        .collect(Collectors.toList())
        ));

        ComboBox<String> comboRemoverAluno = new ComboBox<>();
        comboRemoverAluno.setPromptText("Selecione o aluno para remover");
        atualizarComboAlunos(comboRemoverAluno);

        ComboBox<String> comboEditarAluno = new ComboBox<>();
        comboEditarAluno.setPromptText("Selecione o aluno para editar");
        atualizarComboAlunos(comboEditarAluno);

        Button btnSalvar = new Button("Salvar");
        Button btnListar = new Button("Listar");
        Button btnRemover = new Button("Remover Aluno");
        Button btnEditar = new Button("Editar Aluno");

        TextArea txtArea = new TextArea();
        txtArea.setEditable(false);

        btnSalvar.setOnAction(e -> {
            String nome = txtNome.getText().trim();
            String email = txtEmail.getText().trim();
            String curso = txtCurso.getText().trim();
            String periodo = txtPeriodo.getText().trim();
            String faculdade = txtFaculdade.getText().trim();
            String disciplinaSelecionada = comboDisciplina.getValue();

            List<String> disciplinas = new ArrayList<>();
            if (disciplinaSelecionada != null) {
                disciplinas.add(disciplinaSelecionada);
            }

            if (nome.isEmpty() || email.isEmpty() || curso.isEmpty() || periodo.isEmpty() || faculdade.isEmpty()) {
                txtArea.setText("Erro: Todos os campos devem ser preenchidos.");
                return;
            }

            if (!email.contains("@") || !email.contains(".")) {
                txtArea.setText("Erro: E-mail inválido.");
                return;
            }

            List<Aluno> existentes = AlunoDAO.listarAlunos();
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
            } catch (IOException ex) {
                txtArea.setText("Erro ao salvar: " + ex.getMessage());
            }
        });

        btnListar.setOnAction(e -> {
            var alunos = AlunoDAO.listarAlunos();
            StringBuilder sb = new StringBuilder();
            for (Aluno a : alunos) {
                sb.append("Nome: ").append(a.getNome()).append("\n");
                sb.append("Email: ").append(a.getEmail()).append("\n");
                sb.append("Curso: ").append(a.getCurso()).append("\n");
                sb.append("Período: ").append(a.getPeriodo()).append("\n");
                sb.append("Faculdade: ").append(a.getFaculdade()).append("\n");
                sb.append("Disciplinas Vinculadas: ").append(a.getDisciplinasVinculadas()).append("\n");
                sb.append("--------------------------\n");
            }
            txtArea.setText(sb.toString());
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
                } else {
                    txtArea.setText("Erro: aluno não encontrado.");
                }
            } catch (IOException ex) {
                txtArea.setText("Erro ao remover: " + ex.getMessage());
            }
        });

        btnEditar.setOnAction(e -> {
            String nomeSelecionado = comboEditarAluno.getValue();
            if (nomeSelecionado == null) {
                txtArea.setText("Por favor, selecione um aluno para editar.");
                return;
            }

            List<Aluno> alunos = AlunoDAO.listarAlunos();
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

                if (!alunoSelecionado.getDisciplinasVinculadas().isEmpty()) {
                    comboDisciplina.setValue(alunoSelecionado.getDisciplinasVinculadas().get(0));
                }

                try {
                    AlunoDAO.removerAlunoPorEmail(alunoSelecionado.getEmail());
                    txtArea.setText("Aluno carregado para edição. Altere os dados e clique em Salvar.");
                } catch (IOException ex) {
                    txtArea.setText("Erro ao preparar para edição: " + ex.getMessage());
                }
            } else {
                txtArea.setText("Aluno não encontrado para edição.");
            }
        });

        HBox botoes = new HBox(10, btnSalvar, btnListar);
        VBox layout = new VBox(10,
                new Label("Nome:"), txtNome,
                new Label("E-mail:"), txtEmail,
                new Label("Curso:"), txtCurso,
                new Label("Período:"), txtPeriodo,
                new Label("Faculdade:"), txtFaculdade,
                new Label("Disciplina Vinculada:"), comboDisciplina,
                botoes,
                new Label("Remover Aluno:"), comboRemoverAluno, btnRemover,
                new Label("Editar Aluno:"), comboEditarAluno, btnEditar,
                txtArea
        );
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 520, 750);
        stage.setScene(scene);
        stage.show();
    }

    private void atualizarComboAlunos(ComboBox<String> combo) {
        combo.getItems().setAll(
                AlunoDAO.listarAlunos().stream()
                        .map(Aluno::getNome)
                        .collect(Collectors.toList())
        );
        combo.setValue(null);
    }
}
