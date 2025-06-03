package com.estudomais.demo.view;

import com.estudomais.demo.model.Disciplina;
import com.estudomais.demo.model.Tarefa;
import com.estudomais.demo.persistence.DisciplinaDAO;
import com.estudomais.demo.persistence.TarefaDAO;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class TarefaView {

    public void exibir() {
        Stage stage = new Stage();
        stage.setTitle("Cadastro de Tarefa");

        TextField txtTitulo = new TextField();
        TextArea txtDescricao = new TextArea();
        DatePicker dateEntrega = new DatePicker();
        TextField txtPeso = new TextField();

        ComboBox<String> comboDisciplinas = new ComboBox<>();
        comboDisciplinas.setItems(FXCollections.observableArrayList(
                DisciplinaDAO.listarDisciplinas()
                        .stream()
                        .map(Disciplina::getNome)
                        .collect(Collectors.toList())
        ));

        ComboBox<String> comboRemover = new ComboBox<>();
        comboRemover.setPromptText("Selecione a tarefa para remover");
        atualizarCombo(comboRemover);

        ComboBox<String> comboEditar = new ComboBox<>();
        comboEditar.setPromptText("Selecione a tarefa para editar");
        atualizarCombo(comboEditar);

        Button btnSalvar = new Button("Salvar");
        Button btnListar = new Button("Listar");
        Button btnRemover = new Button("Remover");
        Button btnEditar = new Button("Editar");

        TextArea txtArea = new TextArea();
        txtArea.setEditable(false);

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
            } catch (IOException ex) {
                txtArea.setText("Erro ao salvar: " + ex.getMessage());
            }
        });

        btnListar.setOnAction(e -> {
            var lista = TarefaDAO.listarTarefas();
            StringBuilder sb = new StringBuilder();
            for (Tarefa t : lista) {
                sb.append("Título: ").append(t.getTitulo()).append("\n");
                sb.append("Descrição: ").append(t.getDescricao()).append("\n");
                sb.append("Entrega: ").append(t.getDataEntrega()).append("\n");
                sb.append("Disciplina: ").append(t.getDisciplinaAssociada()).append("\n");
                sb.append("Peso: ").append(t.getPeso()).append("\n");
                sb.append("--------------------------\n");
            }
            txtArea.setText(sb.toString());
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
                    .findFirst()
                    .orElse(null);

            if (tarefa != null) {
                txtTitulo.setText(tarefa.getTitulo());
                txtTitulo.setDisable(true); // título é chave primária, não alterável
                txtDescricao.setText(tarefa.getDescricao());
                dateEntrega.setValue(tarefa.getDataEntrega());
                comboDisciplinas.setValue(tarefa.getDisciplinaAssociada());
                txtPeso.setText(String.valueOf(tarefa.getPeso()));
                txtArea.setText("Altere os campos desejados e clique em 'Salvar' para atualizar.");
            } else {
                txtArea.setText("Erro: tarefa não encontrada.");
            }
        });

        HBox botoes = new HBox(10, btnSalvar, btnListar);
        VBox layout = new VBox(10,
                new Label("Título da Tarefa:"), txtTitulo,
                new Label("Descrição:"), txtDescricao,
                new Label("Data de Entrega:"), dateEntrega,
                new Label("Disciplina Associada:"), comboDisciplinas,
                new Label("Peso da Tarefa:"), txtPeso,
                botoes,
                new Label("Remover Tarefa:"), comboRemover, btnRemover,
                new Label("Editar Tarefa:"), comboEditar, btnEditar,
                txtArea
        );
        layout.setPadding(new Insets(10));

        stage.setScene(new Scene(layout, 500, 750));
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
}
