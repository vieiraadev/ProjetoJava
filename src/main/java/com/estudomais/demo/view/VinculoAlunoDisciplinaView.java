package com.estudomais.demo.view;

import com.estudomais.demo.model.Aluno;
import com.estudomais.demo.model.Disciplina;
import com.estudomais.demo.persistence.AlunoDAO;
import com.estudomais.demo.persistence.DisciplinaDAO;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VinculoAlunoDisciplinaView {

    public void exibir() {
        Stage stage = new Stage();
        stage.setTitle("Gerenciar Vínculo Aluno x Disciplinas");

        // Combo de alunos
        ComboBox<String> comboAlunos = new ComboBox<>();
        List<Aluno> alunos = AlunoDAO.listarAlunos();
        for (Aluno a : alunos) {
            comboAlunos.getItems().add(a.getNome());
        }

        // Lista de disciplinas para vincular
        ListView<String> listaDisciplinasVincular = new ListView<>();
        listaDisciplinasVincular.setItems(FXCollections.observableArrayList(
                DisciplinaDAO.listarDisciplinas().stream().map(Disciplina::getNome).toList()
        ));
        listaDisciplinasVincular.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listaDisciplinasVincular.setMaxHeight(120);

        // Lista de disciplinas para remover
        ListView<String> listaDisciplinasRemover = new ListView<>();
        listaDisciplinasRemover.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listaDisciplinasRemover.setMaxHeight(120);

        // Atualizar disciplinas ao trocar o aluno
        comboAlunos.setOnAction(e -> {
            String nomeSelecionado = comboAlunos.getValue();
            Aluno aluno = alunos.stream()
                    .filter(a -> a.getNome().equalsIgnoreCase(nomeSelecionado))
                    .findFirst()
                    .orElse(null);
            if (aluno != null) {
                listaDisciplinasRemover.setItems(FXCollections.observableArrayList(aluno.getDisciplinasVinculadas()));
            }
        });

        Button btnVincular = new Button("Vincular Disciplinas");
        Button btnRemover = new Button("Remover Disciplinas");
        TextArea resultado = new TextArea();
        resultado.setEditable(false);

        // Vincular disciplinas
        btnVincular.setOnAction(e -> {
            String nomeSelecionado = comboAlunos.getValue();
            if (nomeSelecionado == null) {
                resultado.setText("Selecione um aluno.");
                return;
            }

            List<String> disciplinasSelecionadas = new ArrayList<>(listaDisciplinasVincular.getSelectionModel().getSelectedItems());

            for (Aluno a : alunos) {
                if (a.getNome().equalsIgnoreCase(nomeSelecionado)) {
                    List<String> atuais = a.getDisciplinasVinculadas();
                    if (atuais == null) atuais = new ArrayList<>();

                    for (String nova : disciplinasSelecionadas) {
                        if (!atuais.contains(nova)) {
                            atuais.add(nova);
                        }
                    }

                    a.setDisciplinasVinculadas(atuais);
                    try {
                        AlunoDAO.salvarLista(alunos);
                        resultado.setText("Disciplinas vinculadas ao aluno " + a.getNome());
                        listaDisciplinasRemover.setItems(FXCollections.observableArrayList(atuais));
                    } catch (IOException ex) {
                        resultado.setText("Erro ao salvar: " + ex.getMessage());
                    }
                    break;
                }
            }
        });

        // Remover disciplinas
        btnRemover.setOnAction(e -> {
            String nomeSelecionado = comboAlunos.getValue();
            if (nomeSelecionado == null) {
                resultado.setText("Selecione um aluno.");
                return;
            }

            List<String> disciplinasRemover = new ArrayList<>(listaDisciplinasRemover.getSelectionModel().getSelectedItems());

            for (Aluno a : alunos) {
                if (a.getNome().equalsIgnoreCase(nomeSelecionado)) {
                    List<String> atuais = a.getDisciplinasVinculadas();
                    if (atuais != null) {
                        atuais.removeAll(disciplinasRemover);
                        a.setDisciplinasVinculadas(atuais);
                        try {
                            AlunoDAO.salvarLista(alunos);
                            resultado.setText("Disciplinas removidas do aluno " + a.getNome());
                            listaDisciplinasRemover.setItems(FXCollections.observableArrayList(atuais));
                        } catch (IOException ex) {
                            resultado.setText("Erro ao salvar: " + ex.getMessage());
                        }
                    }
                    break;
                }
            }
        });

        VBox layout = new VBox(10,
                new Label("Selecione o Aluno:"), comboAlunos,
                new Label("Disciplinas para Vincular:"), listaDisciplinasVincular, btnVincular,
                new Label("Disciplinas para Remover:"), listaDisciplinasRemover, btnRemover,
                resultado
        );
        layout.setPadding(new Insets(10));

        stage.setScene(new Scene(layout, 520, 600));
        stage.show();
    }
}
