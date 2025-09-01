package com.ecogrid.mapper.service;

import com.ecogrid.mapper.model.Grafo;
import com.ecogrid.mapper.model.LinhaDeTransmissao;
import com.ecogrid.mapper.model.Subestacao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class GrafoService {

    private final Grafo grafo;
    private final LeitorGrafoService leitorCsv;

    public void adicionarSubestacao(Subestacao subestacao){
        grafo.getAdjacencias().putIfAbsent(subestacao, new ArrayList<>());
    }

    public void removerSubestacao(Subestacao subestacao){
        if(!grafo.getAdjacencias().containsKey(subestacao)){
            return;
        }
        List<LinhaDeTransmissao> linhasSubestacao = new ArrayList<>(grafo.getAdjacencias().get(subestacao));
        for(LinhaDeTransmissao linha : linhasSubestacao){
            removerLinhaDeTransmissao(linha);
        }
        grafo.getAdjacencias().remove(subestacao);
    }

    public void removerLinhaDeTransmissao(LinhaDeTransmissao linha){
        Subestacao subestacaoA = linha.getSubestacaoA();
        Subestacao subestacaoB = linha.getSubestacaoB();

        List<LinhaDeTransmissao> listaA = grafo.getAdjacencias().get(subestacaoA);
        List<LinhaDeTransmissao> listaB = grafo.getAdjacencias().get(subestacaoB);

        if(listaA != null){ listaA.remove(linha); }
        if(listaB != null){ listaB.remove(linha); }
    }

    public void adicionarLinhaDeTransmissao(Subestacao subestacaoA, Subestacao subestacaoB, LinhaDeTransmissao linha){
        adicionarSubestacao(subestacaoA);
        adicionarSubestacao(subestacaoB);
        grafo.getAdjacencias().get(subestacaoA).add(linha);
        grafo.getAdjacencias().get(subestacaoB).add(linha);
    }

    public void carregarSubestacoes(String caminho){
        List<Subestacao> subestacaos = leitorCsv.lerSubestacoes(caminho);

        for(Subestacao subestacao : subestacaos){
            adicionarSubestacao(subestacao);
        }
    }

    public void carregarLinhasDeTransmissao(String caminho){
        Map<String, Subestacao> subestacoes = grafo.getAdjacencias().keySet().stream().filter(s -> s.getIdInstalacao() != null && !s.getIdInstalacao().isEmpty()).collect(Collectors.toMap(Subestacao::getIdInstalacao, Function.identity(), (existing, replacement) -> existing));
        List<LinhaDeTransmissao> linhas = leitorCsv.lerLinhasDeTransissao(caminho, subestacoes);

        for(LinhaDeTransmissao linha : linhas){
            Subestacao subA = linha.getSubestacaoA();
            Subestacao subB = linha.getSubestacaoB();
            adicionarLinhaDeTransmissao(subA, subB, linha);
        }
    }

    public void carregarGrafoCompleto(String caminhoSubestacoes, String caminhoLinhasDeTransmissao){
        carregarSubestacoes(caminhoSubestacoes);
        carregarLinhasDeTransmissao(caminhoLinhasDeTransmissao);
    }

    public int getQtdSubestacoes() {
        return grafo.getAdjacencias().size();
    }

    public int getQtdLinhas() {
        return (int) grafo.getAdjacencias().values().stream()
                .flatMap(List::stream)
                .distinct()
                .count();
    }

}
