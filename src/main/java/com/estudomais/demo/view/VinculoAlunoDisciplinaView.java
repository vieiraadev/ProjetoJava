package com.estudomais.demo.view;

import com.estudomais.demo.model.Aluno;
import com.estudomais.demo.model.Disciplina;
import com.estudomais.demo.persistence.AlunoDAO;
import com.estudomais.demo.persistence.DisciplinaDAO;
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
        List<Aluno> alunos = new ArrayList<>();
        TextArea resultado = new TextArea();
        resultado.setEditable(false);
        resultado.setPrefHeight(120);

        try {
            alunos = AlunoDAO.listarAlunos();
            for (Aluno a : alunos) {
                comboAlunos.getItems().add(a.getNome());
            }
        } catch (IOException ex) {
            resultado.setText("Erro ao carregar alunos: " + ex.getMessage());
        }

        List<String> disciplinasSelecionadasVincular = new ArrayList<>();
        MenuButton dropdownVincular = new MenuButton("Selecionar Disciplinas para Vincular");

        try {
            for (String nomeDisc : DisciplinaDAO.listarDisciplinas().stream().map(Disciplina::getNome).toList()) {
                CheckMenuItem item = new CheckMenuItem(nomeDisc);
                item.setOnAction(e -> {
                    if (item.isSelected()) {
                        disciplinasSelecionadasVincular.add(nomeDisc);
                    } else {
                        disciplinasSelecionadasVincular.remove(nomeDisc);
                    }
                });
                dropdownVincular.getItems().add(item);
            }
        } catch (IOException ex) {
            resultado.setText("Erro ao carregar disciplinas: " + ex.getMessage());
        }

        List<String> disciplinasSelecionadasRemover = new ArrayList<>();
        MenuButton dropdownRemover = new MenuButton("Selecionar Disciplinas para Remover");

        List<Aluno> finalAlunos = alunos;
        comboAlunos.setOnAction(e -> {
            disciplinasSelecionadasRemover.clear();
            dropdownRemover.getItems().clear();
            String nomeSelecionado = comboAlunos.getValue();
            Aluno aluno = finalAlunos.stream()
                    .filter(a -> a.getNome().equalsIgnoreCase(nomeSelecionado))
                    .findFirst()
                    .orElse(null);
            if (aluno != null) {
                for (String disc : aluno.getDisciplinasVinculadas()) {
                    CheckMenuItem item = new CheckMenuItem(disc);
                    item.setOnAction(evt -> {
                        if (item.isSelected()) {
                            disciplinasSelecionadasRemover.add(disc);
                        } else {
                            disciplinasSelecionadasRemover.remove(disc);
                        }
                    });
                    dropdownRemover.getItems().add(item);
                }
            }
        });

        Button btnVincular = new Button("Vincular Disciplinas");
        Button btnRemover = new Button("Remover Disciplinas");

        List<Aluno> finalAlunosRef = alunos;

        btnVincular.setOnAction(e -> {
            String nomeSelecionado = comboAlunos.getValue();
            if (nomeSelecionado == null) {
                resultado.setText("Erro: Selecione um aluno para vincular.");
                return;
            }

            if (disciplinasSelecionadasVincular.isEmpty()) {
                resultado.setText("Erro: Selecione ao menos uma disciplina para vincular.");
                return;
            }

            for (Aluno a : finalAlunosRef) {
                if (a.getNome().equalsIgnoreCase(nomeSelecionado)) {
                    List<String> atuais = a.getDisciplinasVinculadas();
                    if (atuais == null) atuais = new ArrayList<>();

                    List<String> jaVinculadas = new ArrayList<>();
                    List<String> novasParaVincular = new ArrayList<>();

                    for (String nova : disciplinasSelecionadasVincular) {
                        if (!atuais.contains(nova)) {
                            novasParaVincular.add(nova);
                        } else {
                            jaVinculadas.add(nova);
                        }
                    }

                    if (novasParaVincular.isEmpty()) {
                        resultado.setText("Todas as disciplinas selecionadas já estão vinculadas ao aluno.");
                        return;
                    }

                    atuais.addAll(novasParaVincular);
                    a.setDisciplinasVinculadas(atuais);
                    try {
                        AlunoDAO.salvarLista(finalAlunosRef);
                        resultado.setText("Disciplinas vinculadas ao aluno " + a.getNome() +
                                ". Ignoradas (já vinculadas): " + String.join(", ", jaVinculadas));
                        comboAlunos.getOnAction().handle(null);
                    } catch (IOException ex) {
                        resultado.setText("Erro ao salvar: " + ex.getMessage());
                    }
                    break;
                }
            }
        });

        btnRemover.setOnAction(e -> {
            String nomeSelecionado = comboAlunos.getValue();
            if (nomeSelecionado == null) {
                resultado.setText("Erro: Selecione um aluno para remover disciplinas.");
                return;
            }

            if (disciplinasSelecionadasRemover.isEmpty()) {
                resultado.setText("Erro: Selecione ao menos uma disciplina para remover.");
                return;
            }

            for (Aluno a : finalAlunosRef) {
                if (a.getNome().equalsIgnoreCase(nomeSelecionado)) {
                    List<String> atuais = a.getDisciplinasVinculadas();
                    if (atuais != null) {
                        List<String> realmenteRemovidas = new ArrayList<>();
                        for (String d : disciplinasSelecionadasRemover) {
                            if (atuais.contains(d)) {
                                realmenteRemovidas.add(d);
                            }
                        }
                        if (realmenteRemovidas.isEmpty()) {
                            resultado.setText("Nenhuma das disciplinas selecionadas está vinculada ao aluno.");
                            return;
                        }
                        atuais.removeAll(realmenteRemovidas);
                        a.setDisciplinasVinculadas(atuais);
                        try {
                            AlunoDAO.salvarLista(finalAlunosRef);
                            resultado.setText("Disciplinas removidas do aluno " + a.getNome() + ": " +
                                    String.join(", ", realmenteRemovidas));
                            comboAlunos.getOnAction().handle(null);
                        } catch (IOException ex) {
                            resultado.setText("Erro ao salvar: " + ex.getMessage());
                        }
                    }
                    break;
                }
            }
        });

        VBox layout = new VBox(12,
                new Label("Selecione o Aluno:"), comboAlunos,
                new Label("Disciplinas para Vincular:"), dropdownVincular, btnVincular,
                new Label("Disciplinas para Remover:"), dropdownRemover, btnRemover,
                new Label("Mensagens do Sistema:"), resultado
        );
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f4faff;");

        Scene scene = new Scene(layout, 650, 600);
        stage.setScene(scene);
        stage.show();
    }
}
