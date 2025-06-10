package com.estudomais.demo.persistence;

import com.estudomais.demo.model.Tarefa;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TarefaDAO {
    private static final String FILE_NAME = "data/tarefas.dat";

    public static void salvarTarefa(Tarefa nova) throws IOException {
        if (!arquivoExiste()) {
            throw new IOException("Arquivo de dados 'tarefas.dat' não encontrado. Operação cancelada.");
        }

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

    public static List<Tarefa> listarTarefas() throws IOException {
        if (!arquivoExiste()) {
            throw new IOException("Arquivo de dados 'tarefas.dat' não encontrado. Operação cancelada.");
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (List<Tarefa>) ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new IOException("Erro ao ler o arquivo de tarefas: " + e.getMessage(), e);
        }
    }

    public static void salvarLista(List<Tarefa> lista) throws IOException {
        if (!arquivoExiste()) {
            throw new IOException("Arquivo de dados 'tarefas.dat' não encontrado. Operação cancelada.");
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(lista);
        }
    }

    public static void removerPorTitulo(String titulo) throws IOException {
        if (!arquivoExiste()) {
            throw new IOException("Arquivo de dados 'tarefas.dat' não encontrado. Operação cancelada.");
        }

        List<Tarefa> lista = listarTarefas();
        boolean removido = lista.removeIf(t -> t.getTitulo().equalsIgnoreCase(titulo));

        if (!removido) {
            throw new IOException("Nenhuma tarefa com o título '" + titulo + "' foi encontrada para remoção.");
        }

        salvarLista(lista);
    }

    // Verifica se o arquivo existe
    private static boolean arquivoExiste() {
        File arquivo = new File(FILE_NAME);
        return arquivo.exists();
    }
}
