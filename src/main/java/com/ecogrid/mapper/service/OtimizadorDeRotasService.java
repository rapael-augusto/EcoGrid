package com.ecogrid.mapper.service;

import com.ecogrid.mapper.model.Subestacao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OtimizadorDeRotasService {

    private final GrafoService grafoService;
    private final AreaProtegidaService areaProtegidaService;

    public List<Subestacao> encontrarMelhorias(List<Subestacao> rota){

        if(rota == null || rota.size() < 3){
            return rota;
        }

        List<Subestacao> rotaMelhorada = new ArrayList<>(rota);
        boolean flagMelhoria = true;

        while(flagMelhoria){
            flagMelhoria = false;

            for(int i = 0; i < rotaMelhorada.size(); i++){
                for(int j = i + 2; j < rotaMelhorada.size(); j++){
                    Subestacao subA = rotaMelhorada.get(i);
                    Subestacao subB = rotaMelhorada.get(j);

                    if(!areaProtegidaService.verificarInterseccao(subA, subB)){
                        double economia = calcularEconomia(rotaMelhorada, i, j);

                        if(economia > 5.0){
                            aplicarAtalho(rotaMelhorada, i, j);
                            flagMelhoria = true;
                            break;
                        }
                    }
                }
                if(flagMelhoria) break;
            }
        }

        return rotaMelhorada;

    }

    private double calcularEconomia(List<Subestacao> rota, int inicio, int fim){
        double distanciaAtual = 0;

        for(int i = 0; i < rota.size(); i++){
            distanciaAtual += grafoService.calcularDistancia(rota.get(i).getCoordenadas(),
                    rota.get(i +1).getCoordenadas());
        }

        double distanciaAtalho = grafoService.calcularDistancia(rota.get(inicio).getCoordenadas(),
                rota.get(fim).getCoordenadas());

        return distanciaAtual - distanciaAtalho;

    }

    private void aplicarAtalho(List<Subestacao> rota, int inicio, int fim){
        for(int i = 0; i < fim; i++){
            rota.remove(inicio + 1);
        }
    }

}
