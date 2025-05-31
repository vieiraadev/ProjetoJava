package com.estudomais.demo.view;

import com.estudomais.demo.model.Disciplina;
import com.estudomais.demo.persistence.DisciplinaDAO;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class DisciplinaView {

    private boolean modoEdicao = false;

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
        atualizarCombo(comboRemover);

        ComboBox<String> comboEditar = new ComboBox<>();
        comboEditar.setPromptText("Selecione a disciplina para editar");
        atualizarCombo(comboEditar);

        Button btnSalvar = new Button("Salvar");
        Button btnListar = new Button("Listar");
        Button btnRemover = new Button("Remover");
        Button btnEditar = new Button("Editar");

        TextArea txtArea = new TextArea();
        txtArea.setEditable(false);

        btnSalvar.setOnAction(e -> {
            String nome = txtNome.getText().trim();
            String codigo = txtCodigo.getText().trim();
            String carga = txtCargaHoraria.getText().trim();
            String semestre = txtSemestre.getText().trim();
            String professor = txtProfessor.getText().trim();

            if (nome.isEmpty() || codigo.isEmpty() || carga.isEmpty() || semestre.isEmpty()) {
                txtArea.setText("Erro: Todos os campos obrigatórios devem ser preenchidos.");
                return;
            }

            try {
                int cargaInt = Integer.parseInt(carga);

                List<Disciplina> existentes = DisciplinaDAO.listarDisciplinas();
                boolean codigoDuplicado = existentes.stream()
                        .anyMatch(d -> d.getCodigo().equalsIgnoreCase(codigo) &&
                                (!modoEdicao || !d.getNome().equalsIgnoreCase(nome)));

                if (codigoDuplicado) {
                    txtArea.setText("Erro: Já existe uma disciplina com esse código.");
                    return;
                }

                Disciplina disciplina = new Disciplina(nome, codigo, cargaInt, semestre, professor);
                DisciplinaDAO.salvarDisciplina(disciplina);
                txtArea.setText(modoEdicao ? "Disciplina atualizada com sucesso!" : "Disciplina salva com sucesso!");

                atualizarCombo(comboRemover);
                atualizarCombo(comboEditar);
                txtCodigo.setDisable(false);
                modoEdicao = false;
            } catch (NumberFormatException ex) {
                txtArea.setText("Erro: Carga horária deve ser um número inteiro.");
            } catch (IOException ex) {
                txtArea.setText("Erro ao salvar: " + ex.getMessage());
            }
        });

        btnListar.setOnAction(e -> {
            var lista = DisciplinaDAO.listarDisciplinas();
            StringBuilder sb = new StringBuilder();
            for (Disciplina d : lista) {
                sb.append("Nome: ").append(d.getNome()).append("\n");
                sb.append("Código: ").append(d.getCodigo()).append("\n");
                sb.append("Carga Horária: ").append(d.getCargaHoraria()).append("\n");
                sb.append("Semestre: ").append(d.getSemestre()).append("\n");
                sb.append("Professor: ").append(d.getProfessorResponsavel()).append("\n");
                sb.append("--------------------------\n");
            }
            txtArea.setText(sb.toString());
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
                } else {
                    txtArea.setText("Erro: disciplina não encontrada.");
                }
            } catch (IOException ex) {
                txtArea.setText("Erro ao remover: " + ex.getMessage());
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
                txtCodigo.setDisable(true); // impede alteração do código
                txtCargaHoraria.setText(String.valueOf(disciplina.getCargaHoraria()));
                txtSemestre.setText(disciplina.getSemestre());
                txtProfessor.setText(disciplina.getProfessorResponsavel());
                modoEdicao = true;
                txtArea.setText("Preencha os dados e clique em salvar para atualizar.");
            } else {
                txtArea.setText("Erro: disciplina não encontrada.");
            }
        });

        HBox botoes = new HBox(10, btnSalvar, btnListar);
        VBox layout = new VBox(10,
                new Label("Nome da Disciplina:"), txtNome,
                new Label("Código da Disciplina:"), txtCodigo,
                new Label("Carga Horária:"), txtCargaHoraria,
                new Label("Semestre ou Período:"), txtSemestre,
                new Label("Professor Responsável:"), txtProfessor,
                botoes,
                new Label("Remover Disciplina:"), comboRemover, btnRemover,
                new Label("Editar Disciplina:"), comboEditar, btnEditar,
                txtArea
        );
        layout.setPadding(new Insets(10));

        stage.setScene(new Scene(layout, 500, 700));
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
}
