package com.estudomais.demo.persistence;

import com.estudomais.demo.model.Disciplina;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DisciplinaDAO {
    private static final String FILE_NAME = "data/disciplinas.dat";

    public static void salvarDisciplina(Disciplina nova) throws IOException {
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

    public static List<Disciplina> listarDisciplinas() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (List<Disciplina>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void salvarLista(List<Disciplina> lista) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(lista);
        }
    }

    public static void removerPorCodigo(String codigo) throws IOException {
        List<Disciplina> lista = listarDisciplinas();
        lista.removeIf(d -> d.getCodigo().equalsIgnoreCase(codigo));
        salvarLista(lista);
    }
}
