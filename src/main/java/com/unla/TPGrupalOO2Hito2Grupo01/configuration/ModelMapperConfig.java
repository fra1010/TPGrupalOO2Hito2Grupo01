package com.unla.TPGrupalOO2Hito2Grupo01.configuration;

import com.unla.TPGrupalOO2Hito2Grupo01.dtos.FoodTruckDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.dtos.PuestoDesarmableDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.dtos.UnidadVentaDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.FoodTruck;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.PuestoDesarmable;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.UnidadVenta;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

    @Bean("modelMapperUnidadVenta")
    public ModelMapper modelMapperUnidadVenta() {
        ModelMapper mapper = new ModelMapper();

        mapper.typeMap(UnidadVenta.class, UnidadVentaDTO.class)
                .include(FoodTruck.class, FoodTruckDTO.class)
                .include(PuestoDesarmable.class, PuestoDesarmableDTO.class);
        // El include solo copia los mapeos del padre: aca se agregan los propios de cada hija
        mapper.getTypeMap(FoodTruck.class, FoodTruckDTO.class).implicitMappings();
        mapper.getTypeMap(PuestoDesarmable.class, PuestoDesarmableDTO.class).implicitMappings();

        return mapper;
    }
}

