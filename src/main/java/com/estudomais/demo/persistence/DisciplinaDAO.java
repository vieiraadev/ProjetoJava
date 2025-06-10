package com.estudomais.demo.persistence;

import com.estudomais.demo.model.Disciplina;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DisciplinaDAO {
    private static final String FILE_NAME = "data/disciplinas.dat";

    public static void salvarDisciplina(Disciplina nova) throws IOException {
        if (!arquivoExiste()) {
            throw new IOException("Arquivo de dados 'disciplinas.dat' não encontrado. Operação cancelada.");
        }

        List<Disciplina> lista = listarDisciplinas();

        // Verifica se já existe uma disciplina com o mesmo código e substitui
        boolean atualizada = false;
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getCodigo().equalsIgnoreCase(nova.getCodigo())) {
                lista.set(i, nova);
                atualizada = true;
                break;
            }
        }

        // Se não encontrou código igual, adiciona como nova
        if (!atualizada) {
            lista.add(nova);
        }

        salvarLista(lista);
    }

    public static List<Disciplina> listarDisciplinas() throws IOException {
        if (!arquivoExiste()) {
            throw new IOException("Arquivo de dados 'disciplinas.dat' não encontrado. Operação cancelada.");
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (List<Disciplina>) ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new IOException("Erro ao ler o arquivo de disciplinas: " + e.getMessage(), e);
        }
    }

    public static void salvarLista(List<Disciplina> lista) throws IOException {
        if (!arquivoExiste()) {
            throw new IOException("Arquivo de dados 'disciplinas.dat' não encontrado. Operação cancelada.");
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(lista);
        }
    }

    public static void removerPorCodigo(String codigo) throws IOException {
        if (!arquivoExiste()) {
            throw new IOException("Arquivo de dados 'disciplinas.dat' não encontrado. Operação cancelada.");
        }

        List<Disciplina> lista = listarDisciplinas();
        boolean removido = lista.removeIf(d -> d.getCodigo().equalsIgnoreCase(codigo));

        if (!removido) {
            throw new IOException("Nenhuma disciplina com o código '" + codigo + "' foi encontrada para remoção.");
        }

        salvarLista(lista);
    }

    // Verifica se o arquivo existe
    private static boolean arquivoExiste() {
        File arquivo = new File(FILE_NAME);
        return arquivo.exists();
    }
}
