package com.ecogrid.mapper.service;

import com.ecogrid.mapper.model.LinhaDeTransmissao;
import com.ecogrid.mapper.model.Subestacao;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LeitorGrafoService {

    public List<Subestacao> lerSubestacoes(String caminho){
        List<Subestacao> subestacoes = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(caminho))){
            String linha;
            String[] cabecalhos = null;
            boolean primeiraLinha = true;

            while((linha = br.readLine()) != null){
                if(primeiraLinha){
                    cabecalhos = linha.split(";");
                    primeiraLinha = false;
                    continue;
                }

                String[] valores = linha.split(";");
                Subestacao subestacao = criarSubestacaoCsv(cabecalhos, valores);
                subestacoes.add(subestacao);
            }
        } catch (IOException e){
            throw new RuntimeException("Erro ao ler arquivo CSV: " + caminho, e);
        }

        return subestacoes;

    }

    private Subestacao criarSubestacaoCsv(String[] cabecalhos, String[] valores){
        Subestacao subestacao = new Subestacao();

        for(int i = 0; i < cabecalhos.length; i++){
            String cabecalho = cabecalhos[i].trim().toLowerCase();
            String valor = valores[i].trim();

            if(valor.isEmpty()) continue;

            switch (cabecalho){
                case "id da instalação":
                    subestacao.setIdInstalacao(valor);
                    break;
                case "nome":
                    subestacao.setNome(valor);
                    break;
                case "id agente principal":
                    subestacao.setIdAgentePrincipal(valor);
                    break;
                case "agente principal":
                    subestacao.setAgentePrincipal(valor);
                    break;
                case "data entrada,x,y":
                    tratarCoordenadas(subestacao, valor);
                    break;
                default:
                    break;
            }
        }
        return subestacao;
    }

    private void tratarCoordenadas(Subestacao subestacao, String valor){
        try {
            String[] coordenadas = valor.split(",");
            if(coordenadas.length >= 3){
                String x = coordenadas[1].trim();
                String y = coordenadas[2].trim();

                double longitude = Double.parseDouble(x);
                double latitude = Double.parseDouble(y);

                GeometryFactory gf  = new GeometryFactory();
                Coordinate coordinate = new Coordinate(longitude, latitude);
                Point ponto = gf.createPoint(coordinate);

                subestacao.setCoordenadas(ponto);

            }
        } catch (NumberFormatException e) {
            System.err.println("Erro ao converter coordenadas: " + valor + " - " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro ao processar coordenadas: " + valor + " - " + e.getMessage());
        }
    }

    public List<LinhaDeTransmissao> lerLinhasDeTransissao(String caminho, Map<String, Subestacao> subestacoes){
        List<LinhaDeTransmissao> linhas = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(caminho))){
            String linha;
            String[] cabecalhos = null;
            boolean primeiraLinha = true;

            while((linha = br.readLine()) != null){
                if(primeiraLinha){
                    cabecalhos = linha.split(";");
                    primeiraLinha = false;
                    continue;
                }

                String[] valores = linha.split(";");
                LinhaDeTransmissao linhaDeTransmissao = criarLinhaDeTransmissaoCsv(cabecalhos, valores, subestacoes);

                if(linhaDeTransmissao != null){
                    linhas.add(linhaDeTransmissao);
                }

            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler arquivo CSV: " + caminho, e);
        }
        return linhas;
    }

    private LinhaDeTransmissao criarLinhaDeTransmissaoCsv(String[] cabecalhos, String[] valores, Map<String, Subestacao> subestacoes){
        LinhaDeTransmissao linha = new LinhaDeTransmissao();
        String idSubestacaoA = null;
        String idSubestacaoB = null;

        for(int i = 0; i < cabecalhos.length && i < valores.length; i++){
            String cabecalho = cabecalhos[i].trim().toLowerCase();
            String valor = valores[i].trim();

            if(valor.isEmpty()) continue;

            try {
                switch (cabecalho){
                    case "id do equipamento":
                        linha.setIdEquipamento(valor);
                        break;
                    case "agente proprietário":
                        linha.setProprietario(valor);
                        break;
                    case "capacidade cpst longa sem limitação (a)":
                        linha.setCSPT_longa_sL(Double.parseDouble(valor));
                        break;
                    case "capacidade cpst curta sem limitação (a)":
                        linha.setCSPT_curta_sl(Double.parseDouble(valor));
                        break;
                    case "capacidade cpst longa com limitação (a)":
                        linha.setCSPT_longa_cL(Double.parseDouble(valor));
                        break;
                    case "capacidade cpst curta com limitação (a)":
                        linha.setCSPT_curta_cL(Double.parseDouble(valor));
                        break;
                    case "resistência (%)":
                        linha.setResistencia(Double.parseDouble(valor));
                        break;
                    case "reatância fixa (%)":
                        linha.setReatancia(Double.parseDouble(valor));
                        break;
                    case "comprimento (Km)":
                        linha.setComprimento(Double.parseDouble(valor));
                        break;
                    case "tensão":
                        linha.setTensao(Double.parseDouble(valor));
                        break;
                    default:
                        break;
                }
            } catch (NumberFormatException e){
                System.err.println("Erro ao converter valor numérico para " + cabecalho + ": " + valor);
            }
        }

        if(idSubestacaoA != null && idSubestacaoB != null){
            Subestacao subA = subestacoes.get(idSubestacaoA);
            Subestacao subB = subestacoes.get(idSubestacaoB);

            if(subA != null && subB != null){
                linha.setSubestacaoA(subA);
                linha.setSubestacaoB(subB);
                return linha;
            } else {
                System.err.println("Subestações não encontradas: " + idSubestacaoA + " e/ou " + idSubestacaoB);
                return null;
            }
        }
        return null;
    }

}
