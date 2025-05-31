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

import java.util.List;

public class DetalhesView {

    public void exibir() {
        Stage stage = new Stage();
        stage.setTitle("Ver Detalhes");

        TextField txtBuscaDisciplina = new TextField();
        Button btnBuscarDisciplina = new Button("Buscar por Código da Disciplina");

        TextField txtBuscaAluno = new TextField();
        Button btnBuscarAluno = new Button("Buscar por Nome do Aluno");

        TextArea resultado = new TextArea();
        resultado.setEditable(false);

        btnBuscarDisciplina.setOnAction(e -> {
            String codigo = txtBuscaDisciplina.getText().trim();
            List<Disciplina> disciplinas = DisciplinaDAO.listarDisciplinas();
            List<Tarefa> tarefas = TarefaDAO.listarTarefas();
            List<Aluno> alunos = AlunoDAO.listarAlunos();

            Disciplina encontrada = disciplinas.stream()
                    .filter(d -> d.getCodigo().equalsIgnoreCase(codigo))
                    .findFirst()
                    .orElse(null);

            if (encontrada != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("Disciplina: ").append(encontrada.getNome()).append("\n\n");

                sb.append("Tarefas vinculadas:\n");
                for (Tarefa t : tarefas) {
                    if (t.getDisciplinaAssociada().equalsIgnoreCase(encontrada.getNome())) {
                        sb.append("- ").append(t.getTitulo()).append(" (Entrega: ")
                                .append(t.getDataEntrega()).append(", Peso: ")
                                .append(t.getPeso()).append(")\n");
                    }
                }

                sb.append("\nAlunos vinculados:\n");
                for (Aluno a : alunos) {
                    if (a.getDisciplinasVinculadas() != null &&
                            a.getDisciplinasVinculadas().contains(encontrada.getNome())) {
                        sb.append("- ").append(a.getNome()).append(" (").append(a.getEmail()).append(")\n");
                    }
                }

                resultado.setText(sb.toString());
            } else {
                resultado.setText("Disciplina não encontrada.");
            }
        });

        btnBuscarAluno.setOnAction(e -> {
            String nomeAluno = txtBuscaAluno.getText().trim();
            List<Aluno> alunos = AlunoDAO.listarAlunos();

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
                resultado.setText("Aluno não encontrado ou sem disciplinas vinculadas.");
            }
        });

        VBox layout = new VBox(10,
                new Label("Buscar por Código da Disciplina:"), txtBuscaDisciplina, btnBuscarDisciplina,
                new Label("Buscar por Nome do Aluno:"), txtBuscaAluno, btnBuscarAluno,
                resultado
        );
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 500, 500);
        stage.setScene(scene);
        stage.show();
    }
}
