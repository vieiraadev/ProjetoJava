package com.estudomais.demo.persistence;

import com.estudomais.demo.model.Aluno;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AlunoDAO {
    private static final String FILE_NAME = "data/alunos.dat";

    public static void salvarAluno(Aluno aluno) throws IOException {
        if (!arquivoExiste()) {
            throw new IOException("Arquivo de dados 'alunos.dat' não encontrado. Operação cancelada.");
        }

        List<Aluno> lista = listarAlunos();
        lista.add(aluno);
        salvarLista(lista);
    }

    public static List<Aluno> listarAlunos() throws IOException {
        if (!arquivoExiste()) {
            throw new IOException("Arquivo de dados 'alunos.dat' não encontrado. Operação cancelada.");
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (List<Aluno>) ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new IOException("Erro ao ler o arquivo de alunos: " + e.getMessage(), e);
        }
    }

    public static void salvarLista(List<Aluno> lista) throws IOException {
        if (!arquivoExiste()) {
            throw new IOException("Arquivo de dados 'alunos.dat' não encontrado. Operação cancelada.");
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(lista);
        }
    }

    public static void removerAlunoPorEmail(String email) throws IOException {
        if (!arquivoExiste()) {
            throw new IOException("Arquivo de dados 'alunos.dat' não encontrado. Operação cancelada.");
        }

        List<Aluno> lista = listarAlunos();
        boolean removido = lista.removeIf(a -> a.getEmail().equalsIgnoreCase(email));

        if (!removido) {
            throw new IOException("Aluno com email " + email + " não encontrado para remoção.");
        }

        salvarLista(lista);
    }

    public static void atualizarAluno(Aluno alunoAtualizado) throws IOException {
        if (!arquivoExiste()) {
            throw new IOException("Arquivo de dados 'alunos.dat' não encontrado. Operação cancelada.");
        }

        List<Aluno> lista = listarAlunos();
        boolean encontrado = false;

        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getEmail().equalsIgnoreCase(alunoAtualizado.getEmail())) {
                lista.set(i, alunoAtualizado);
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            throw new IOException("Aluno com email " + alunoAtualizado.getEmail() + " não encontrado para atualização.");
        }

        salvarLista(lista);
    }

    // Verifica se o arquivo existe
    private static boolean arquivoExiste() {
        File arquivo = new File(FILE_NAME);
        return arquivo.exists();
    }
}
