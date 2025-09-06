package com.ecogrid.mapper.service;

import com.ecogrid.mapper.model.AreaProtegida;
import com.ecogrid.mapper.model.Subestacao;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @RequiredArgsConstructor
public class AreaProtegidaService {

    private List<AreaProtegida> mapaAreasProtegidas;
    private final LeitorShapefileService leitorShapefileService;

    public void carregarAreasProtegidas(String caminho){
        mapaAreasProtegidas = leitorShapefileService.lerAreasProtegidas(caminho);
    }

    public boolean verificarInterseccao(Subestacao a, Subestacao b){

        if (a == null || b == null || a.getCoordenadas() == null || b.getCoordenadas() == null) {
            return false;
        }

        GeometryFactory gf = new GeometryFactory();
        Coordinate coordA = new Coordinate(a.getCoordenadas().getX(), a.getCoordenadas().getY());
        Coordinate coordB = new Coordinate(b.getCoordenadas().getX(), a.getCoordenadas().getY());

        LineString ligacao = gf.createLineString(new Coordinate[]{coordA, coordB});

        for(AreaProtegida area : mapaAreasProtegidas){
            if(area.getArea() != null && ligacao.intersects(area.getArea())){
                return true;
            }
        }

        return false;

    }

}
