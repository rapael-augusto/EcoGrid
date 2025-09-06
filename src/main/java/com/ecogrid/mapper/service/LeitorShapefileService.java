package com.ecogrid.mapper.service;

import com.ecogrid.mapper.model.AreaProtegida;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.simple.SimpleFeature;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class LeitorShapefileService {

    public List<AreaProtegida> lerAreasProtegidas(String caminho){
        List<AreaProtegida> areasProtegidas = new ArrayList<>();

        try {
            File arquivo = new File(caminho);
            if(!arquivo.exists()) {
                throw new RuntimeException("Arquivo (" + caminho +") não encontrado!");
            }

            System.setProperty("org.geotools.referencing.forceXY", "true");
            ShapefileDataStore dataStore = new ShapefileDataStore(arquivo.toURI().toURL());
            dataStore.setCharset(StandardCharsets.UTF_8);

            SimpleFeatureSource featureSource = dataStore.getFeatureSource();
            SimpleFeatureCollection collection = featureSource.getFeatures();

            try(SimpleFeatureIterator i = collection.features()){
                while(i.hasNext()){
                    SimpleFeature feature = i.next();
                    AreaProtegida area = criarAreaProtegidaShp(feature);
                    if(area != null){
                        areasProtegidas.add(area);
                    }
                }
            }
            dataStore.dispose();
        } catch (IOException e){
            throw new RuntimeException("Erro ao ler ("+ caminho +") Shapefile!", e);
        } catch (Exception e){
            throw new RuntimeException("Erro ao processar (" + caminho +") Shapefile!", e);
        }

        return areasProtegidas;
    }

    private AreaProtegida criarAreaProtegidaShp(SimpleFeature feature){
        try {
            AreaProtegida area = new AreaProtegida();

            extrairAtributos(feature, area);
            extrairGeometria(feature, area);

            return area;
        } catch (Exception e) {
            System.out.println("Erro ao processar feature: "+ e.getMessage());
            return null;
        }
    }

    private void extrairAtributos(SimpleFeature feature, AreaProtegida area){

        Set<String> estados = Set.of("BA", "AL", "CE", "MA", "PB", "PE", "PI", "RN", "SE");
        String UFabrangente = pegarValorString(feature, "UFAbrang");

        if (UFabrangente != null) {
            for (String estado : estados) {
                if (UFabrangente.contains(estado)) {
                    area.setUf(estado);
                    String valorNome = pegarValorString(feature, "NomeUC");
                    area.setNome(valorNome == null ? "" : valorNome);
                    Double valorArea = pegarValorDouble(feature, "AreaHaAlb");
                    area.setMedidaArea(valorArea != null ? valorArea : 0);
                    break;
                }
            }
        }

    }

    private void extrairGeometria(SimpleFeature feature, AreaProtegida area){
        Object geometria = feature.getDefaultGeometry();
        if(geometria instanceof Polygon){
            area.setArea((Polygon) geometria);
        } else {
            org.locationtech.jts.geom.MultiPolygon multiPoligono = (org.locationtech.jts.geom.MultiPolygon) geometria;
            if(multiPoligono.getNumGeometries() > 0){
                area.setArea((Polygon) multiPoligono.getGeometryN(0));
            }
        }
    }

    private String pegarValorString(SimpleFeature feature, String nome){

        Object valor = feature.getAttribute(nome);
        if(valor != null){
            return valor.toString();
        }
        return null;

    }

    private Double pegarValorDouble(SimpleFeature feature, String nome){
        Object valor = feature.getAttribute(nome);
        if(valor != null){
            if(valor instanceof Number){
                return ((Number) valor).doubleValue();
            }
        }
        return null;
    }

}
