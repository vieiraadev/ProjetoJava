package com.estudomais.demo.persistence;

import com.estudomais.demo.model.Aluno;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AlunoDAO {
    private static final String FILE_NAME = "data/alunos.dat";

    public static void salvarAluno(Aluno aluno) throws IOException {
        List<Aluno> lista = listarAlunos();
        lista.add(aluno);
        salvarLista(lista);
    }

    public static List<Aluno> listarAlunos() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (List<Aluno>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void salvarLista(List<Aluno> lista) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(lista);
        }
    }

    public static void removerAlunoPorEmail(String email) throws IOException {
        List<Aluno> lista = listarAlunos();
        lista.removeIf(a -> a.getEmail().equalsIgnoreCase(email));
        salvarLista(lista);
    }

    public static void atualizarAluno(Aluno alunoAtualizado) throws IOException {
        List<Aluno> lista = listarAlunos();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getEmail().equalsIgnoreCase(alunoAtualizado.getEmail())) {
                lista.set(i, alunoAtualizado);
                break;
            }
        }
        salvarLista(lista);
    }
}
