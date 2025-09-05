package com.ecogrid.mapper.service;

import com.ecogrid.mapper.model.LinhaDeTransmissao;
import com.ecogrid.mapper.model.Subestacao;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RoteadorService {

    //Algoritmo A*

    private final GrafoService grafoService;
    private static final double maximo = 1_000_000_000.0;

    public List<Subestacao> encontrarCaminhoAStar(Subestacao origem, Subestacao destino,
                                                  AreaProtegidaService areaProtegidaService) {

        Map<Subestacao, Double> custoG = new HashMap<>();
        Map<Subestacao, Subestacao> veioDe = new HashMap<>();
        Map<Subestacao, Double> fScore = new HashMap<>();
        Set<Subestacao> visitados = new HashSet<>();
        Set<Subestacao> emFila = new HashSet<>();

        PriorityQueue<Subestacao> filaPrioritaria = new PriorityQueue<>(Comparator.comparingDouble
                (sub -> custoG.getOrDefault(sub, maximo) + heuristica(sub, destino)));

        for (Subestacao sub : grafoService.getTodasSubestacoes()) {
            custoG.put(sub, maximo);
            fScore.put(sub, maximo);
        }

        custoG.put(origem, 0.0);
        fScore.put(origem, heuristica(origem, destino));
        filaPrioritaria.add(origem);
        emFila.add(origem);

        while (!filaPrioritaria.isEmpty()) {
            Subestacao atual = filaPrioritaria.poll();
            emFila.remove(atual);

            if (visitados.contains(atual)) continue;

            visitados.add(atual);

            if (atual.equals(destino)) {
                return reconstruirCaminho(veioDe, atual);
            }

            for (LinhaDeTransmissao linha : grafoService.getLinhasDaSubestacao(atual)) {
                Subestacao vizinho = grafoService.getVizinho(atual, linha);
                if (vizinho == null || visitados.contains(vizinho)) continue;

                double custoTentativa = custoG.get(atual) + calcularPeso(linha);

                if (custoTentativa < custoG.get(vizinho) &&
                        !areaProtegidaService.verificarInterseccao(atual, vizinho)) {

                    veioDe.put(vizinho, atual);
                    custoG.put(vizinho, custoTentativa);
                    fScore.put(vizinho, custoTentativa + heuristica(vizinho, destino));

                    if (!emFila.contains(vizinho)) {
                        filaPrioritaria.add(vizinho);
                        emFila.add(vizinho);
                    }
                }
            }
        }

        return Collections.emptyList();

    }

    private double heuristica(Subestacao a, Subestacao b) {
        return calcularDistancia(a.getCoordenadas(), b.getCoordenadas());
    }

    private double calcularDistancia(Point a, Point b) {
        if (a == null || b == null) {
            return Double.MAX_VALUE;
        }

        double lon1 = a.getX();
        double lat1 = a.getY();
        double lon2 = b.getX();
        double lat2 = b.getY();

        double deltaLonKm = (lon2 - lon1) * 111.32 * Math.cos(Math.toRadians((lat1 + lat2) / 2));
        double deltaLatKm = (lat2 - lat1) * 111.32;

        return Math.sqrt(deltaLonKm * deltaLonKm + deltaLatKm * deltaLatKm);

    }

    private List<Subestacao> reconstruirCaminho(Map<Subestacao, Subestacao> veioDe, Subestacao atual) {
        List<Subestacao> caminho = new ArrayList<>();
        caminho.add(atual);

        while (veioDe.containsKey(atual)) {
            atual = veioDe.get(atual);
            caminho.addFirst(atual);
        }
        return caminho;
    }

    public double calcularPeso(LinhaDeTransmissao linha) {
        return linha.getComprimento();
    }

}
