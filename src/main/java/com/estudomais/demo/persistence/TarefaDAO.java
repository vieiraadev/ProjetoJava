package com.estudomais.demo.persistence;

import com.estudomais.demo.model.Tarefa;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TarefaDAO {
    private static final String FILE_NAME = "tarefas.dat";

    public static void salvarTarefa(Tarefa nova) throws IOException {
        List<Tarefa> lista = listarTarefas();

        boolean atualizada = false;
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getTitulo().equalsIgnoreCase(nova.getTitulo())) {
                lista.set(i, nova);
                atualizada = true;
                break;
            }
        }

        if (!atualizada) {
            lista.add(nova);
        }

        salvarLista(lista);
    }

    public static List<Tarefa> listarTarefas() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (List<Tarefa>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void salvarLista(List<Tarefa> lista) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(lista);
        }
    }

    public static void removerPorTitulo(String titulo) throws IOException {
        List<Tarefa> lista = listarTarefas();
        lista.removeIf(t -> t.getTitulo().equalsIgnoreCase(titulo));
        salvarLista(lista);
    }
}
