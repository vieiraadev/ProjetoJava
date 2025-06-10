package com.estudomais.demo.view;

import com.estudomais.demo.model.Aluno;
import com.estudomais.demo.model.Disciplina;
import com.estudomais.demo.model.Tarefa;
import com.estudomais.demo.persistence.AlunoDAO;
import com.estudomais.demo.persistence.DisciplinaDAO;
import com.estudomais.demo.persistence.TarefaDAO;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class DetalhesView {

    public void exibir() {
        Stage stage = new Stage();
        stage.setTitle("Detalhes de Disciplinas e Alunos");

        TextField txtBuscaDisciplina = new TextField();
        txtBuscaDisciplina.setPromptText("Ex: INF101");

        Button btnBuscarDisciplina = new Button("Buscar Disciplina");

        TextField txtBuscaAluno = new TextField();
        txtBuscaAluno.setPromptText("Ex: João da Silva");

        Button btnBuscarAluno = new Button("Buscar Aluno");

        TextArea resultado = new TextArea();
        resultado.setEditable(false);
        resultado.setPrefHeight(300);

        btnBuscarDisciplina.setOnAction(e -> {
            String codigo = txtBuscaDisciplina.getText().trim();
            if (codigo.isEmpty()) {
                resultado.setText("Erro: Informe o código da disciplina.");
                return;
            }

            List<Disciplina> disciplinas;
            List<Tarefa> tarefas;
            List<Aluno> alunos;
            try {
                disciplinas = DisciplinaDAO.listarDisciplinas();
                tarefas = TarefaDAO.listarTarefas();
                alunos = AlunoDAO.listarAlunos();
            } catch (IOException ex) {
                resultado.setText("Erro ao carregar dados: " + ex.getMessage());
                return;
            }

            Disciplina encontrada = disciplinas.stream()
                    .filter(d -> d.getCodigo().equalsIgnoreCase(codigo))
                    .findFirst()
                    .orElse(null);

            if (encontrada != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("Disciplina: ").append(encontrada.getNome()).append("\n\n");

                sb.append("Tarefas vinculadas:\n");
                boolean temTarefas = false;
                for (Tarefa t : tarefas) {
                    if (t.getDisciplinaAssociada().equalsIgnoreCase(encontrada.getNome())) {
                        sb.append("- ").append(t.getTitulo()).append(" (Entrega: ")
                                .append(t.getDataEntrega()).append(", Peso: ")
                                .append(t.getPeso()).append(")\n");
                        temTarefas = true;
                    }
                }
                if (!temTarefas) sb.append("Nenhuma tarefa encontrada.\n");

                sb.append("\nAlunos vinculados:\n");
                boolean temAlunos = false;
                for (Aluno a : alunos) {
                    if (a.getDisciplinasVinculadas() != null &&
                            a.getDisciplinasVinculadas().contains(encontrada.getNome())) {
                        sb.append("- ").append(a.getNome()).append(" (").append(a.getEmail()).append(")\n");
                        temAlunos = true;
                    }
                }
                if (!temAlunos) sb.append("Nenhum aluno vinculado.\n");

                resultado.setText(sb.toString());
            } else {
                resultado.setText("Disciplina não encontrada.");
            }
        });

        btnBuscarAluno.setOnAction(e -> {
            String nomeAluno = txtBuscaAluno.getText().trim();
            if (nomeAluno.isEmpty()) {
                resultado.setText("Erro: Informe o nome do aluno.");
                return;
            }

            List<Aluno> alunos;
            try {
                alunos = AlunoDAO.listarAlunos();
            } catch (IOException ex) {
                resultado.setText("Erro ao carregar alunos: " + ex.getMessage());
                return;
            }

            Aluno encontrado = alunos.stream()
                    .filter(a -> a.getNome().equalsIgnoreCase(nomeAluno))
                    .findFirst()
                    .orElse(null);

            if (encontrado != null && encontrado.getDisciplinasVinculadas() != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("Disciplinas vinculadas ao aluno ").append(encontrado.getNome()).append(":\n");
                for (String disc : encontrado.getDisciplinasVinculadas()) {
                    sb.append("- ").append(disc).append("\n");
                }
                resultado.setText(sb.toString());
            } else {
                resultado.setText("Aluno não encontrado.");
            }
        });

        VBox layout = new VBox(12,
                new Label("Buscar por Código da Disciplina:"), txtBuscaDisciplina, btnBuscarDisciplina,
                new Label("Buscar por Nome do Aluno:"), txtBuscaAluno, btnBuscarAluno,
                new Label("Resultado da Busca:"), resultado
        );
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f4faff;");

        Scene scene = new Scene(layout, 700, 600);
        stage.setScene(scene);
        stage.show();
    }
}
