package com.ecogrid.mapper;

import com.ecogrid.mapper.service.GrafoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EcogridMapperApplication implements CommandLineRunner {

	private final GrafoService grafoService;

    public EcogridMapperApplication(GrafoService grafoService){
        this.grafoService = grafoService;
    }

    public static void main(String[] args){
        SpringApplication.run(EcogridMapperApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Iniciando carregamento do grafo!");

        String caminhoSubestacoes = "src/main/resources/data/subestacoes-data.csv";
        String caminhoLinhasDeTransmissao = "src/main/resources/data/linhas-de-transmissao-data.csv";

        grafoService.carregarGrafoCompleto(caminhoSubestacoes, caminhoLinhasDeTransmissao);

        System.out.println("Grafo carregado...");
        System.out.println("Qtd de Subestações: " + grafoService.getQtdSubestacoes());
        System.out.println("Qtd de linhas: " + grafoService.getQtdLinhas());
    }

}
