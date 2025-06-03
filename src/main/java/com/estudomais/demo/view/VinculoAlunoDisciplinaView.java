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

        ComboBox<String> comboAlunos = new ComboBox<>();
        List<Aluno> alunos = AlunoDAO.listarAlunos();
        for (Aluno a : alunos) {
            comboAlunos.getItems().add(a.getNome());
        }

        ListView<String> listaDisciplinas = new ListView<>();
        listaDisciplinas.setItems(FXCollections.observableArrayList(
                DisciplinaDAO.listarDisciplinas().stream().map(Disciplina::getNome).toList()
        ));
        listaDisciplinas.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listaDisciplinas.setMaxHeight(150);

        Button btnSalvar = new Button("Salvar Vínculo");
        TextArea resultado = new TextArea();
        resultado.setEditable(false);

        btnSalvar.setOnAction(e -> {
            String nomeSelecionado = comboAlunos.getValue();
            if (nomeSelecionado == null) {
                resultado.setText("Selecione um aluno.");
                return;
            }

            List<String> disciplinasSelecionadas = new ArrayList<>(listaDisciplinas.getSelectionModel().getSelectedItems());

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
                        resultado.setText("Disciplinas atualizadas para o aluno " + a.getNome());
                    } catch (IOException ex) {
                        resultado.setText("Erro ao salvar: " + ex.getMessage());
                    }
                    break;
                }
            }
        });

        VBox layout = new VBox(10,
                new Label("Selecione o Aluno:"), comboAlunos,
                new Label("Selecione as Disciplinas:"), listaDisciplinas,
                btnSalvar, resultado
        );
        layout.setPadding(new Insets(10));

        stage.setScene(new Scene(layout, 500, 450));
        stage.show();
    }
}
