package com.ecogrid.mapper.service;

import com.ecogrid.mapper.model.LinhaDeTransmissao;
import com.ecogrid.mapper.model.Subestacao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AnalisadorConectividadeService {

    private final GrafoService grafoService;
    private final AreaProtegidaService areaProtegidaService;

    public Map<Integer, List<Subestacao>> encontrarComponentesConexos(){
        Set<Subestacao> visitados = new HashSet<>();
        Map<Integer, List<Subestacao>> componentesConexas = new HashMap<>();
        int id = 0;

        for(Subestacao sub : grafoService.getTodasSubestacoes()){
            if(!visitados.contains(sub)){
                List<Subestacao> componente = new ArrayList<>();
                dfs(sub, visitados, componente);
                componentesConexas.put(id, componente);
                id++;
            }
        }

        return componentesConexas;

    }

    public void dfs(Subestacao atual, Set<Subestacao> visitados, List<Subestacao> componente){
        visitados.add(atual);
        componente.add(atual);

        for(LinhaDeTransmissao linha : grafoService.getLinhasDaSubestacao(atual)){
            Subestacao vizinho = grafoService.getVizinho(atual, linha);
            if(vizinho == null) continue;
            if(!visitados.contains(vizinho) && !areaProtegidaService.verificarInterseccao(atual, vizinho)){
                dfs(vizinho, visitados, componente);
            }
        }
    }

    public boolean verificarConectividade(Subestacao a, Subestacao b) {
        if (a == null || b == null) return false;

        Map<Integer, List<Subestacao>> componentes = encontrarComponentesConexos();

        for (List<Subestacao> componente : componentes.values()) {
            if (componente.contains(a) && componente.contains(b)) {
                return true;
            }
        }

        return false;

    }

    public List<Subestacao> encontrarParMaisProximo(List<Subestacao> componenteA, List<Subestacao> componenteB){

        if (componenteA.isEmpty() || componenteB.isEmpty()) {
            return Collections.emptyList();
        }

        double menorDistancia = Double.MAX_VALUE;
        Subestacao melhorA = null;
        Subestacao melhorB = null;

        for(Subestacao a : componenteA){
            for(Subestacao b : componenteB){
               double distancia = grafoService.calcularDistancia(a.getCoordenadas(), b.getCoordenadas());
               if(distancia < menorDistancia && !areaProtegidaService.verificarInterseccao(a, b)){
                   menorDistancia = distancia;
                   melhorA = a;
                   melhorB = b;
               }
            }
        }

        if (melhorA != null && melhorB != null) {
            return Arrays.asList(melhorA, melhorB);
        }

        return Collections.emptyList();

    }

}
